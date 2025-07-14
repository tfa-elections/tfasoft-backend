package com.tfa.elections.tfasoft.audit

import com.tfa.elections.tfasoft.User
import grails.rest.*
import grails.converters.*
import grails.rest.RestfulController
import org.springframework.http.HttpStatus

class ResultAuditController {

    static responseFormats = ['json']

    def index() {
        def user = User.findByUsername(request.getAttribute("authenticatedUsername"))

        if (!user || !(user.role in [User.RoleType.ADMIN_LEVEL_1, User.RoleType.ADMIN_LEVEL_2, User.RoleType.AUDITOR])) {
            respond([error: 'Unauthorized access'], status: HttpStatus.FORBIDDEN)
            return
        }

        def logs

        if (params.pollingStationCode) {
            logs = ResultAuditLog.findAllByPollingStationCode(params.pollingStationCode)
        } else if (params.username) {
            logs = ResultAuditLog.findAllByUsername(params.username)
        } else {
            logs = ResultAuditLog.list(sort: 'submissionTime', order: 'desc', max: 100)
        }

        respond logs
    }
}
