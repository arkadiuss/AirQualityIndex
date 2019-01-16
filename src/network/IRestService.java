package network;

import model.QualityIndex;
import model.Sensor;
import model.SensorData;
import model.Station;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Class that enable to download data from external services
 */
public interface IRestService {
    /**
     * Method that download stations from external service
     * @return list of stations
     */
    CompletableFuture<List<Station>> getStations();
    /**
     * Method that download sensors from external service
     * @param stationID id of station
     * @return list of sensors
     */
    CompletableFuture<List<Sensor>> getSensors(Long stationID);
    /**
     * Method that download sensors' data from external service
     * @param sensor sensor to download
     * @return list of sensor data
     */
    CompletableFuture<List<SensorData>> getSensorData(Sensor sensor);
    /**
     * Method that download indices from external service
     * @param stationID id of station
     * @return list of indices
     */
    CompletableFuture<List<QualityIndex>> getIndexes(Long stationID);
}
