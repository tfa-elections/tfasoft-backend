package com.tfa.elections.tfasoft

import grails.gorm.annotation.Entity

@Entity
class FAIRProfile {

    String deviceId // unique ID per device
    String phoneNumber
    String pollingStationCode // links to a PollingStation domain (to be created later)
    boolean verified = false
    Date registeredAt

    static belongsTo = [user: User]

    static constraints = {
        deviceId blank: false, unique: true
        phoneNumber blank: false
        pollingStationCode blank: false
    }
}
