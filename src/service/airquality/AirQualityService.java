package service.airquality;

import exception.NotFoundException;
import kotlin.Pair;
import kotlin.Triple;
import model.QualityIndex;
import model.Sensor;
import model.SensorData;
import model.Station;
import service.data.AirQualityDataService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * {@inheritDoc}
 *
 * Implementation using stream interface. It turns out that is not always the good solution to use only streams
 */
public class AirQualityService implements IAirQualityService {

    private final AirQualityDataService airQualityDataService;

    public AirQualityService(AirQualityDataService airQualityDataService) {
        this.airQualityDataService = airQualityDataService;
    }

    /**
     * {@inheritDoc}
     */
    public CompletableFuture<List<QualityIndex>> getCurrentIndexForStation(String stationName){
        return getStationByName(stationName)
                .thenCompose(station -> airQualityDataService.getIndexes(station.getId()));
    }

    /**
     * {@inheritDoc}
     */
    public CompletableFuture<Pair<Station, SensorData>> getSensorDataForStationAndDate(String stationName, String sensorName,
                                                                                       LocalDateTime date){
        return getStationByName(stationName).thenCompose(station ->
                getSensorByName(station.getId(),sensorName).thenCompose(sensor ->
                        airQualityDataService.getSensorData(sensor).thenApply(sensorData ->
                            sensorData.stream()
                                    .min((o1, o2) ->
                                            (int) (secondsDiff(o1.getDate(), date) - secondsDiff(o2.getDate(),date)))
                                    .map(sensorEntry -> new Pair<>(station, sensorEntry)).get())));
    }

    /**
     * {@inheritDoc}
     */
    public CompletableFuture<Pair<Station, Double>> getAverageForStationAndSensor(String stationName, String sensorName,
                                                                                  LocalDateTime startDate, LocalDateTime endDate){
        return getStationByName(stationName).thenCompose(station ->
                getSensorByName(station.getId(),sensorName).thenCompose(sensor ->
                        airQualityDataService.getSensorData(sensor).thenApply(sensorData -> {
                            Double avg = sensorData.stream()
                                    .filter(sensorEntry ->
                                            sensorEntry.getDate().isAfter(startDate) &&
                                                    sensorEntry.getDate().isBefore(endDate))
                                    .mapToDouble(SensorData::getValue)
                                    .average().orElse(0);
                            return new Pair<>(station, avg);
                        })));
    }

    /**
     * {@inheritDoc}
     */
    public CompletableFuture<Pair<Sensor, Double>> getMostUnstableParameter(String[] stationsNames, LocalDateTime startDate){
        return airQualityDataService.getStations()
                .thenApplyAsync(stations -> stations.stream()
                            .filter(station -> Arrays.stream(stationsNames)
                                    .anyMatch(name -> station.getName().contains(name)))
                            .map(station -> airQualityDataService.getSensors(station.getId()).join())
                            .reduce((sensors, sensors2) -> {
                                sensors.addAll(sensors2);
                                return sensors;
                            }).orElseThrow(() -> new NotFoundException("No data")))
                .thenApplyAsync(sensors ->
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
                            .orElseThrow(() -> new NotFoundException("No data")));
    }

    /**
     * {@inheritDoc}
     */
    public CompletableFuture<Pair<Sensor, Double>> getMinimalParameter(LocalDateTime date){
        return airQualityDataService.getStations()
                .thenApplyAsync(stations ->
                        stations.stream()
                                .map(station ->
                                        new Pair<>(station, airQualityDataService.getSensors(station.getId()).join()))
                                .flatMap(sensors ->
                                        sensors.component2().stream()
                                            .map(sensor -> new Pair<>(sensors.component1(), sensor)))
                                .collect(Collectors.toList()))
                .thenApplyAsync(sensors ->
                        sensors.stream()
                                .map(sensor ->
                                        new Triple<>(sensor.component1(), sensor.component2(),
                                        airQualityDataService.getSensorData(sensor.component2()).join()))
                                .map(sensorData ->
                                        new Triple<>( sensorData.component1(), sensorData.component2(),
                                        sensorData.component3().stream()
                                        .min((o1, o2) ->
                                                (int) (secondsDiff(o1.getDate(), date) - secondsDiff(o2.getDate(), date)))
                                        .orElseThrow(() -> new NotFoundException("No data for "+sensorData.component2().getName()))))

                                .min((o1, o2) -> (int) (o1.component3().getValue() - o2.component3().getValue()))
                                .map(val -> new Pair<>(val.getSecond(), val.getThird().getValue())).get());
    }

