package network

import kotlinx.coroutines.runBlocking
import model.Sensor
import model.SensorData
import model.Station
import network.model.GION.SensorDataGIONResponse
import network.model.GION.SensorGIONResponse
import network.model.GION.StationGIONResponse

class GIONRestService: IRestService {

    val url = "http://api.gios.gov.pl/pjp-api/rest/"

    override fun getStations(callback: (List<Station>?) -> Unit) {
        runBlocking {
            println("start")
            val res = httpGetAsync( "$url/station/findAll", Array<StationGIONResponse>::class.java)
                .map { sts -> sts?.map { it.map() } }?.toList()
            println("end")
            callback(res)
        }
    }

    override fun getSensors(stationID: Long, callback: (List<Sensor>?) -> Unit) {
        runBlocking {
            val res = httpGetAsync("$url/station/sensors/$stationID", Array<SensorGIONResponse>::class.java)
                .map { sts -> sts?.map { it.map() } }
            callback(res)
        }
    }

    override fun getSensorData(sensorID: Long, callback: (List<SensorData>?) -> Unit) {
        runBlocking {
            val res = httpGetAsync( "$url/station/sensors/$sensorID", Array<SensorDataGIONResponse>::class.java)
                .map { sts -> sts?.map { it.map() } }
            callback(res)
        }
    }

}

