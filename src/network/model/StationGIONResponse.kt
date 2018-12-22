package network.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import model.Mappable
import model.Station

@JsonIgnoreProperties(ignoreUnknown = true)
class StationGIONResponse(val id: Long = 0,
                          val stationName: String = "") : Mappable<Station> {

    override fun map(): Station {
        return Station(
            id = this.id,
            name = this.stationName
        )
    }
}