    /**
     * {@inheritDoc}
     */
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
                                                .orElseThrow(() -> new NotFoundException("No data for "+stationName))
                                    ))
                            .filter(sensorData ->
                                    sensorData.component3().getValue() > limit.get(sensorData.component2().getName()))
                            .collect(Collectors.toList())
                );
    }

    /**
     * {@inheritDoc}
     */
    public CompletableFuture<Pair<Triple<Station, Sensor, DoubleSummaryStatistics>,Triple<Station, Sensor, DoubleSummaryStatistics>>> minMaxForParameter(String sensorName){
        return airQualityDataService.getStations()
                .thenApplyAsync(stations -> stations.stream()
                            .map(station -> new Pair<>(station, airQualityDataService.getSensors(station.getId()).join()))
                            .map(stationListPair ->
                                     new Pair<>(stationListPair.component1(),
                                            stationListPair.component2().stream()
                                                    .filter(sensor -> sensor.getName().contains(sensorName))
                                                    .findFirst().orElse(null)))
                            .filter(stationSensorPair -> stationSensorPair.getSecond() != null)
                            .collect(Collectors.toList()))
                .thenApplyAsync(data ->
                        data.stream()
                            .map(pair -> new Triple<>(pair.getFirst(),
                                            pair.getSecond(),
                                            airQualityDataService.getSensorData(pair.getSecond()).join()))
                            .map(t -> new Triple<>(t.getFirst(), t.getSecond(),
                                    t.getThird().stream().mapToDouble(SensorData::getValue).summaryStatistics()))
                            .collect(Collectors.toList()))
                .thenApply(data -> {
                        Triple<Station, Sensor, DoubleSummaryStatistics> min = data.stream()
                                        .min((o1, o2) -> (int) (o1.getThird().getMin() - o2.getThird().getMin()))
                                        .orElseThrow(() -> new NotFoundException("No data for "+sensorName));
                        Triple<Station, Sensor, DoubleSummaryStatistics> max = data.stream()
                                        .max((o1, o2) -> (int) (o1.getThird().getMax() - o2.getThird().getMax()))
                                        .orElseThrow(() -> new NotFoundException("No data for "+sensorName));
                        return new Pair<>(min, max);
                });
    }

    /**
     * {@inheritDoc}
     */
    public CompletableFuture<List<Pair<Station, SensorData>>> getForStationsAndParam(String[] stationsNames, String sensorName,
                                                                                     LocalDateTime startDate, LocalDateTime endDate){
        return airQualityDataService.getStations()
                .thenApplyAsync(stations ->
                        stations.stream()
                                .filter(station -> Arrays.stream(stationsNames)
                                .anyMatch(name -> station.getName().contains(name)))
                                .map(station -> new Pair<>(station, getSensorByName(station.getId(),sensorName).join()))
                                .collect(Collectors.toList()))
                .thenApplyAsync(data ->
                        data.stream()
                            .map(p -> new Pair<>(p.getFirst(), airQualityDataService.getSensorData(p.getSecond()).join()))
                            .flatMap(p ->
                                    p.getSecond().stream()
                                        .map(sensorData -> new Pair<>(p.getFirst(), sensorData)))
                            .filter(p -> p.getSecond().getDate().isAfter(startDate) &&
                                    p.getSecond().getDate().isBefore(endDate))
                            .collect(Collectors.toList()));
    }

    /**
     * Get stations and filter given one
     * @param stationName name to filter by
     * @return station filtered
     */
    private CompletableFuture<Station> getStationByName(String stationName) {
        return airQualityDataService.getStations().thenApply(stations ->
                stations.stream()
                        .filter(s -> s.getName().contains(stationName))
                        .findFirst().orElseThrow(() -> new NotFoundException("Station "+stationName+" couldn't be found")));
    }

    /**
     * Get sensors and filter given one
     * @param sensorName name to filter by
     * @return sensors filtered
     */
    private CompletableFuture<Sensor> getSensorByName(Long stationId, String sensorName){
        return airQualityDataService.getSensors(stationId).thenCompose(sensors ->
            CompletableFuture.completedFuture(sensors.stream()
                    .filter(s -> s.getName().contains(sensorName))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Sensor "+sensorName+" couldn't be found"))));
    }

    private Long secondsDiff(LocalDateTime date1, LocalDateTime date2){
        return Math.abs(date1.until(date2, ChronoUnit.SECONDS));
    }
}
