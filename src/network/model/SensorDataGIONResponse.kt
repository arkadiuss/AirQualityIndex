package network.model

import model.Mappable
import model.SensorData
import java.time.LocalDate

class SensorDataGIONResponse(
    val key: String,
    val values: Array<Entry>
) : Mappable<SensorData> {
    class Entry(
        val date: LocalDate,
        val value: Double
    )

    override fun map(): SensorData {
        return SensorData()
    }
}