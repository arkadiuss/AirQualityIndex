package network.model.GION

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import model.IMappable
import model.Station

@JsonIgnoreProperties(ignoreUnknown = true)
class StationGIONResponse(val id: Long = 0,
                          val stationName: String = "",
                          @JsonIgnore
                          var city: String? = "",
                          val address: String = "") : IMappable<Station> {

    @JsonProperty("city")
    private fun unpackCityName(city: Map<String, Any>){
        this.city = city["name"] as String
    }

    override fun map(): Station {
        return Station(
            id = this.id,
            name = "${this.city} (${this.address})"
        )
    }
}