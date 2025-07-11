package com.tfa.elections.tfasoft.alert

import com.tfa.elections.tfasoft.ResultEntry
import com.tfa.elections.tfasoft.User
import grails.rest.*
import grails.converters.*
import grails.rest.RestfulController
import org.springframework.http.HttpStatus

class ResultReviewController {

    static responseFormats = ['json']

    def submitSuspicion() {
        def user = User.findByUsername(request.getAttribute("authenticatedUsername"))
        def resultId = params.long("resultId")
        def reason = request.JSON.reason

        if (!user || !reason || reason.length() < 10) {
            respond([error: 'Reason must be at least 10 characters'], status: HttpStatus.BAD_REQUEST)
            return
        }

        def result = ResultEntry.get(resultId)
        if (!result) {
            respond([error: 'Result entry not found'], status: HttpStatus.NOT_FOUND)
            return
        }

        if (SuspicionReport.findByReportedByAndResultEntry(user, result)) {
            respond([error: 'You already submitted a report for this entry'], status: HttpStatus.CONFLICT)
            return
        }

        new SuspicionReport(reportedBy: user, resultEntry: result, reason: reason).save(flush: true)
        result.isSuspicious = true
        result.save(flush: true)

        respond([message: 'Suspicion report recorded'], status: HttpStatus.CREATED)
    }

    def markAsRejectionCandidate() {
        def user = User.findByUsername(request.getAttribute("authenticatedUsername"))
        def result = ResultEntry.get(params.long("resultId"))

        if (!user || !result || user.role != User.RoleType.ADMIN_LEVEL_1) {
            respond([error: 'Unauthorized'], status: HttpStatus.FORBIDDEN)
            return
        }

        if (AdminReviewAction.findByAdminAndResultEntryAndAction(user, result, AdminReviewAction.ActionType.MARK_REJECTION)) {
            respond([error: 'Already marked as rejection candidate'], status: HttpStatus.CONFLICT)
            return
        }

        new AdminReviewAction(admin: user, resultEntry: result, action: AdminReviewAction.ActionType.MARK_REJECTION).save(flush: true)
        result.isRejectionCandidate = true
        result.save(flush: true)

        respond([message: 'Marked as rejection candidate'], status: HttpStatus.OK)
    }

    def markAsRemovable() {
        def user = User.findByUsername(request.getAttribute("authenticatedUsername"))
        def result = ResultEntry.get(params.long("resultId"))

        if (!user || !result || user.role != User.RoleType.ADMIN_LEVEL_2) {
            respond([error: 'Unauthorized'], status: HttpStatus.FORBIDDEN)
            return
        }

        if (AdminReviewAction.findByAdminAndResultEntryAndAction(user, result, AdminReviewAction.ActionType.MARK_REMOVABLE)) {
            respond([error: 'Already marked as removable'], status: HttpStatus.CONFLICT)
            return
        }

        new AdminReviewAction(admin: user, resultEntry: result, action: AdminReviewAction.ActionType.MARK_REMOVABLE).save(flush: true)

        def totalAdmin2 = User.countByRole(User.RoleType.ADMIN_LEVEL_2)
        def markedCount = AdminReviewAction.countByResultEntryAndAction(result, AdminReviewAction.ActionType.MARK_REMOVABLE)

        if (totalAdmin2 > 0 && markedCount / totalAdmin2 > 0.5) {
            result.includeInConsensus = false
            result.save(flush: true)
        }

        respond([message: 'Marked as removable', consensusStatusUpdated: !result.includeInConsensus], status: HttpStatus.OK)
    }
}
