package service.data;

import cache.ICacheService;
import model.QualityIndex;
import model.Sensor;
import model.SensorData;
import model.Station;
import network.IRestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AirQualityDataService {

    private final IRestService restService;
    private final ICacheService cacheService;
    private final Boolean forceNetwork;
    private ExecutorService executor
            = Executors.newSingleThreadExecutor();

    public AirQualityDataService(IRestService restService, ICacheService cacheService, boolean forceNetwork){
        this.restService = restService;
        this.cacheService = cacheService;
        this.forceNetwork = forceNetwork;
    }


    public CompletableFuture<List<Station>> getStations(){
        final String key = "stations";
        if(!forceNetwork && !isExpired(key)){
            return CompletableFuture.completedFuture(getFromCache(key));
        }else{
            return restService.getStations().thenCompose(stations ->
                    CompletableFuture.completedFuture(saveOrGetFromCache(key, stations)));
        }
    }

    public CompletableFuture<List<Sensor>> getSensors(Long stationId){
        final String key = "sensors";
        if(!forceNetwork && !isExpired(key)){
            return CompletableFuture.completedFuture(getFromCache(key));
        }else{
            return restService.getSensors(stationId)
                    .thenCompose(sensors ->
                            CompletableFuture.completedFuture(saveOrGetFromCache(key, sensors)));
        }
    }

    public CompletableFuture<List<SensorData>> getSensorData(Sensor sensor){
        final String key = "sensorData";
        if(!forceNetwork && !isExpired(key)){
            return CompletableFuture.completedFuture(getFromCache(key));
        }else{
            return restService.getSensorData(sensor).thenCompose(sensorData ->
                    CompletableFuture.completedFuture(saveOrGetFromCache(key, sensorData)));
        }
    }

    public CompletableFuture<List<QualityIndex>> getIndexes(Long stationId){
        final String key = "indices";
        if(!forceNetwork && !isExpired(key)){
            return CompletableFuture.completedFuture(getFromCache(key));
        }else{
            return restService.getIndexes(stationId).thenCompose(qualityIndices ->
                    CompletableFuture.completedFuture(saveOrGetFromCache(key,qualityIndices)));
        }
    }



    private <T> T getFromCache(String key){
        showCacheInfo(key);
        Optional<T> fromCache = cacheService.read(key).map(o -> (T) o);
        return fromCache.orElse(null);
    }

    private <T> T saveOrGetFromCache(String key, T data){
        if(data == null){
            showDownloadError(key);
            return (T) cacheService.read(key).get();
        }
        cacheService.save(key, data);
        return data;
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