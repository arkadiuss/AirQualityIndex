package service.airquality

import de.jodamob.kotlin.testrunner.KotlinTestRunner
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import model.QualityIndex
import model.Sensor
import model.SensorData
import model.Station
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import service.data.AirQualityDataService
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

@RunWith(KotlinTestRunner::class)
class AirQualityServiceTest {

    private val dataService = Mockito.mock(AirQualityDataService::class.java)
    private val airQualityService = AirQualityService(dataService)

    @Before
    fun before() {
        val stations = Arrays.asList(
            Station(0, "test1", "ul Bujaka"),
            Station(1, "test2", "ul NieBujka")
        )
        val sensors = Arrays.asList(
            Sensor(0, 0, "PM10"),
            Sensor(1, 0, "PM15"),
            Sensor(2, 0, "PM3"),
            Sensor(3, 1, "PM10"),
            Sensor(4, 1, "PM2.5")
        )
        val sensorData0 = Arrays.asList(
            SensorData("PM10", LocalDateTime.now().minus(1, ChronoUnit.HOURS), 1.0),
            SensorData("PM10", LocalDateTime.now().minus(2, ChronoUnit.HOURS), 2.0),
            SensorData("PM10", LocalDateTime.now().minus(3, ChronoUnit.HOURS), 3.0)
        )
        val sensorData1 = Arrays.asList(
            SensorData("PM15", LocalDateTime.now().minus(1, ChronoUnit.HOURS), 10.0),
            SensorData("PM15", LocalDateTime.now().minus(2, ChronoUnit.HOURS), 20.0),
            SensorData("PM15", LocalDateTime.now().minus(3, ChronoUnit.HOURS), 30.0)
        )
        val sensorData2 = Arrays.asList(
            SensorData("PM3", LocalDateTime.now().minus(1, ChronoUnit.HOURS), 0.1),
            SensorData("PM3", LocalDateTime.now().minus(2, ChronoUnit.HOURS), 20.0),
            SensorData("PM3", LocalDateTime.now().minus(3, ChronoUnit.HOURS), 4.3)
        )
        val sensorData3 = Arrays.asList(
            SensorData("PM10", LocalDateTime.now().minus(1, ChronoUnit.HOURS), 5.0),
            SensorData("PM10", LocalDateTime.now().minus(2, ChronoUnit.HOURS), 2.0),
            SensorData("PM10", LocalDateTime.now().minus(3, ChronoUnit.HOURS), 3.0)
        )
        val sensorData4 = Arrays.asList(
            SensorData("PM2.5", LocalDateTime.now().minus(1, ChronoUnit.HOURS), 1.0),
            SensorData("PM2.5", LocalDateTime.now().minus(2, ChronoUnit.HOURS), 4.0),
            SensorData("PM2.5", LocalDateTime.now().minus(3, ChronoUnit.HOURS), 3.0)
        )
        val indices = Arrays.asList(
            QualityIndex("st", LocalDateTime.now().minus(1, ChronoUnit.HOURS), "Good"),
            QualityIndex("NO2", LocalDateTime.now().minus(1, ChronoUnit.HOURS), "Bad")
        )
        Mockito.`when`(dataService.getStations())
            .thenReturn(GlobalScope.async { stations })
        Mockito.`when`(dataService.getSensors(0L))
            .thenReturn(GlobalScope.async { sensors.filter { sensor -> sensor.stationId == 0L } })
        Mockito.`when`(dataService.getSensors(1L))
            .thenReturn(GlobalScope.async { sensors.filter { sensor -> sensor.stationId == 1L } })
        Mockito.`when`(dataService.getSensorData(sensors[0]))
            .thenReturn(GlobalScope.async {sensorData0})
        Mockito.`when`(dataService.getSensorData(sensors[1]))
            .thenReturn(GlobalScope.async {sensorData1})
        Mockito.`when`(dataService.getSensorData(sensors[2]))
            .thenReturn(GlobalScope.async {sensorData2})
        Mockito.`when`(dataService.getSensorData(sensors[3]))
            .thenReturn(GlobalScope.async { sensorData3 })
        Mockito.`when`(dataService.getSensorData(sensors[4]))
            .thenReturn(GlobalScope.async { sensorData4 })
        Mockito.`when`(dataService.getIndexes(0L))
            .thenReturn(GlobalScope.async { indices })
    }

    @Test
    fun getCurrentIndexForStation() {
        runBlocking {
            val qualityIndices = airQualityService.getCurrentIndexForStation("test1")
            assertEquals(2, qualityIndices.size)
            assertEquals("Good", qualityIndices[0].level)
            assertEquals("Bad", qualityIndices[1].level)
        }
    }

    @Test
    fun getSensorDataForStationAndDate() {
        runBlocking {
            val stationSensorDataPair = airQualityService.getSensorDataForStationAndDate("test1", "PM10",
                LocalDateTime.now().minusHours(2))
            assertEquals(2.0, stationSensorDataPair.third?.value?:-1.0, 0.01)
        }
    }

    @Test
    fun getAverageForStationAndSensor() {
        runBlocking {
            val stationSensorDataPair = airQualityService.getAverageForStationAndSensor(
                "test1", "PM10",
                LocalDateTime.now().minusHours(2).minusMinutes(2), LocalDateTime.now()
            )
            assertEquals(1.5, stationSensorDataPair.second, 0.01)
        }
    }

    @Test
    fun getMostUnstableParameter() {
        runBlocking {
            val sensorDoublePair  = airQualityService.getMostUnstableParameter(arrayOf("test1", "test2"),
                LocalDateTime.now().minusHours(5))
            assertEquals("PM15", sensorDoublePair.first.name)
            assertEquals(20.0, sensorDoublePair.second, 0.01)
        }

    }

    @Test
    fun getMinimalParameter() {
        runBlocking {
            val sensorDoublePair = airQualityService.getMinimalParameter(LocalDateTime.now().minusHours(2))
            assertEquals("PM10", sensorDoublePair.first.name)
            assertEquals(2.0, sensorDoublePair.second, 0.01)
        }
    }


    @Test
    fun minMaxForParameter() {
        runBlocking {
            val tripleTriplePair = airQualityService.minMaxForParameter("PM10")
            assertEquals(1.0, tripleTriplePair.first.third.min, 0.01)
            assertEquals(3.0, tripleTriplePair.first.third.max, 0.01)
        }
    }

}