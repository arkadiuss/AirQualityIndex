package network

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import model.QualityIndex
import model.Sensor
import model.SensorData
import model.Station
import network.model.GION.QualityIndexGIONResponse
import network.model.GION.SensorDataGIONResponse
import network.model.GION.SensorGIONResponse
import network.model.GION.StationGIONResponse
import java.util.concurrent.Executors

class GIONRestService: IRestService {

    val url = "http://api.gios.gov.pl/pjp-api/rest/"
    private val executor = Executors.newFixedThreadPool(4)

    override fun getStations(callback: (List<Station>?) -> Unit) {
        executor.execute {
            runBlocking {
                val res = httpGetAsync("$url/station/findAll", Array<StationGIONResponse>::class.java)
                    .map { sts -> sts?.map { it.map() } }?.toList()
                callback(res)
            }
        }
    }

    override fun getSensors(stationID: Long, callback: (List<Sensor>?) -> Unit) {
        executor.execute {
            runBlocking {
                val res = httpGetAsync("$url/station/sensors/$stationID", Array<SensorGIONResponse>::class.java)
                    .map { sts -> sts?.map { it.map() } }
                callback(res)
            }
        }
    }

    override fun getSensorData(sensor: Sensor, callback: (List<SensorData>?) -> Unit) {
        executor.execute {
            runBlocking {
                val res = httpGetAsync( "$url/data/getData/${sensor.id}", SensorDataGIONResponse::class.java)
                    .map { it?.map() }
                callback(res)
            }
        }
    }

    override fun getIndexes(stationID: Long, callback: (List<QualityIndex>?) -> Unit) {
        GlobalScope.launch {
            val res = httpGetAsync( "$url/aqindex/getIndex/$stationID", QualityIndexGIONResponse::class.java)
                .map { it?.map() }
            callback(res)
        }
    }

}

