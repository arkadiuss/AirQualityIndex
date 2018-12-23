package service

import model.Station
import network.GIONRestService

class GIONAirQualityService: AirQualityService {

    val restService: GIONRestService = GIONRestService()

    override fun getStations(): List<Station> {
        return listOf();//restService.getStations()?.map { it -> mapper.map(it) }?: emptyList()
    }
}