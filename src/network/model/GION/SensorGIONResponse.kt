package network.model.GION

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import model.IMappable
import model.Sensor

@JsonIgnoreProperties(ignoreUnknown = true)
class SensorGIONResponse(
    val id: Long,
    val stationId: Long,
    @JsonProperty("param.paramCode")
    val name: String): IMappable<Sensor> {

    override fun map(): Sensor {
        return Sensor(
            id = id,
            stationId = stationId,
            name = name
        )
    }
}