package network;

import model.Sensor;
import model.SensorData;
import model.Station;

import java.util.List;

public interface RestService {

    void getStations(Callback<List<Station>> callback);
    void getSensor(Long stationID, Callback<List<Sensor>> callback);
    void getStations(Long sensorID, Callback<List<SensorData>> callback);
}
