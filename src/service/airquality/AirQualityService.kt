package service.airquality

import common.*
import exception.NotFoundException
import model.QualityIndex
import model.Sensor
import model.SensorData
import model.Station
import service.data.AirQualityDataService
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

/**
 * {@inheritDoc}
 *
 * Implementation using stream interface. It turns out that is not always the good solution to use only streams
 */
class AirQualityService(private val airQualityDataService: AirQualityDataService) : IAirQualityService {

    /**
     * {@inheritDoc}
     */
    override suspend fun getCurrentIndexForStation(stationName: String): List<QualityIndex> {
        val station =  getStationByName(stationName)
        return airQualityDataService.getIndexes(station.id).await()
    }

    /**
     * {@inheritDoc}
     */
    override suspend fun getSensorDataForStationAndDate(
        stationName: String, sensorName: String,
        date: LocalDateTime
    ): Triple<Station, Sensor, SensorData?> {
        val station =  getStationByName(stationName)
        val sensor = getSensorByName(station.id, sensorName)
        return Triple(station, sensor, airQualityDataService.getSensorData(sensor).awaitAndMap { sensorData ->
            sensorData.minBy { secondsDiff(it.date, date) }
        })
    }

    /**
     * {@inheritDoc}
     */
    override suspend fun getAverageForStationAndSensor(
        stationName: String, sensorName: String,
        startDate: LocalDateTime, endDate: LocalDateTime
    ): Pair<Station, Double> {
        val station =  getStationByName(stationName)
        val sensor = getSensorByName(station.id, sensorName)
        return Pair(station, airQualityDataService.getSensorData(sensor).awaitAndMap { sensorData ->
                sensorData.stream()
                        .filter { sensorEntry -> sensorEntry.date.isBetween(startDate, endDate) }
                        .mapToDouble { it.value }
                        .average().orElse(0.0)
                })

    }

    /**
     * {@inheritDoc}
     */
    override suspend fun getMostUnstableParameter(
        stationsNames: Array<String>,
        startDate: LocalDateTime
    ): Pair<Sensor, Double> {
        val stations = airQualityDataService.getStations().awaitAndMap { stations ->
            stations.filter {station ->
                stationsNames.any { station.name.contains(it) }
            }
        }
        return stations
            .flatMap { airQualityDataService.getSensors(it.id).await() }
            .map { sensor -> Pair(sensor, airQualityDataService.getSensorData(sensor).await()) }
            .mapSecond { sensorData ->
                val filData = sensorData.stream()
                    .filter { data -> data.date.isAfter(startDate) }
                    .mapToDouble { it.value }
                    .summaryStatistics()
                filData.max - filData.min
            }
            .maxBy { it.second }?:throw NotFoundException("Unable to determine")
    }

    /**
     * {@inheritDoc}
     */
    override suspend fun getMinimalParameter(date: LocalDateTime): Pair<Sensor, Double> {
        val stations = airQualityDataService.getStations().await()
        val sensors = stations
            .flatMap { station -> airQualityDataService.getSensors(station.id).await() }
        return sensors
            .map { Pair(it, airQualityDataService.getSensorData(it).await()) }
            .mapSecond { sensorData ->
                sensorData.minBy { secondsDiff(it.date, date) }?.value?:-1.0
            }
            .minBy { it.second }?:throw NotFoundException("Unable to determine")
    }

    /**
     * {@inheritDoc}
     */
    override suspend fun getExceededParamsForStation(
        stationName: String,
        date: LocalDateTime
    ): List<Triple<Station, Sensor, SensorData?>> {
        val limit = HashMap<String, Double>()
        limit["C6H6"] = 5.0
        limit["NO2"] = 200.0
        limit["SO2"] = 350.0
        limit["CO"] = 10000.0
        limit["PM10"] = 50.0
        limit["PM2.5"] = 25.0
        limit["Pb"] = 0.5
        val station = getStationByName(stationName)
        val sensors = airQualityDataService.getSensors(station.id).await()
        return sensors
            .map { Triple(station, it, airQualityDataService.getSensorData(it).await()) }
            .mapThird { data -> data.minBy { secondsDiff(it.date, date) } }
            .filter {t -> t.third?.value?:-1.0 > limit[t.second.name]?:100000.0 }

    }

    /**
     * {@inheritDoc}
     */
    override suspend fun minMaxForParameter(sensorName: String): Pair<Triple<Station, Sensor, DoubleSummaryStatistics>,
            Triple<Station, Sensor, DoubleSummaryStatistics>> {
        val stations = airQualityDataService.getStations().await()
        val stationWithSensors = stations.map { station -> Pair(station, getSensorByName(station.id, sensorName)) }
        val data = stationWithSensors
            .map { Triple(it.first, it.second, airQualityDataService.getSensorData(it.second).await()) }
            .mapThird { t -> t.stream().mapToDouble{ it.value }.summaryStatistics() }

            val min = data
                .minBy { it.third.min }?: throw NotFoundException("No data for $sensorName")
            val max = data
                .maxBy { it.third.max }?: throw NotFoundException("No data for $sensorName")
            return Pair(min, max)

    }

    /**
     * {@inheritDoc}
     */
    override suspend fun getForStationsAndParam(
        stationsNames: Array<String>, sensorName: String,
        startDate: LocalDateTime, endDate: LocalDateTime
    ): List<Pair<Station, SensorData>> {
        val stations = airQualityDataService.getStations().awaitAndMap { stations ->
            stations.filter {station ->
                stationsNames.any { station.name.contains(it) }
            }
        }
        val stationWithSensor = stations
            .map { station -> Pair(station, getSensorByName(station.id, sensorName)) }

        return stationWithSensor
            .map { Pair(it.first, airQualityDataService.getSensorData(it.second).await()) }
            .flatMap { p -> p.second.map { sensorData -> Pair(p.first, sensorData) } }
            .filter { p -> p.second.date.isBetween(startDate, endDate) }
    }

    /**
     * Get stations and filter given one
     * @param stationName name to filter by
     * @return station filtered
     */
    private suspend fun getStationByName(stationName: String): Station {
        return airQualityDataService.getStations().map { stations ->
            stations
                .firstOrNull { s -> s.name.contains(stationName) }
                ?:throw NotFoundException("Station $stationName couldn't be found")
        }.await()
    }

    /**
     * Get sensors and filter given one
     * @param sensorName name to filter by
     * @return sensors filtered
     */
    private suspend fun getSensorByName(stationId: Long, sensorName: String): Sensor {
        return airQualityDataService.getSensors(stationId).map { sensors ->
            sensors.firstOrNull { s -> s.name.contains(sensorName) }
                ?: throw NotFoundException("Sensor $sensorName couldn't be found")
        }.await()
    }

    private fun secondsDiff(date1: LocalDateTime, date2: LocalDateTime): Long {
        return Math.abs(date1.until(date2, ChronoUnit.SECONDS))
    }
}
