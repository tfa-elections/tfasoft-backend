package com.tfa.elections.tfasoft.audit

import com.tfa.elections.tfasoft.User
import grails.gorm.annotation.Entity
import groovy.transform.ToString

@Entity
@ToString(includeNames = true, includePackage = false)
class ResultAuditLog {

    User submittedBy
    String pollingStationCode
    Map<String, Integer> resultsSnapshot = [:]
    Date submissionTime = new Date()
    String clientIp
    String deviceId
    boolean isFairUser = false
    String userRole
    String username
    String fairProfileCode

    static constraints = {
        pollingStationCode blank: false
        resultsSnapshot validator: { val ->
            return val instanceof Map && !val.isEmpty()
        }
        clientIp nullable: true
        deviceId nullable: true
        username blank: false
        userRole blank: false
        fairProfileCode nullable: true
    }
}
