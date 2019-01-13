package service;

import cache.ICacheService;
import kotlin.Unit;
import model.QualityIndex;
import model.Sensor;
import model.SensorData;
import model.Station;
import network.IRestService;
import service.response.ServiceResponse1;
import service.response.ServiceResponse2;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    public void getSensorDataForStationAndDate(String stationName, String sensorName, LocalDateTime date,
                                               ServiceResponse2<Station, SensorData> callback){
        getStationByName(stationName,station ->
            getSensorByName(station.getId(),sensorName, sensor ->
                getSensorData(sensor, sensorData -> {
                    sensorData.stream()
                            .min((o1, o2) ->
                                    (int) (secondsDiff(o1.getDate(), date) - secondsDiff(o2.getDate(),date)))
                            .ifPresent(sensorEntry -> callback.onResponse(station, sensorEntry));

                })));
    }

    public void getAverageForStationAndSensor(String stationName, String sensorName,
                                              LocalDateTime startDate, LocalDateTime endDate,
                                              ServiceResponse2<Station, Double> callback){
        getStationByName(stationName,station ->
                getSensorByName(station.getId(),sensorName, sensor ->
                        getSensorData(sensor, sensorData -> {
                            Double avg = sensorData.stream()
                                    .filter(sensorEntry ->
                                            sensorEntry.getDate().isAfter(startDate) &&
                                            sensorEntry.getDate().isBefore(endDate))
                                    .mapToDouble(SensorData::getValue)
                                    .average().orElse(0);
                            callback.onResponse(station, avg);
                        })));
    }

    private void getStationByName(String stationName, ServiceResponse1<Station> callback){
        getStations(stations -> {
            stations.stream()
                    .filter(s -> s.getName().contains(stationName))
                    .findFirst()
                    .ifPresent(callback::onResponse);
        });
    }

    private void getSensorByName(Long stationId, String sensorName, ServiceResponse1<Sensor> callback){
        getSensors(stationId, sensors -> {
            sensors.stream()
                    .filter(s -> s.getName().contains(sensorName))
                    .findFirst()
                    .ifPresent(callback::onResponse);
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

    private Long secondsDiff(LocalDateTime date1, LocalDateTime date2){
        return Math.abs(date1.until(date2, ChronoUnit.SECONDS));
    }
}