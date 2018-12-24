package service;

import cache.ICacheService;
import network.IRestService;

public class AirQualityService {

    private final IRestService restService;
    private final ICacheService cacheService;

    public AirQualityService(IRestService restService, ICacheService cacheService){
        this.restService = restService;
        this.cacheService = cacheService;
    }

}