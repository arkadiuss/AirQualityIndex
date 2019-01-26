package service.airquality

import model.QualityIndex
import model.Sensor
import model.SensorData
import model.Station
import java.time.LocalDateTime
import java.util.*

/**
 * Class that have a methods to process  and analyze data from service
 */
interface IAirQualityService {
    /**
     * Method that obtains current indices for given station
     *
     * @param stationName substring of station name
     * @return index for a station
     */
    suspend fun getCurrentIndexForStation(stationName: String): List<QualityIndex>

    /**
     * Method that obtains value of parameter for given arguments
     *
     * @param stationName  substring of station name
     * @param sensorName substring of sensor name
     * @param date the nearest date
     * @return data from sensor for given arguments
     */
    suspend fun getSensorDataForStationAndDate(
        stationName: String,
        sensorName: String,
        date: LocalDateTime
    ): Triple<Station, Sensor, SensorData?>

    /**
     * Method that counts an average for sensor for given period
     *
     * @param stationName substring of station name
     * @param sensorName substring of sensor name
     * @param startDate beginning of the period
     * @param endDate end of the period
     * @return Average from given
     */
    suspend fun getAverageForStationAndSensor(
        stationName: String,
        sensorName: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Pair<Station, Double>

    /**
     * Method that counts most unstable parameter
     *
     * @param stationsNames substrings of stations
     * @param startDate date that we start counting
     * @return Most unstable parameter
     */
    suspend fun getMostUnstableParameter(
        stationsNames: Array<String>,
        startDate: LocalDateTime
    ): Pair<Sensor, Double>

    /**
     * Method that counts parameter with minimum value for given date
     *
     * @param date date to obtain data
     * @return sensor for Minimal parameter
     */
    suspend fun getMinimalParameter(date: LocalDateTime): Pair<Sensor, Double>

    /**
     * Method that obtains parameter that exceed the norm
     *
     * @param stationName station for parameters
     * @param date date for data
     * @return list of parameters that exceeded the norm
     */
    suspend fun getExceededParamsForStation(
        stationName: String,
        date: LocalDateTime
    ): List<Triple<Station, Sensor, SensorData?>>

    /**
     * Method that obtain minimum and maximum value for given sensor
     *
     * @param sensorName name of the sensor
     * @return Minimum and maximum value for given sensor
     */
    suspend fun minMaxForParameter(sensorName: String): Pair<Triple<Station, Sensor, DoubleSummaryStatistics>, Triple<Station, Sensor, DoubleSummaryStatistics>>

    /**
     * Method that obtain data for station from given period
     *
     * @param stationsNames stations to download data
     * @param sensorName sensor to obtain data from
     * @param startDate beginning of the period
     * @param endDate end of the period
     * @return station with sensor data for it
     */
    suspend fun getForStationsAndParam(
        stationsNames: Array<String>, sensorName: String,
        startDate: LocalDateTime, endDate: LocalDateTime
    ): List<Pair<Station, SensorData>>
}
