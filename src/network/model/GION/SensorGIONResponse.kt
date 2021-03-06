package network.model.GION

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import model.IMappable
import model.Sensor

@JsonIgnoreProperties(ignoreUnknown = true)
class SensorGIONResponse(
    val id: Long=-1,
    val stationId: Long=-1,
    @JsonIgnore
    var name: String=""): IMappable<Sensor> {

    @JsonProperty("param")
    fun unpackParamCode(values: Map<String, String>){
        this.name = values["paramCode"]?:""
    }

    override fun map(): Sensor {
        return Sensor(
            id = id,
            stationId = stationId,
            name = name
        )
    }
}