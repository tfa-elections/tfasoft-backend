package com.tfa.elections.tfasoft

import grails.gorm.transactions.Transactional

@Transactional(readOnly = true)
class PollingStationService {

    List<String> listRegions() {
        PollingStation.executeQuery("select distinct region from PollingStation order by region")
    }

    List<String> listDivisions(String region) {
        PollingStation.executeQuery("select distinct division from PollingStation where region = :region order by division", [region: region])
    }

    List<String> listSubdivisions(String region, String division) {
        PollingStation.executeQuery("select distinct subdivision from PollingStation where region = :region and division = :division order by subdivision", [region: region, division: division])
    }

    List<String> listCenters(String region, String division, String subdivision) {
        PollingStation.executeQuery("select distinct centerCode from PollingStation where region = :region and division = :division and subdivision = :subdivision order by centerCode", [region: region, division: division, subdivision: subdivision])
    }

    List<PollingStation> listPollingStations(String region, String division, String subdivision, String centerCode) {
        PollingStation.findAllByRegionAndDivisionAndSubdivisionAndCenterCode(region, division, subdivision, centerCode)
    }

    PollingStation getByCode(String code) {
        PollingStation.findByCode(code)
    }
}
