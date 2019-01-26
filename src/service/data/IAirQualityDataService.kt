package service.data

import kotlinx.coroutines.Deferred
import model.QualityIndex
import model.Sensor
import model.SensorData
import model.Station

/**
 * Class that is responsible for managing data from different sources
 */
interface IAirQualityDataService {

    /**
     * Method that download stations from sources
     *
     * @return list of stations
     */
    fun getStations(): Deferred<List<Station>>

    /**
     * Method that download sensor for station from sources
     *
     * @return list of sensor
     */
    fun getSensors(stationId: Long): Deferred<List<Sensor>>

    /**
     * Method that download sensor data for sensor from sources
     *
     * @return list of sensordata
     */
    fun getSensorData(sensor: Sensor): Deferred<List<SensorData>>

    /**
     * Method that download indices for given station from sources
     *
     * @return list of indices
     */
    fun getIndexes(stationId: Long): Deferred<List<QualityIndex>>

}
