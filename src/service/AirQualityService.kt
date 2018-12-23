package service

import cache.CacheService
import model.Station
import network.RestService

abstract class AirQualityService {

    abstract val restService: RestService
    abstract val cacheService: CacheService

    fun getStations(): List<Station>{

    }
}