package service.data

import cache.CacheService
import network.GIONRestService
import service.data.AirQualityDataService

class GIONAirQualityDataService(forceNetwork: Boolean = false):
    AirQualityDataService(GIONRestService(), CacheService("GION"), forceNetwork)