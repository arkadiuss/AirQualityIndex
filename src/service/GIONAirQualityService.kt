package service

import model.Station
import model.mapper.StationMapper
import network.GIONRestService

class GIONAirQualityService: AirQualityService {

    val restService: GIONRestService = GIONRestService()
    val mapper: StationMapper = StationMapper()

    override fun getStations(): List<Station> {
        return restService.getStations()?.map { it -> mapper.map(it) }?: emptyList()
    }
}