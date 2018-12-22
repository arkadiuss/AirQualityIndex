package network

import kotlinx.coroutines.runBlocking
import model.Sensor
import model.SensorData
import model.Station
import network.model.SensorDataGIONResponse
import network.model.SensorGIONResponse
import network.model.StationGIONResponse

class GIONRestService: RestService {

    val url = "http://api.gios.gov.pl/pjp-api/rest/"

    override fun getStations(callback: Callback<List<Station>?>) {
        runBlocking {
            val res = httpGetAsync( "$url/station/findAll", Array<StationGIONResponse>::class.java)
                .map { sts -> sts?.map { it.map() } }?.toList()
            callback.onReceive(res)
        }
    }

    fun getSensors(stationID: Long) {
        return httpGetAsync("$url/station/sensors/$stationID", Array<SensorGIONResponse>::class.java)
            .map { sts -> sts?.map { it.map() } }
    }

    fun getSensorData(sensorID: Long) {
        return httpGetAsync( "$url/station/sensors/$sensorID", Array<SensorDataGIONResponse>::class.java)
            .map { sts -> sts?.map { it.map() } }
    }

}

