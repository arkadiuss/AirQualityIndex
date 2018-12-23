package network.model

import model.Mappable
import model.Sensor

class SensorGIONResponse(
    val id: Long,
    val stationId: Long,
    val param: ParamGIONResponse): Mappable<Sensor> {

    override fun map(): Sensor {
        return Sensor(
            id = id,
            stationId = stationId
        )
    }
}