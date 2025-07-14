package com.tfa.elections.tfasoft

import grails.gorm.annotation.Entity
import groovy.transform.ToString

@Entity
@ToString(includeNames = true, includePackage = false)
class ConsensusStatus {

    String pollingStationCode
    Map<String, Integer> consensusResults = [:]  // aggregated final results if consensus reached
    int totalSubmissions = 0
    boolean consensusReached = false
    Date lastUpdated = new Date()

    static constraints = {
        pollingStationCode blank: false, unique: true
        consensusResults nullable: true
    }
}
