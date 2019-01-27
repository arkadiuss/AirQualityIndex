package service.data

import cache.CacheService
import de.jodamob.kotlin.testrunner.KotlinTestRunner
import kotlinx.coroutines.runBlocking
import model.Station
import network.GIONRestService
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import java.time.LocalDateTime

class AirQualityDataServiceTest {

    private val cacheService = Mockito.spy(CacheService::class.java)
    private val dataService = AirQualityDataService(GIONRestService(), cacheService, false)

    @Test
    fun getFromCache() {
        Mockito.`when`(cacheService.getLasyModificationOfFile("stations"))
            .thenReturn(LocalDateTime.now().minusMinutes(59))
        cacheService.save("stations", listOf(Station(0, "test1", "1234")))
        runBlocking {
            val stations = dataService.getStations().await()
            assertEquals(1, stations.size)
            assertEquals("test1", stations[0].name)
        }
    }

    @Test
    fun getFromNetwork() {
        Mockito.`when`(cacheService.getLasyModificationOfFile("stations"))
            .thenReturn(LocalDateTime.now().minusMinutes(61))
        cacheService.save("stations", listOf(Station(0, "test1", "1234")))
        runBlocking {
            val stations = dataService.getStations().await()
            Assert.assertTrue(stations.size > 10)
        }
    }
}