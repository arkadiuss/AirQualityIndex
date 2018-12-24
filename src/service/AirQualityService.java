package service;

import cache.ICacheService;
import kotlin.Unit;
import model.Station;
import network.IRestService;

import java.util.List;
import java.util.function.Consumer;

public class AirQualityService {

    private final IRestService restService;
    private final ICacheService cacheService;

    public AirQualityService(IRestService restService, ICacheService cacheService){
        this.restService = restService;
        this.cacheService = cacheService;
    }


    public void getStations(Consumer<List<Station>> callback){
        restService.getStations(stations -> {
            cacheService.save("stations", stations);
            callback.accept((List<Station>) cacheService.read("stations"));
            return Unit.INSTANCE;
        });
    }
}