package service

import cache.CacheService
import network.AirlyRestService

class AirlyAirQualityService : AirQualityService(AirlyRestService(), CacheService("Airly"))