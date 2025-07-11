package com.tfa.elections.tfasoft.alert

import com.tfa.elections.tfasoft.ResultEntry
import com.tfa.elections.tfasoft.User
import grails.gorm.annotation.Entity

@Entity
class AdminReviewAction {

    User admin
    ResultEntry resultEntry

    enum ActionType {
        MARK_REJECTION, MARK_REMOVABLE
    }

    ActionType action
    Date dateCreated = new Date()

    static constraints = {
        admin unique: ['resultEntry', 'action']
        action nullable: false
    }
}
