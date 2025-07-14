package com.tfa.elections.tfasoft


import grails.rest.*
import grails.converters.*
import grails.rest.RestfulController
import org.springframework.http.HttpStatus

class ResultAggregationController {

    static responseFormats = ['json']
    ResultAggregationService resultAggregationService

    def summary() {
        try {
            def result = resultAggregationService.summarizeByZone(
                    params.region,
                    params.division,
                    params.subdivision,
                    params.centerCode
            )
            respond result
        } catch (Exception e) {
            respond([error: e.message], status: HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    def others() {
        try {
            def result = resultAggregationService.breakdownOtherCandidates(
                    params.region,
                    params.division,
                    params.subdivision,
                    params.centerCode
            )
            respond result
        } catch (Exception e) {
            respond([error: e.message], status: HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}
