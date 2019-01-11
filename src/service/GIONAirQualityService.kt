package service

import cache.CacheService
import network.GIONRestService

class GIONAirQualityService(forceNetwork: Boolean = false):
    AirQualityService(GIONRestService(), CacheService("GION"), forceNetwork)