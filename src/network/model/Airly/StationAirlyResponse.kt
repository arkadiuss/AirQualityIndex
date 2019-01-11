package network.model.Airly

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import model.IMappable
import model.Station

@JsonIgnoreProperties(ignoreUnknown = true)
class StationAirlyResponse(val id: Long = 0,
                           @JsonIgnore
                           var city: String? = "",
                           @JsonIgnore
                           var street: String? = "",
                           @JsonIgnore
                           var number: String? = "") : IMappable<Station> {

    @JsonProperty("address")
    fun unpackFromAddress(address: Map<String, String>) {
        city = address["city"]
        street = address["street"]
        number = address["number"]
    }

    override fun map(): Station {
        return Station(
            id = this.id,
            name = "${this.city} ${this.street} ${this.number}",
            address = "${this.city} (ul. ${this.street} ${this.number})"
        )
    }
}