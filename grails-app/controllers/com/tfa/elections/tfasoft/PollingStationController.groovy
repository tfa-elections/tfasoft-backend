package com.tfa.elections.tfasoft


import grails.rest.*
import grails.converters.*
import grails.rest.RestfulController
import org.springframework.http.HttpStatus

class PollingStationController {

    static responseFormats = ['json']
    PollingStationService pollingStationService

    def listRegions() {
        respond pollingStationService.listRegions()
    }

    def listDivisions() {
        respond pollingStationService.listDivisions(params.region)
    }

    def listSubdivisions() {
        respond pollingStationService.listSubdivisions(params.region, params.division)
    }

    def listCenters() {
        respond pollingStationService.listCenters(params.region, params.division, params.subdivision)
    }

    def listStations() {
        respond pollingStationService.listPollingStations(params.region, params.division, params.subdivision, params.centerCode)
    }

    def getByCode() {
        def station = pollingStationService.getByCode(params.code)
        if (!station) {
            respond([error: "Not found"], status: HttpStatus.NOT_FOUND)
        } else {
            respond station
        }
    }
}
