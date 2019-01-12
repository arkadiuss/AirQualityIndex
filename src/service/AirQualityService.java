package service;

import cache.ICacheService;
import kotlin.Unit;
import model.QualityIndex;
import model.Sensor;
import model.SensorData;
import model.Station;
import network.IRestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class AirQualityService {

    private final IRestService restService;
    private final ICacheService cacheService;
    private final Boolean forceNetwork;

    public AirQualityService(IRestService restService, ICacheService cacheService, boolean forceNetwork){
        this.restService = restService;
        this.cacheService = cacheService;
        this.forceNetwork = forceNetwork;
    }


    public void getStations(Consumer<List<Station>> callback){
        final String key = "stations";
        if(!forceNetwork && !isExpired(key)){
            getFromCache(key, callback);
        }else{
            restService.getStations(stations -> {
                saveAndAccept(key, callback, stations);
                return Unit.INSTANCE;
            });
        }    }

    public void getSensors(Long stationId, Consumer<List<Sensor>> callback){
        final String key = "sensors";
        if(!forceNetwork && !isExpired(key)){
            getFromCache(key, callback);
        }else{
            restService.getSensors(stationId, sensors -> {
                saveAndAccept(key, callback, sensors);
                return Unit.INSTANCE;
            });
        }
    }

    public void getSensorData(Sensor sensor, Consumer<List<SensorData>> callback){
        final String key = "sensorData";
        if(!forceNetwork && !isExpired(key)){
            getFromCache(key, callback);
        }else{
            restService.getSensorData(sensor, sensorData -> {
                saveAndAccept(key, callback, sensorData);
                return Unit.INSTANCE;
            });
        }
    }

    public void getIndexes(Long stationId, Consumer<List<QualityIndex>> callback){
        final String key = "indices";
        if(!forceNetwork && !isExpired(key)){
            getFromCache(key, callback);
        }else{
            restService.getIndexes(stationId, qualityIndices -> {
                saveAndAccept(key, callback, qualityIndices);
                return Unit.INSTANCE;
            });
        }
    }

    public void getCurrentIndexForStation(String station, Consumer<List<QualityIndex>> callback){
        getStations(stations -> {
            Optional<Station> cs = stations.stream()
                    .filter(s -> s.getName().contains(station))
                    .findFirst();
            cs.ifPresent(st -> getIndexes(st.getId(), callback));
        });
    }

    public void getSensorDataForStationAndDate(String stationName, String sensorName,
                                               ServiceResponse<Station, List<SensorData>> callback){
        getStations(stations -> {
            stations.stream()
                    .filter(s -> s.getName().contains(stationName))
                    .findFirst()
                    .ifPresent(st ->{
                        getSensors(st.getId(),sensors -> {
                            sensors.stream()
                                    .filter(sensor -> sensor.getName().contains(sensorName))
                                    .findFirst()
                                    .ifPresent(sensor -> {
                                        getSensorData(sensor, sensorData -> callback.onResponse(st, sensorData));
                                    });
                        });
                    });
        });
    }

    private <T> void getFromCache(String key, Consumer<T> callback){
        showCacheInfo(key);
        Optional<T> fromCache = cacheService.read(key).map(o -> (T) o);
        fromCache.ifPresent(callback);
    }

    private <T> void saveAndAccept(String key, Consumer<T> callback, T data){
        if(data == null){
            showDownloadError(key);
            callback.accept((T) cacheService.read(key));
        }
        cacheService.save(key, data);
        callback.accept((T) data);
    }

    private boolean isExpired(String key){
        return !cacheService.getLasyModificationOfFile(key).plusHours(1).isAfter(LocalDateTime.now());
    }

    private void showDownloadError(String key){
        System.out.println("Unable to download, getting "+key+" from cache");
    }

    private void showCacheInfo(String key){
        System.out.println("Data is younger than 1 hour, getting "+key+" from cache");
    }


}