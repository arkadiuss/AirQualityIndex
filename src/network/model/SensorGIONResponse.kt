package network.model

import model.IMappable
import model.Sensor

class SensorGIONResponse(
    val id: Long,
    val stationId: Long,
    val param: ParamGIONResponse): IMappable<Sensor> {

    override fun map(): Sensor {
        return Sensor(
            id = id,
            stationId = stationId
        )
    }
}