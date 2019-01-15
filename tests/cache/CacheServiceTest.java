package cache;

import model.Sensor;
import model.Station;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CacheServiceTest {

    private CacheService cacheService = new CacheService("test");

    @Test
    public void saveAndReadStationsTest(){
        List<Station> stations = Arrays.asList(
                new Station(0,"test0", "123"),
                new Station(1,"test1", "123")
        );
        cacheService.save("stations", stations);
        List<Station> readed = ((List<Station>) cacheService.read("stations").get());
        assertEquals(stations.size(), readed.size());
        assertEquals(stations.get(0).getId(), readed.get(0).getId());
    }

    @Test
    public void saveAndReadSensorsTest(){
        List<Sensor> sensors0= Arrays.asList(
                new Sensor(0,0, "123"),
                new Sensor(1,0, "1234")
        );
        List<Sensor> sensors1= Arrays.asList(
                new Sensor(2,1, "123"),
                new Sensor(3,1, "1234")
        );
        cacheService.save("sensors0", sensors0);
        cacheService.save("sensors1", sensors0);
        List<Sensor> readed = ((List<Sensor>) cacheService.read("sensors0").get());
        assertEquals(sensors0.size(), readed.size());
        assertEquals(sensors0.get(0).getId(), readed.get(0).getId());
        assertEquals(sensors0.get(1).getId(), readed.get(1).getId());
    }
}
