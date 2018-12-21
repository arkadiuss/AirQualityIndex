package network

import model.mapper.Mapper
import network.model.SensorDataGIONResponse
import network.model.SensorGIONResponse
import network.model.StationGIONResponse
import java.util.concurrent.Executors
import java.util.concurrent.Future

class GIONRestService {

    val url = "http://api.gios.gov.pl/pjp-api/rest/"
    val executor = Executors.newSingleThreadExecutor()

    fun getStations(): Future<Array<StationGIONResponse>?> {
        return httpGetAsync(executor, "$url/station/findAll", Array<StationGIONResponse>::class.java)
    }

    fun getSensors(stationID: Long): Future<Array<SensorGIONResponse>?> {
        return httpGetAsync(executor,"$url/station/sensors/$stationID", Array<SensorGIONResponse>::class.java)
    }

    fun getSensorData(sensorID: Long): Future<Array<SensorDataGIONResponse>?> {
        return httpGetAsync(executor, "$url/station/sensors/$sensorID", Array<SensorDataGIONResponse>::class.java)
    }

}

fun <T, V> Future<T>.map(mapper: Mapper<T, V>): V{
    return mapper.map(get())
}