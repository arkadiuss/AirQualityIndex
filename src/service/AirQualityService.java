package service;

import cache.ICacheService;
import kotlin.Unit;
import model.QualityIndex;
import model.Sensor;
import model.SensorData;
import model.Station;
import network.IRestService;

import java.util.List;
import java.util.Optional;
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
            callback.accept(stations);
            return Unit.INSTANCE;
        });
    }

    public void getSensors(Long stationId, Consumer<List<Sensor>> callback){
        restService.getSensors(stationId, sensors -> {
            cacheService.save("sensors", sensors);
            callback.accept(sensors);
            return Unit.INSTANCE;
        });
    }

    public void getSensorData(Sensor sensor, Consumer<List<SensorData>> callback){
        restService.getSensorData(sensor, sensorData -> {
            cacheService.save("sensorData", sensorData);
            callback.accept(sensorData);
            return Unit.INSTANCE;
        });
    }

    public void getIndexes(Long stationId, Consumer<List<QualityIndex>> callback){
        restService.getIndexes(stationId, indexes -> {
            cacheService.save("indexes", indexes);
            callback.accept(indexes);
            return Unit.INSTANCE;
        });
    }

    public void getCurrentIndexForStation(String station, Consumer<List<QualityIndex>> callback){
        getStations(stations -> {
            Optional<Station> cs = stations.stream().filter(s -> s.getName().contains(station)).findFirst();
            cs.ifPresent(st -> getIndexes(st.getId(), callback));
        });
    }
}