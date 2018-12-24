package network.model.GION

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import model.IMappable
import model.Station

@JsonIgnoreProperties(ignoreUnknown = true)
class StationGIONResponse(val id: Long = 0,
                          val stationName: String = "") : IMappable<Station> {

    override fun map(): Station {
        return Station(
            id = this.id,
            name = this.stationName
        )
    }
}