package com.tfa.elections.tfasoft

import grails.gorm.transactions.Transactional

@Transactional(readOnly = true)
class ResultAggregationService {

    def grailsApplication

    Map<String, Object> summarizeByZone(String region = null, String division = null, String subdivision = null, String center = null) {
        def criteria = ResultEntry.createCriteria()

        def entries = criteria.list {
            pollingStationCode {
                if (region) ilike("region", region)
                if (division) ilike("division", division)
                if (subdivision) ilike("subdivision", subdivision)
                if (center) ilike("centerCode", center)
            }
        }

        Map<String, Integer> totalVotes = [:].withDefault { 0 }
        int totalBallots = 0

        entries.each { entry ->
            entry.resultsPerCandidate.each { party, votes ->
                totalVotes[party] += votes
                totalBallots += votes
            }
        }

        def top3 = totalVotes.sort { -it.value }.take(3)
        def remaining = totalVotes.findAll { !(it.key in top3.keySet()) }

        Map<String, Object> response = [
                totalBallots: totalBallots,
                topCandidates: top3.collect { [name: it.key, votes: it.value, percentage: totalBallots != 0 ? ((it.value * 100.0) / totalBallots).round(2) : 0] },
                otherCandidates: remaining.collect { [name: it.key, votes: it.value] },
                otherTotal: remaining.values().sum()
        ]

        return response
    }

    List<Map<String, Object>> breakdownOtherCandidates(String region = null, String division = null, String subdivision = null, String center = null) {
        def criteria = ResultEntry.createCriteria()

        def entries = criteria.list {
            pollingStationCode {
                if (region) ilike("region", region)
                if (division) ilike("division", division)
                if (subdivision) ilike("subdivision", subdivision)
                if (center) ilike("centerCode", center)
            }
        }

        Map<String, Integer> totalVotes = [:].withDefault { 0 }

        entries.each { entry ->
            entry.resultsPerCandidate.each { party, votes ->
                totalVotes[party] += votes
            }
        }

        def top3Keys = totalVotes.sort { -it.value }.take(3).keySet()
        def otherVotes = totalVotes.findAll { !(it.key in top3Keys) }

        return otherVotes.collect { [name: it.key, votes: it.value] }
    }
}
