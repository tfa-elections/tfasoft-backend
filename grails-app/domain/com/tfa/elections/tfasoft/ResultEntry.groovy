package com.tfa.elections.tfasoft

import grails.gorm.annotation.Entity
import groovy.transform.ToString

@Entity
@ToString(includeNames = true, includePackage = false)
class ResultEntry {

    User submittedBy
    String pollingStationCode
    Map<String, Integer> resultsPerCandidate = [:]  // e.g., ["partyA": 102, "partyB": 98]
    boolean isValidated = false
    boolean includeInConsensus = true
    boolean isSuspicious = false
    boolean isRejectionCandidate = false
    Date submissionTime = new Date()

    static constraints = {
        pollingStationCode blank: false
        resultsPerCandidate validator: { val, obj ->
            return val instanceof Map && !val.isEmpty()
        }
    }
}
