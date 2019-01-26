package network

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
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
        return GlobalScope.async {
            val stations = httpGet(
                url + "installations/nearest?lat=50.0647&lng=19.9450&&maxDistanceKM=20&maxResults=100",
                Array<StationAirlyResponse>::class.java, headers
            )
            stations?.map { it.map() }?: emptyList()
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun getSensors(stationID: Long): Deferred<List<Sensor>> {
        return GlobalScope.async {
            val response = httpGet(
                url + "measurements/installation?installationId=" + stationID,
                MeasurementsAirlyResponse::class.java, headers
            )
            val values = response?.current?.values
            IntStream.range(0, values!!.size)
                .mapToObj { i -> Sensor(i.toLong(), stationID?:-1, values[i].name ?:"") }
                .toList()
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun getSensorData(sensor: Sensor): Deferred<List<SensorData>> {
        return GlobalScope.async {
            val response = httpGet(
                url + "measurements/installation?installationId=" + sensor.stationId,
                MeasurementsAirlyResponse::class.java, headers
            )
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
        return GlobalScope.async{
            val response = httpGet(
                url + "measurements/installation?installationId=" + stationID,
                MeasurementsAirlyResponse::class.java, headers
            )
            val date = response?.current?.fromDateTime?: LocalDateTime.MIN
            response?.current?.indexes?.map { indexAirly ->
                    val builder = QualityIndex.Builder()
                    builder.date = date
                    builder.level = indexAirly.level
                    builder.name = indexAirly.name
                    builder.build()
                }?: emptyList()
        }
    }
}
