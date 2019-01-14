package network;

import model.QualityIndex;
import model.Sensor;
import model.SensorData;
import model.Station;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IRestService {
    CompletableFuture<List<Station>> getStations();
    CompletableFuture<List<Sensor>> getSensors(Long stationID);
    CompletableFuture<List<SensorData>> getSensorData(Sensor sensor);
    CompletableFuture<List<QualityIndex>> getIndexes(Long stationID);
}
