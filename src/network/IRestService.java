package network;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import model.Sensor;
import model.SensorData;
import model.Station;

import java.util.List;

public interface IRestService {

    void getStations(Function1<List<Station>, Unit> callback);
    void getSensors(Long stationID, Function1<List<Sensor>, Unit> callback);
    void getSensorData(Long sensorID, Function1<List<SensorData>, Unit> callback);
}
