package com.tfa.elections.tfasoft

import grails.gorm.transactions.Transactional

@Transactional(readOnly = true)
class ConsensusService {

    def grailsApplication

    Map computeConsensusAgreement(String pollingStationCode) {
        def entries = ResultEntry.findAllByPollingStationCodeAndIncludeInConsensus(pollingStationCode, true)
        if (!entries || entries.size() == 0) return [status: "no_data"]

        def total = entries.size()
        def tally = [:].withDefault { [] }

        entries.each { entry ->
            entry.resultsPerCandidate.each { candidate, votes ->
                tally[candidate] << votes
            }
        }

        def agreement = [:]
        def consensus = [:]

        tally.each { candidate, voteList ->
            def frequency = voteList.countBy { it }
            def (mostCommonVote, count) = frequency.max { it.value }
            def percentAgreement = (count * 100.0 / total).round(2)
            agreement[candidate] = [
                    agreedVotes: mostCommonVote,
                    agreementPercent: percentAgreement
            ]
            consensus[candidate] = mostCommonVote
        }

        return [
                pollingStationCode: pollingStationCode,
                consensusReached: ConsensusStatus.findByPollingStationCode(pollingStationCode)?.consensusReached ?: false,
                agreementMap: agreement,
                consensusResults: consensus
        ]
    }

    List<Map> listSubmissionsWithDeltas(String pollingStationCode) {
        def consensus = computeConsensusAgreement(pollingStationCode)?.consensusResults
        if (!consensus) return []

        ResultEntry.findAllByPollingStationCode(pollingStationCode).collect { entry ->
            [
                    submittedBy: entry.submittedBy.username,
                    submissionTime: entry.submissionTime,
                    delta: consensus.collectEntries { k, v ->
                        [(k): ((entry.resultsPerCandidate[k] ?: 0) as int) - ((v ?: 0) as int)]
                    },
                    includeInConsensus: entry.includeInConsensus,
                    isSuspicious: entry.isSuspicious
            ]
        }
    }
}
