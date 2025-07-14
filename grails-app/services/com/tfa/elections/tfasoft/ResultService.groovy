package com.tfa.elections.tfasoft

import com.tfa.elections.tfasoft.audit.ResultAuditLog
import grails.gorm.transactions.Transactional

@Transactional
class ResultService {

    def grailsApplication

    ResultEntry submitResult(User user, String pollingStationCode, Map<String, Integer> results, String clientIp = null, String deviceId = null) {
        if (ResultEntry.findBySubmittedBy(user)) {
            throw new IllegalStateException("User has already submitted results and cannot submit again")
        }

        if (user.fairProfile && user.fairProfile.pollingStationCode != pollingStationCode) {
            throw new IllegalStateException("FAIR Users can only post results for their registered polling station")
        }

        def entry = new ResultEntry(
                submittedBy: user,
                pollingStationCode: pollingStationCode,
                resultsPerCandidate: results,
                includeInConsensus: true
        )
        entry.save(flush: true)

        // Audit logging
        new ResultAuditLog(
                submittedBy: user,
                pollingStationCode: pollingStationCode,
                resultsSnapshot: results,
                submissionTime: new Date(),
                clientIp: clientIp,
                deviceId: deviceId
        ).save(flush: true)

        updateConsensus(pollingStationCode)
        return entry
    }

    void updateConsensus(String pollingStationCode) {
        def allEntries = ResultEntry.findAllByPollingStationCodeAndIncludeInConsensus(pollingStationCode, true)
        def total = allEntries.size()
        if (total == 0) return

        def tally = [:].withDefault { 0 }

        allEntries.each { entry ->
            entry.resultsPerCandidate.each { candidate, votes ->
                tally[candidate] += votes
            }
        }

        def averaged = [:]
        tally.each { candidate, totalVotes ->
            averaged[candidate] = (int) (totalVotes / total)
        }

        def poolThreshold = grailsApplication.config.tfasoft.consensus.poolConsensusLevel ?: 0.7
        def consensusReached = total >= (grailsApplication.config.tfasoft.consensus.minReports ?: 3)

        def status = ConsensusStatus.findByPollingStationCode(pollingStationCode) ?: new ConsensusStatus(pollingStationCode: pollingStationCode)
        status.totalSubmissions = total
        status.consensusReached = consensusReached
        status.consensusResults = consensusReached ? averaged : [:]
        status.lastUpdated = new Date()
        status.save(flush: true)
    }

    ConsensusStatus getConsensus(String pollingStationCode) {
        return ConsensusStatus.findByPollingStationCode(pollingStationCode)
    }
}
