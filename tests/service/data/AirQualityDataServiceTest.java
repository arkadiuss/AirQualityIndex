package service.data;

import cache.CacheService;
import model.Station;
import network.GIONRestService;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AirQualityDataServiceTest {

    private CacheService cacheService = Mockito.spy(CacheService.class);
    private AirQualityDataService dataService = new AirQualityDataService(new GIONRestService(), cacheService, false);

    @Test
    public void getFromCache() {
        Mockito.when(cacheService.getLasyModificationOfFile("stations"))
                .thenReturn(LocalDateTime.now().minusMinutes(59));
        cacheService.save("stations", Collections.singletonList(new Station(0, "test1", "1234")));
        dataService.getStations().thenAccept(stations -> {
            assertEquals(1, stations.size());
            assertEquals("test1", stations.get(0).getName());
        }).join();

    }

    @Test
    public void getFromNetwork() {
        Mockito.when(cacheService.getLasyModificationOfFile("stations"))
                .thenReturn(LocalDateTime.now().minusMinutes(61));
        cacheService.save("stations", Collections.singletonList(new Station(0, "test1", "1234")));
        dataService.getStations().thenAccept(stations -> {
            assertTrue(stations.size() > 10);
        }).join();

    }
}