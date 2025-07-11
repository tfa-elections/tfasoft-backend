package com.tfa.elections.tfasoft


import grails.rest.*
import grails.converters.*

import grails.rest.RestfulController
import org.springframework.http.HttpStatus

class ResultController {

    static responseFormats = ['json']
    ResultService resultService

    def submit() {
        def json = request.JSON
        def user = User.findByUsername(request.getAttribute("authenticatedUsername"))

        if (!user) {
            respond([error: 'Unauthorized'], status: HttpStatus.UNAUTHORIZED)
            return
        }

        if (!json.pollingStationCode || !json.resultsPerCandidate) {
            respond([error: 'Missing pollingStationCode or resultsPerCandidate'], status: HttpStatus.BAD_REQUEST)
            return
        }

        try {
            def result = resultService.submitResult(user, json.pollingStationCode, json.resultsPerCandidate)
            respond([id: result.id], status: HttpStatus.CREATED)
        } catch (IllegalStateException e) {
            respond([error: e.message], status: HttpStatus.CONFLICT)
        }
    }

    def consensus() {
        def code = params.pollingStationCode
        def status = resultService.getConsensus(code)
        if (!status) {
            respond([error: 'Polling station not found'], status: HttpStatus.NOT_FOUND)
        } else {
            respond status
        }
    }

    def submissionStatus() {
        def user = User.findByUsername(request.getAttribute("authenticatedUsername"))

        if (!user) {
            respond([error: 'Unauthorized'], status: HttpStatus.UNAUTHORIZED)
            return
        }

        def entry = ResultEntry.findBySubmittedBy(user)
        def hasSubmitted = entry != null

        respond([
                username: user.username,
                hasSubmitted: hasSubmitted,
                pollingStationCode: entry?.pollingStationCode,
                submissionTime: entry?.submissionTime
        ], status: HttpStatus.OK)
    }
}
