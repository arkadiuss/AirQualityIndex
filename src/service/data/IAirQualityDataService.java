package service.data;

import model.QualityIndex;
import model.Sensor;
import model.SensorData;
import model.Station;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Class that is responsible for managing data from different sources
 */
public interface IAirQualityDataService {

    /**
     * Method that download stations from sources
     *
     * @return list of stations
     */
    CompletableFuture<List<Station>> getStations();
    /**
     * Method that download sensor for station from sources
     *
     * @return list of sensor
     */
    CompletableFuture<List<Sensor>> getSensors(Long stationId);
    /**
     * Method that download sensor data for sensor from sources
     *
     * @return list of sensordata
     */
    CompletableFuture<List<SensorData>> getSensorData(Sensor sensor);
    /**
     * Method that download indices for given station from sources
     *
     * @return list of indices
     */
    CompletableFuture<List<QualityIndex>> getIndexes(Long stationId);

}
