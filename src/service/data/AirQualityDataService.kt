package service.data

import cache.ICacheService
import common.map
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import model.QualityIndex
import model.Sensor
import model.SensorData
import model.Station
import network.IRestService
import java.time.LocalDateTime

/**
 * {@inheritDoc}
 *
 * This implementation cache data and if they are younger then one hour returns them
 */
open class AirQualityDataService(
    private val restService: IRestService,
    private val cacheService: ICacheService,
    private val forceNetwork: Boolean
) : IAirQualityDataService {


    /**
     * {@inheritDoc}
     */
    override fun getStations(): Deferred<List<Station>> {
        val key = "stations"
        return checkCacheAndGet(key) {
            restService.getStations().map { stations -> saveOrGetFromCache(key, stations) }
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun getSensors(stationId: Long): Deferred<List<Sensor>> {
        val key = "sensors$stationId"
        return checkCacheAndGet(key) {
            restService.getSensors(stationId).map { sensors -> saveOrGetFromCache(key, sensors) }
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun getSensorData(sensor: Sensor): Deferred<List<SensorData>> {
        val key = "sensorData${sensor.id}"
        return checkCacheAndGet(key){
            restService.getSensorData(sensor).map { sensorData -> saveOrGetFromCache(key, sensorData) }
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun getIndexes(stationId: Long): Deferred<List<QualityIndex>> {
        val key = "indices$stationId"
        return checkCacheAndGet(key){
            restService.getIndexes(stationId).map{ qualityIndices -> saveOrGetFromCache(key, qualityIndices) }
        }
    }

    private fun <T> checkCacheAndGet(key: String, f: () -> Deferred<T>): Deferred<T> {
        return if ((!forceNetwork) && !isExpired(key)) {
            GlobalScope.async { getFromCache<T>(key)}
        } else {
            f()
        }
    }

    private fun <T> getFromCache(key: String): T {
        showCacheInfo(key)
        val fromCache = cacheService.read(key).map { o -> o as T }
        return fromCache.orElse(null)
    }

    private fun <T> saveOrGetFromCache(key: String, data: T?): T {
        if (data == null) {
            showDownloadError(key)
            return cacheService.read(key).get() as T
        }
        cacheService.save(key, data)
        return data
    }

    private fun isExpired(key: String): Boolean {
        return !cacheService.getLasyModificationOfFile(key).plusHours(1).isAfter(LocalDateTime.now())
    }

    private fun showDownloadError(key: String) {
        println("Unable to download, getting $key from cache")
    }

    private fun showCacheInfo(key: String) {
        println("Data is younger than 1 hour, getting $key from cache")
    }
}