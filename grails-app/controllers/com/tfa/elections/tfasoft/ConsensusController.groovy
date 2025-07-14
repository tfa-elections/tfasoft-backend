package com.tfa.elections.tfasoft


import grails.rest.*
import grails.converters.*
import grails.rest.RestfulController
import org.springframework.http.HttpStatus

class ConsensusController {

    static responseFormats = ['json']
    ConsensusService consensusService

    def agreement() {
        def code = params.pollingStationCode
        if (!code) {
            respond([error: 'Missing pollingStationCode'], status: HttpStatus.BAD_REQUEST)
            return
        }

        def report = consensusService.computeConsensusAgreement(code)
        respond report
    }

    def submissions() {
        def code = params.pollingStationCode
        if (!code) {
            respond([error: 'Missing pollingStationCode'], status: HttpStatus.BAD_REQUEST)
            return
        }

        def deltas = consensusService.listSubmissionsWithDeltas(code)
        respond deltas
    }
}