package network.model.GION

import model.IMappable
import model.SensorData
import java.util.*

class SensorDataGIONResponse(
    val key: String,
    val values: Array<Entry>
) : IMappable<SensorData> {
    class Entry(
        val date: Date,
        val value: Double
    )

    override fun map(): SensorData {
        return SensorData(date = values[0].date, value = values[1].value)
    }
}