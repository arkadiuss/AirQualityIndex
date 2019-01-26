package network

import common.map
import kotlinx.coroutines.Deferred
import model.QualityIndex
import model.Sensor
import model.SensorData
import model.Station
import network.model.Airly.MeasurementsAirlyResponse
import network.model.Airly.StationAirlyResponse
import java.time.LocalDateTime
import java.util.*
import java.util.stream.IntStream
import java.util.stream.Stream
import kotlin.streams.toList

class AirlyRestService : IRestService {

    private val url = "https://airapi.airly.eu/v2/"
    private val apiKey = "tX152DHenh3FKr2aLFbuYuJgsxNDa9wc"
    private val headers = HashMap<String, String>()

    init {
        headers["apikey"] = apiKey
    }

    /**
     * {@inheritDoc}
     */
    override fun getStations(): Deferred<List<Station>> {
        return httpGetAsync(
                url + "installations/nearest?lat=50.0647&lng=19.9450&&maxDistanceKM=20&maxResults=100",
                Array<StationAirlyResponse>::class.java, headers
            ).map { stations ->
                stations?.map { it.map() } ?: emptyList()
            }
    }

    /**
     * {@inheritDoc}
     */
    override fun getSensors(stationID: Long): Deferred<List<Sensor>> {
        return httpGetAsync(
                url + "measurements/installation?installationId=" + stationID,
                MeasurementsAirlyResponse::class.java, headers
            ).map {
                val values = it?.current?.values
                IntStream.range(0, values?.size?:0)
                    .mapToObj { i -> Sensor(i.toLong(), stationID, values?.get(i)?.name ?:"") }
                    .toList()
            }
    }

    /**
     * {@inheritDoc}
     */
    override fun getSensorData(sensor: Sensor): Deferred<List<SensorData>> {
        return httpGetAsync(
                url + "measurements/installation?installationId=" + sensor.stationId,
                MeasurementsAirlyResponse::class.java, headers
            ).map { response ->
                val curValues = response?.current?.values
                val histValues = response?.history
                Stream.concat(
                    curValues?.stream(),
                    histValues?.stream()?.flatMap { it.values?.stream() }
                )
                    .filter { sensorDataAirly -> sensorDataAirly.name == sensor.name }
                    .map { sensorDataAirly ->
                        SensorData(
                            sensorDataAirly.name?:"",
                            response?.current?.tillDateTime?: LocalDateTime.MIN,
                            sensorDataAirly.value?:0.0
                        )
                    }
                    .toList()
            }
    }


    /**
     * {@inheritDoc}
     */
    override fun getIndexes(stationID: Long): Deferred<List<QualityIndex>> {
        return httpGetAsync(
                url + "measurements/installation?installationId=" + stationID,
                MeasurementsAirlyResponse::class.java, headers
            ).map { response ->
                val date = response?.current?.fromDateTime ?: LocalDateTime.MIN
                response?.current?.indexes?.map { indexAirly ->
                    val builder = QualityIndex.Builder()
                    builder.date = date
                    builder.level = indexAirly.level
                    builder.name = indexAirly.name
                    builder.build()
                } ?: emptyList()
            }
    }

}
