package network;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import model.QualityIndex;
import model.Sensor;
import model.SensorData;
import model.Station;
import network.model.Airly.IndexAirly;
import network.model.Airly.MeasurementsAirlyResponse;
import network.model.Airly.SensorDataAirly;
import network.model.Airly.StationAirlyResponse;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
                    .httpGet(url + "installations/nearest?lat=50.0647&lng=19.9450&&maxDistanceKM=20&maxResults=100",
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
        executor.execute(() -> {
            MeasurementsAirlyResponse response = HttpServiceKt
                    .httpGet(url + "measurements/installation?installationId="+stationID,
                            MeasurementsAirlyResponse.class, headers);
            System.out.println("getted");
            SensorDataAirly[] values = response.getCurrent().getValues();
            List<Sensor> sensors =  IntStream.range(0,values.length)
                    .mapToObj(i -> new Sensor(i, stationID, values[i].getName()))
                    .collect(Collectors.toList());
            callback.invoke(sensors);
        });
    }

    @Override
    public void getSensorData(Sensor sensor, Function1<List<SensorData>, Unit> callback) {
        executor.execute(() -> {
            MeasurementsAirlyResponse response = HttpServiceKt
                    .httpGet(url + "measurements/installation?installationId="+sensor.getStationId(),
                            MeasurementsAirlyResponse.class, headers);
            SensorDataAirly[] values = response.getCurrent().getValues();
            List<SensorData> sensorsData = Arrays.stream(values)
                    .filter(sensorDataAirly -> sensorDataAirly.getName().equals(sensor.getName()))
                    .map(sensorDataAirly -> new SensorData(sensorDataAirly.getName(),
                            response.getCurrent().getTillDateTime(),
                            sensorDataAirly.getValue()))
                    .collect(Collectors.toList());
            callback.invoke(sensorsData);
        });
    }

    @Override
    public void getIndexes(Long stationID, Function1<List<QualityIndex>, Unit> callback) {
        executor.execute(() -> {
            MeasurementsAirlyResponse response = HttpServiceKt
                    .httpGet(url + "measurements/installation?installationId="+stationID,
                            MeasurementsAirlyResponse.class, headers);
            IndexAirly[] indexes = response.getCurrent().getIndexes();
            List<QualityIndex> indexesList = Arrays.stream(indexes)
                    .map(indexAirly -> {
                        QualityIndex.Builder builder = new QualityIndex.Builder();
                        builder.setDate(response.getCurrent().getFromDateTime());
                        builder.setLevel(indexAirly.getLevel());
                        builder.setName(indexAirly.getName());
                        return builder.build();
                    })
                    .collect(Collectors.toList());
            callback.invoke(indexesList);
        });
    }
}
