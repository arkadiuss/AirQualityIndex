package service.airquality;

import kotlin.Pair;
import kotlin.Triple;
import model.QualityIndex;
import model.Sensor;
import model.SensorData;
import model.Station;

import java.time.LocalDateTime;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.concurrent.CompletableFuture;
public interface IAirQualityService {
    CompletableFuture<List<QualityIndex>> getCurrentIndexForStation(String stationName);
    CompletableFuture<Pair<Station, SensorData>> getSensorDataForStationAndDate(String stationName, String sensorName, LocalDateTime date);
    CompletableFuture<Pair<Station, Double>> getAverageForStationAndSensor(String stationName, String sensorName, LocalDateTime startDate, LocalDateTime endDate);
    CompletableFuture<Pair<Sensor, Double>> getMostUnstableParameter(String[] stationsNames, LocalDateTime startDate);
    CompletableFuture<Pair<Sensor, Double>> getMinimalParameter(LocalDateTime date);
    CompletableFuture<List<Triple<Station, Sensor, SensorData>>> getExceededParamsForStation(String stationName, LocalDateTime date);
    CompletableFuture<Pair<Triple<Station, Sensor, DoubleSummaryStatistics>,Triple<Station, Sensor, DoubleSummaryStatistics>>> minMaxForParameter(String sensorName);
    CompletableFuture<List<Pair<Station, SensorData>>> getForStationsAndParam(String[] stationsNames, String sensorName,
                                                                                     LocalDateTime startDate, LocalDateTime endDate);
}
