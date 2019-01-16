package network

import model.QualityIndex
import model.Sensor
import model.SensorData
import model.Station
import network.model.GION.QualityIndexGIONResponse
import network.model.GION.SensorDataGIONResponse
import network.model.GION.SensorGIONResponse
import network.model.GION.StationGIONResponse
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

class GIONRestService: IRestService {

    val url = "http://api.gios.gov.pl/pjp-api/rest/"
    private val executor = Executors.newFixedThreadPool(4)

    override fun getStations(): CompletableFuture<List<Station>?> {
        return CompletableFuture.supplyAsync{
            val res = httpGet("$url/station/findAll", Array<StationGIONResponse>::class.java)
                ?.map { it.map() }?.toList() as List<Station>
            res
        }
    }

    override fun getSensors(stationID: Long): CompletableFuture<List<Sensor>?> {
        return CompletableFuture.supplyAsync{
            httpGet("$url/station/sensors/$stationID", Array<SensorGIONResponse>::class.java)
                ?.map { it.map() }
        }
    }

    override fun getSensorData(sensor: Sensor): CompletableFuture<List<SensorData>?>{
        return CompletableFuture.supplyAsync {
            httpGet( "$url/data/getData/${sensor.id}", SensorDataGIONResponse::class.java)?.map()
        }
    }

    override fun getIndexes(stationID: Long): CompletableFuture<List<QualityIndex>?> {
        return CompletableFuture.supplyAsync{
            httpGet( "$url/aqindex/getIndex/$stationID", QualityIndexGIONResponse::class.java)?.map()
        }
    }

}

