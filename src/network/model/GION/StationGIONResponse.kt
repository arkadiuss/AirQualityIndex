package network.model.GION

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import model.IMappable
import model.Station

@JsonIgnoreProperties(ignoreUnknown = true)
class StationGIONResponse(val id: Long = 0,
                          val stationName: String = "",
                          var addressStreet: String? = "",
                          @JsonIgnore()
                          var city: String? = "") : IMappable<Station> {

    @JsonProperty("city", required = false)
    private fun unpackCityName(city: Map<String, Any>?){
        this.city =  (city?.get("name")?:"") as String
    }

    override fun map(): Station {
        return Station(
            id = this.id,
            name = stationName,
            address = "${this.city} (${this.addressStreet})"
        )
    }
}