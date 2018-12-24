package service

import cache.CacheService
import model.Station
import network.GIONRestService

class GIONAirQualityService: AirQualityService(GIONRestService(), CacheService("GION"))