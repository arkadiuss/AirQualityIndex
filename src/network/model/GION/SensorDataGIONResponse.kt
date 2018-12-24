package network.model.GION

import model.IMappable
import model.SensorData
import java.time.LocalDate

class SensorDataGIONResponse(
    val key: String,
    val values: Array<Entry>
) : IMappable<SensorData> {
    class Entry(
        val date: LocalDate,
        val value: Double
    )

    override fun map(): SensorData {
        return SensorData()
    }
}