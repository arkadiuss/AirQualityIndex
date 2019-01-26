package network

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
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

    override fun getStations(): Deferred<List<Station>> {
        return GlobalScope.async{
            val res = httpGet("$url/station/findAll", Array<StationGIONResponse>::class.java)
                ?.map { it.map() }?.toList() as List<Station>
            res
        }
    }

    override fun getSensors(stationID: Long): Deferred<List<Sensor>> {
        return GlobalScope.async{
            httpGet("$url/station/sensors/$stationID", Array<SensorGIONResponse>::class.java)
                ?.map { it.map() }?: emptyList()
        }
    }

    override fun getSensorData(sensor: Sensor): Deferred<List<SensorData>>{
        return GlobalScope.async {
            httpGet( "$url/data/getData/${sensor.id}", SensorDataGIONResponse::class.java)?.map()?: emptyList()
        }
    }

    override fun getIndexes(stationID: Long): Deferred<List<QualityIndex>> {
        return GlobalScope.async{
            httpGet( "$url/aqindex/getIndex/$stationID", QualityIndexGIONResponse::class.java)?.map()?: emptyList()
        }
    }

}

