package network;

import model.QualityIndex;
import model.Sensor;
import model.SensorData;
import model.Station;
import network.model.Airly.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class AirlyRestService implements IRestService {

    private String url = "https://airapi.airly.eu/v2/";
    private String apiKey = "tX152DHenh3FKr2aLFbuYuJgsxNDa9wc";
    private Map<String, String> headers = new HashMap<>();
    private ExecutorService executor = Executors.newFixedThreadPool(4);
    public AirlyRestService(){
        headers.put("apikey", apiKey);
    }

    @Override
    public CompletableFuture<List<Station>> getStations() {
        return CompletableFuture.supplyAsync(() -> {
            StationAirlyResponse[] stations = HttpServiceKt
                    .httpGet(url + "installations/nearest?lat=50.0647&lng=19.9450&&maxDistanceKM=20&maxResults=100",
                            StationAirlyResponse[].class, headers);
            List<Station> response;
            if(stations == null) {
                response = Collections.emptyList();
            }else {
                response =  Arrays.stream(stations)
                        .map(StationAirlyResponse::map)
                        .collect(Collectors.toList());
            }
            return response;
        });
    }

    @Override
    public CompletableFuture<List<Sensor>> getSensors(Long stationID) {
        return CompletableFuture.supplyAsync(() -> {
            MeasurementsAirlyResponse response = HttpServiceKt
                    .httpGet(url + "measurements/installation?installationId="+stationID,
                            MeasurementsAirlyResponse.class, headers);
            System.out.println("getted");
            SensorDataAirly[] values = response.getCurrent().getValues();
            return IntStream.range(0,values.length)
                    .mapToObj(i -> new Sensor(i, stationID, values[i].getName()))
                    .collect(Collectors.toList());
        });
    }

    @Override
    public CompletableFuture<List<SensorData>> getSensorData(Sensor sensor) {
        return CompletableFuture.supplyAsync(() -> {
            MeasurementsAirlyResponse response = HttpServiceKt
                    .httpGet(url + "measurements/installation?installationId="+sensor.getStationId(),
                            MeasurementsAirlyResponse.class, headers);
            SensorDataAirly[] curValues = response.getCurrent().getValues();
            MeasurementAirly[] histValues = response.getHistory();
            return  Stream.concat(
                            Arrays.stream(curValues),
                            Arrays.stream(histValues).map(MeasurementAirly::getValues).flatMap(Arrays::stream))
                    .filter(sensorDataAirly -> sensorDataAirly.getName().equals(sensor.getName()))
                    .map(sensorDataAirly -> new SensorData(sensorDataAirly.getName(),
                            response.getCurrent().getTillDateTime(),
                            sensorDataAirly.getValue()))
                    .collect(Collectors.toList());
        });
    }

    @Override
    public CompletableFuture<List<QualityIndex>> getIndexes(Long stationID) {
        return CompletableFuture.supplyAsync(() -> {
            MeasurementsAirlyResponse response = HttpServiceKt
                    .httpGet(url + "measurements/installation?installationId="+stationID,
                            MeasurementsAirlyResponse.class, headers);
            IndexAirly[] indexes = response.getCurrent().getIndexes();
            return Arrays.stream(indexes)
                    .map(indexAirly -> {
                        QualityIndex.Builder builder = new QualityIndex.Builder();
                        builder.setDate(response.getCurrent().getFromDateTime());
                        builder.setLevel(indexAirly.getLevel());
                        builder.setName(indexAirly.getName());
                        return builder.build();
                    })
                    .collect(Collectors.toList());
        });
    }
}
