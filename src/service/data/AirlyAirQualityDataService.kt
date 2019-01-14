package service.data

import cache.CacheService
import network.AirlyRestService
import service.data.AirQualityDataService

class AirlyAirQualityDataService(forceNetwork: Boolean = false) :
    AirQualityDataService(AirlyRestService(), CacheService("Airly"), forceNetwork)