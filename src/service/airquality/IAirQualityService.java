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

/**
 * Class that have a methods to process  and analyze data from service
 */
public interface IAirQualityService {
    /**
     * Method that obtains current indices for given station
     *
     * @param stationName substring of station name
     * @return index for a station
     */
    CompletableFuture<List<QualityIndex>> getCurrentIndexForStation(String stationName);

    /**
     * Method that obtains value of parameter for given arguments
     *
     * @param stationName  substring of station name
     * @param sensorName substring of sensor name
     * @param date the nearest date
     * @return data from sensor for given arguments
     */
    CompletableFuture<Pair<Station, SensorData>> getSensorDataForStationAndDate(String stationName, String sensorName, LocalDateTime date);

    /**
     * Method that counts an average for sensor for given period
     *
     * @param stationName substring of station name
     * @param sensorName substring of sensor name
     * @param startDate beginning of the period
     * @param endDate end of the period
     * @return Average from given
     */
    CompletableFuture<Pair<Station, Double>> getAverageForStationAndSensor(String stationName, String sensorName, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Method that counts most unstable parameter
     *
     * @param stationsNames substrings of stations
     * @param startDate date that we start counting
     * @return Most unstable parameter
     */
    CompletableFuture<Pair<Sensor, Double>> getMostUnstableParameter(String[] stationsNames, LocalDateTime startDate);

    /**
     * Method that counts parameter with minimum value for given date
     *
     * @param date date to obtain data
     * @return sensor for Minimal parameter
     */
    CompletableFuture<Pair<Sensor, Double>> getMinimalParameter(LocalDateTime date);

    /**
     * Method that obtains parameter that exceed the norm
     *
     * @param stationName station for parameters
     * @param date date for data
     * @return list of parameters that exceeded the norm
     */
    CompletableFuture<List<Triple<Station, Sensor, SensorData>>> getExceededParamsForStation(String stationName, LocalDateTime date);

    /**
     * Method that obtain minimum and maximum value for given sensor
     *
     * @param sensorName name of the sensor
     * @return Minimum and maximum value for given sensor
     */
    CompletableFuture<Pair<Triple<Station, Sensor, DoubleSummaryStatistics>,Triple<Station, Sensor, DoubleSummaryStatistics>>>
            minMaxForParameter(String sensorName);

    /**
     * Method that obtain data for station from given period
     *
     * @param stationsNames stations to download data
     * @param sensorName sensor to obtain data from
     * @param startDate beginning of the period
     * @param endDate end of the period
     * @return station with sensor data for it
     */
    CompletableFuture<List<Pair<Station, SensorData>>> getForStationsAndParam(String[] stationsNames, String sensorName,
                                                                                     LocalDateTime startDate, LocalDateTime endDate);
}
