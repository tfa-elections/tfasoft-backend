package com.tfa.elections.tfasoft

import grails.gorm.annotation.Entity
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes='username')
@ToString(includes='username', includeNames=true, includePackage=false)
@Entity
class User {

    String username
    String email
    String passwordHash
    boolean enabled = true
    boolean accountLocked = false
    Date dateCreated
    Date lastUpdated

    static hasOne = [fairProfile: FAIRProfile]

    enum RoleType { ORDINARY, FAIR, ADMIN_LEVEL_1, ADMIN_LEVEL_2, AUDITOR }
    RoleType role = RoleType.ORDINARY

    static constraints = {
        username blank: false, unique: true
        email blank: false, email: true, unique: true
        passwordHash blank: false
        role nullable: false
    }

    static mapping = {
        passwordHash column: 'password_hash'
    }
}
