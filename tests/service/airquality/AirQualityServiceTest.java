package service.airquality;

import model.QualityIndex;
import model.Sensor;
import model.SensorData;
import model.Station;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import service.data.AirQualityDataService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class AirQualityServiceTest {

    private AirQualityDataService dataService = Mockito.mock(AirQualityDataService.class);
    private AirQualityService airQualityService = new AirQualityService(dataService);

    @Before
    public void before(){
        List<Station> stations = Arrays.asList(
                new Station(0,"test1", "ul Bujaka"),
                new Station(1,"test2", "ul NieBujka")
        );
        List<Sensor> sensors = Arrays.asList(
                new Sensor(0,0,"PM10"),
                new Sensor(1,0,"PM15"),
                new Sensor(2,0,"PM3"),
                new Sensor(3,1,"PM10"),
                new Sensor(4,1,"PM2.5")
        );
        List<SensorData> sensorData0 = Arrays.asList(
                new SensorData("PM10", LocalDateTime.now().minus(1, ChronoUnit.HOURS),1),
                new SensorData("PM10", LocalDateTime.now().minus(2, ChronoUnit.HOURS),2),
                new SensorData("PM10", LocalDateTime.now().minus(3, ChronoUnit.HOURS),3)
        );
        List<SensorData> sensorData1 = Arrays.asList(
                new SensorData("PM15", LocalDateTime.now().minus(1, ChronoUnit.HOURS),10),
                new SensorData("PM15", LocalDateTime.now().minus(2, ChronoUnit.HOURS),20),
                new SensorData("PM15", LocalDateTime.now().minus(3, ChronoUnit.HOURS),30)
        );
        List<SensorData> sensorData2 = Arrays.asList(
                new SensorData("PM3", LocalDateTime.now().minus(1, ChronoUnit.HOURS),0.1),
                new SensorData("PM3", LocalDateTime.now().minus(2, ChronoUnit.HOURS),20),
                new SensorData("PM3", LocalDateTime.now().minus(3, ChronoUnit.HOURS),4.3)
        );
        List<SensorData> sensorData3 = Arrays.asList(
                new SensorData("PM10", LocalDateTime.now().minus(1, ChronoUnit.HOURS),5),
                new SensorData("PM10", LocalDateTime.now().minus(2, ChronoUnit.HOURS),2),
                new SensorData("PM10", LocalDateTime.now().minus(3, ChronoUnit.HOURS),3)
        );
        List<SensorData> sensorData4 = Arrays.asList(
                new SensorData("PM2.5", LocalDateTime.now().minus(1, ChronoUnit.HOURS),1),
                new SensorData("PM2.5", LocalDateTime.now().minus(2, ChronoUnit.HOURS),4),
                new SensorData("PM2.5", LocalDateTime.now().minus(3, ChronoUnit.HOURS),3)
        );
        List<QualityIndex> indices = Arrays.asList(
                new QualityIndex("st", LocalDateTime.now().minus(1, ChronoUnit.HOURS),"Good"),
                new QualityIndex("NO2", LocalDateTime.now().minus(1, ChronoUnit.HOURS),"Bad")
        );
        Mockito.when(dataService.getStations())
                .thenReturn(CompletableFuture.completedFuture(stations));
        Mockito.when(dataService.getSensors(0L))
                .thenReturn(CompletableFuture.completedFuture(sensors.stream()
                        .filter(sensor -> sensor.getStationId()==0).collect(Collectors.toList())));
        Mockito.when(dataService.getSensors(1L))
                .thenReturn(CompletableFuture.completedFuture(sensors.stream()
                        .filter(sensor -> sensor.getStationId()==1).collect(Collectors.toList())));
        Mockito.when(dataService.getSensorData(sensors.get(0)))
                .thenReturn(CompletableFuture.completedFuture(sensorData0));
        Mockito.when(dataService.getSensorData(sensors.get(1)))
                .thenReturn(CompletableFuture.completedFuture(sensorData1));
        Mockito.when(dataService.getSensorData(sensors.get(2)))
                .thenReturn(CompletableFuture.completedFuture(sensorData2));
        Mockito.when(dataService.getSensorData(sensors.get(3)))
                .thenReturn(CompletableFuture.completedFuture(sensorData3));
        Mockito.when(dataService.getSensorData(sensors.get(4)))
                .thenReturn(CompletableFuture.completedFuture(sensorData4));
        Mockito.when(dataService.getIndexes(0L))
                .thenReturn(CompletableFuture.completedFuture(indices));
    }

    @Test
    public void getCurrentIndexForStation() {
        airQualityService.getCurrentIndexForStation("test1")
                .thenAccept(qualityIndices -> {
                    assertEquals(2, qualityIndices.size());
                    assertEquals("Good", qualityIndices.get(0).getLevel());
                    assertEquals("Bad", qualityIndices.get(1).getLevel());
                }).join();
    }

    @Test
    public void getSensorDataForStationAndDate() {
        airQualityService.getSensorDataForStationAndDate("test1", "PM10",
                LocalDateTime.now().minusHours(2)).thenAccept(stationSensorDataPair -> {
                    assertEquals(2., stationSensorDataPair.getSecond().getValue(), 0.01);
        }).join();
    }

    @Test
    public void getAverageForStationAndSensor() {
        airQualityService.getAverageForStationAndSensor("test1", "PM10",
                LocalDateTime.now().minusHours(2).minusMinutes(2), LocalDateTime.now())
                .thenAccept(stationSensorDataPair -> {
                    assertEquals(1.5, stationSensorDataPair.getSecond(), 0.01);
                }).join();
    }

    @Test
    public void getMostUnstableParameter() {
        airQualityService.getMostUnstableParameter(new String[]{"test1", "test2"},LocalDateTime.now().minusHours(5))
                .thenAccept(sensorDoublePair -> {
                    assertEquals("PM15",sensorDoublePair.getFirst().getName());
                    assertEquals(20.,sensorDoublePair.getSecond(), 0.01);
                }).join();
    }

    @Test
    public void getMinimalParameter() {
        airQualityService.getMinimalParameter(LocalDateTime.now().minusHours(2))
                .thenAccept(sensorDoublePair -> {
                    assertEquals("PM10",sensorDoublePair.getFirst().getName());
                    assertEquals(2.,sensorDoublePair.getSecond(), 0.01);
                }).join();
    }


    @Test
    public void minMaxForParameter() {
        airQualityService.minMaxForParameter("PM10")
                .thenAccept(tripleTriplePair -> {
                    assertEquals(1, tripleTriplePair.getFirst().getThird().getMin(), 0.01);
                    assertEquals(3, tripleTriplePair.getFirst().getThird().getMax(), 0.01);
                }).join();
    }

}