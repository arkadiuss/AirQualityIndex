package network

import network.model.StationGIONResponse

class GIONRestService {

    val url = "http://api.gios.gov.pl/pjp-api/rest/"

    fun getStations(): List<StationGIONResponse>? {
        return httpGet(url + "station/findAll", Array<StationGIONResponse>::class.java)?.toList()
    }
}