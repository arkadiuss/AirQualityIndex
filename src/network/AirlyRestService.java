package network;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import model.Sensor;
import model.SensorData;
import model.Station;

import java.util.List;

public class AirlyRestService implements IRestService {

    @Override
    public void getStations(Function1<List<Station>, Unit> callback) {

    }

    @Override
    public void getSensors(Long stationID, Function1<List<Sensor>, Unit> callback) {

    }

    @Override
    public void getSensorData(Long sensorID, Function1<List<SensorData>, Unit> callback) {

    }
}
