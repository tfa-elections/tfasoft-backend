package com.tfa.elections.tfasoft.alert

import com.tfa.elections.tfasoft.ResultEntry
import com.tfa.elections.tfasoft.User

import grails.gorm.annotation.Entity

@Entity
class SuspicionReport {

    User reportedBy
    ResultEntry resultEntry
    String reason
    Date dateCreated = new Date()

    static constraints = {
        reason blank: false, minSize: 10
        reportedBy unique: ['resultEntry']
    }
}
