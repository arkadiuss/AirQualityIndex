package network

import kotlinx.coroutines.Deferred
import model.QualityIndex
import model.Sensor
import model.SensorData
import model.Station

/**
 * Class that enable to download data from external services
 */
interface IRestService {
    /**
     * Method that download stations from external service
     * @return list of stations
     */
    fun getStations(): Deferred<List<Station>>

    /**
     * Method that download sensors from external service
     * @param stationID id of station
     * @return list of sensors
     */
    fun getSensors(stationID: Long): Deferred<List<Sensor>>

    /**
     * Method that download sensors' data from external service
     * @param sensor sensor to download
     * @return list of sensor data
     */
    fun getSensorData(sensor: Sensor): Deferred<List<SensorData>>

    /**
     * Method that download indices from external service
     * @param stationID id of station
     * @return list of indices
     */
    fun getIndexes(stationID: Long): Deferred<List<QualityIndex>>
}
