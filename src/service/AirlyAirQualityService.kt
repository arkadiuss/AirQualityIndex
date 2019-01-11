package service

import cache.CacheService
import network.AirlyRestService

class AirlyAirQualityService(forceNetwork: Boolean = false) :
    AirQualityService(AirlyRestService(), CacheService("Airly"), forceNetwork)