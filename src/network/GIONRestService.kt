package network

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import model.QualityIndex
import model.Sensor
import model.SensorData
import model.Station
import network.model.GION.QualityIndexGIONResponse
import network.model.GION.SensorDataGIONResponse
import network.model.GION.SensorGIONResponse
import network.model.GION.StationGIONResponse

class GIONRestService: IRestService {

    val url = "http://api.gios.gov.pl/pjp-api/rest/"

    override fun getStations(callback: (List<Station>?) -> Unit) {
        GlobalScope.launch {
            val res = httpGetAsync( "$url/station/findAll", Array<StationGIONResponse>::class.java)
                .map { sts -> sts?.map { it.map() } }?.toList()
            callback(res)
        }
    }

    override fun getSensors(stationID: Long, callback: (List<Sensor>?) -> Unit) {
        GlobalScope.launch {
            val res = httpGetAsync("$url/station/sensors/$stationID", Array<SensorGIONResponse>::class.java)
                .map { sts -> sts?.map { it.map() } }
            callback(res)
        }
    }

    override fun getSensorData(sensor: Sensor, callback: (List<SensorData>?) -> Unit) {
        GlobalScope.launch {
            val res = httpGetAsync( "$url/station/sensors/${sensor.id}", SensorDataGIONResponse::class.java)
                .map { it?.map() }
            callback(res)
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

