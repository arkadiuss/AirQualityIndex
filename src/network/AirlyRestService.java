package network;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import model.Sensor;
import model.SensorData;
import model.Station;
import network.model.Airly.StationAirlyResponse;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class AirlyRestService implements IRestService {

    private String url = "https://airapi.airly.eu/v2/";
    private String apiKey = "tX152DHenh3FKr2aLFbuYuJgsxNDa9wc";
    private Map<String, String> headers = new HashMap<>();
    private ExecutorService executor = Executors.newFixedThreadPool(4);
    public AirlyRestService(){
        headers.put("apikey", apiKey);
    }

    @Override
    public void getStations(Function1<List<Station>, Unit> callback) {
        executor.execute(() -> {
            StationAirlyResponse[] stations = HttpServiceKt
                    .httpGet(url + "installations/nearest?lat=51.0647&lng=19.9450&&maxDistanceKM=500&maxResults=100",
                            StationAirlyResponse[].class, headers);
            if(stations == null)
                callback.invoke(new ArrayList<>());
            else
                callback.invoke(
                    Arrays.stream(stations)
                            .map(StationAirlyResponse::map)
                            .collect(Collectors.toList())
            );
        });
    }

    @Override
    public void getSensors(Long stationID, Function1<List<Sensor>, Unit> callback) {

    }

    @Override
    public void getSensorData(Long sensorID, Function1<List<SensorData>, Unit> callback) {

    }
}
