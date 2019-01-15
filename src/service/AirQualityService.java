package service;

import kotlin.Pair;
import kotlin.Triple;
import model.QualityIndex;
import model.Sensor;
import model.SensorData;
import model.Station;
import service.data.AirQualityDataService;
import service.response.ServiceResponse2;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AirQualityService {

    private final AirQualityDataService airQualityDataService;

    public AirQualityService(AirQualityDataService airQualityDataService) {
        this.airQualityDataService = airQualityDataService;
    }

    public void getCurrentIndexForStation(String stationName, Consumer<List<QualityIndex>> callback){
        getStationByName(stationName)
                .thenCompose(station -> airQualityDataService.getIndexes(station.getId()))
                .thenAccept(callback);
    }

    public void getSensorDataForStationAndDate(String stationName, String sensorName, LocalDateTime date,
                                               ServiceResponse2<Station, SensorData> callback){
        getStationByName(stationName).thenCompose(station ->
                getSensorByName(station.getId(),sensorName).thenCompose(sensor ->
                        airQualityDataService.getSensorData(sensor).thenAccept(sensorData -> {
                            sensorData.stream()
                                    .min((o1, o2) ->
                                            (int) (secondsDiff(o1.getDate(), date) - secondsDiff(o2.getDate(),date)))
                                    .ifPresent(sensorEntry -> callback.onResponse(station, sensorEntry));

                        })));
    }

    public void getAverageForStationAndSensor(String stationName, String sensorName,
                                              LocalDateTime startDate, LocalDateTime endDate,
                                              ServiceResponse2<Station, Double> callback){
        getStationByName(stationName).thenCompose(station ->
                getSensorByName(station.getId(),sensorName).thenCompose(sensor ->
                        airQualityDataService.getSensorData(sensor).thenAccept(sensorData -> {
                            Double avg = sensorData.stream()
                                    .filter(sensorEntry ->
                                            sensorEntry.getDate().isAfter(startDate) &&
                                                    sensorEntry.getDate().isBefore(endDate))
                                    .mapToDouble(SensorData::getValue)
                                    .average().orElse(0);
                            callback.onResponse(station, avg);
                        })));
    }

    public void getMostUnstableParameter(String[] stationsNames, LocalDateTime startDate,
                                         ServiceResponse2<Sensor, Double> callback){
        airQualityDataService.getStations()
                .thenApplyAsync(stations ->{
                                System.out.println(Arrays.toString(stations.stream().map(Station::getName).toArray()));
                    return stations.stream()
                            .filter(station -> Arrays.stream(stationsNames)
                                    .anyMatch(name -> station.getName().contains(name)))
                            .map(station -> airQualityDataService.getSensors(station.getId()).join())
                            .reduce((sensors, sensors2) -> {
                                sensors.addAll(sensors2);
                                return sensors;
                            }).get();})
                .thenAcceptAsync(sensors -> {
                        sensors.stream()
                            .map(sensor ->
                                    new Pair<>(sensor, airQualityDataService.getSensorData(sensor).join()))
                            .map(sensorData -> {
                                    DoubleSummaryStatistics filData = sensorData.component2().stream()
                                            .filter(data -> data.getDate().isAfter(startDate))
                                            .mapToDouble(SensorData::getValue)
                                            .summaryStatistics();
                                    Double min = filData.getMin();
                                    Double max = filData.getMax();
                                    return new Pair<>(sensorData.component1(), max - min);
                            })
                            .max((o1, o2) -> (int) (o1.component2() - o2.component2()))
                            .ifPresent(sensorDoublePair ->
                                    callback.onResponse(
                                            sensorDoublePair.component1(),
                                            sensorDoublePair.component2()));
                });

    }

    public void getMinimalParameter(LocalDateTime date, ServiceResponse2<Sensor, Double> callback){
        airQualityDataService.getStations()
                .thenApplyAsync(stations ->
                        stations.stream()
                                .map(station ->
                                        new Pair<>(station, airQualityDataService.getSensors(station.getId()).join()))
                                .flatMap(sensors ->
                                        sensors.component2().stream()
                                            .map(sensor -> new Pair<>(sensors.component1(), sensor)))
                                .collect(Collectors.toList()))
                .thenAcceptAsync(sensors -> {
                        sensors.stream()
                                .map(sensor ->
                                        new Triple<>(sensor.component1(), sensor.component2(),
                                        airQualityDataService.getSensorData(sensor.component2()).join()))
                                .map(sensorData ->
                                        new Triple<>( sensorData.component1(), sensorData.component2(),
                                        sensorData.component3().stream()
                                        .min((o1, o2) ->
                                                (int) (secondsDiff(o1.getDate(), date) - secondsDiff(o2.getDate(), date)))
                                        .get()))

                                .min((o1, o2) -> (int) (o1.component3().getValue() - o2.component3().getValue()))
                                .ifPresent(val -> callback.onResponse(val.component2(), val.component3().getValue()));
                });
    }

    public CompletableFuture<List<Triple<Station, Sensor, SensorData>>> getExceededParamsForStation(String stationName, LocalDateTime date){
        Map<String, Double> limit = new HashMap<>();
        limit.put("C6H6", 5.);
        limit.put("NO2", 200.);
        limit.put("SO2", 350.);
        limit.put("CO", 10000.);
        limit.put("PM10", 50.);
        limit.put("PM2.5", 25.);
        limit.put("Pb", 0.5);
        return getStationByName(stationName)
                .thenApplyAsync(station ->
                        new Pair<>(station, airQualityDataService.getSensors(station.getId()).join()))
                .thenApplyAsync(data ->
                         data.component2().stream()
                            .map(sensor ->
                                    new Pair<>(sensor, airQualityDataService.getSensorData(sensor).join()))
                            .map(sensorData ->
                                    new Triple<>(data.component1(), sensorData.component1(),
                                            sensorData.component2().stream()
                                                .min((o1, o2) ->
                                                        (int) (secondsDiff(o1.getDate(),date) - secondsDiff(o2.getDate(), date)))
                                                .get()
                                    ))
                            .filter(sensorData ->
                                    sensorData.component3().getValue() > limit.get(sensorData.component2().getName()))
                            .collect(Collectors.toList())
                );
    }

    private CompletableFuture<Station> getStationByName(String stationName) {
        return airQualityDataService.getStations().thenApply(stations ->
                stations.stream()
                        .filter(s -> s.getName().contains(stationName))
                        .findFirst().get());
    }

    private CompletableFuture<Sensor> getSensorByName(Long stationId, String sensorName){
        return airQualityDataService.getSensors(stationId).thenCompose(sensors ->
            CompletableFuture.completedFuture(sensors.stream()
                    .filter(s -> s.getName().contains(sensorName))
                    .findFirst()
                    .get()));
    }


    private Long secondsDiff(LocalDateTime date1, LocalDateTime date2){
        return Math.abs(date1.until(date2, ChronoUnit.SECONDS));
    }
}
