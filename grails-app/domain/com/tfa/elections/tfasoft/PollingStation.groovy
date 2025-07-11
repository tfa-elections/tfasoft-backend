package com.tfa.elections.tfasoft

import grails.gorm.annotation.Entity
import groovy.transform.ToString

@Entity
@ToString(includeNames = true, includePackage = false)
class PollingStation {

    String code // e.g., CM-AD001-01-001
    String name // optional station name or description
    String region
    String division
    String subdivision
    String centerCode // group of polling stations in the same voting center
    Integer registeredVoters = 0
    boolean active = true

    static constraints = {
        code blank: false, unique: true
        name nullable: true
        region blank: false
        division blank: false
        subdivision blank: false
        centerCode blank: false
        registeredVoters min: 0
    }

    static mapping = {
        sort code: 'asc'
    }
}
