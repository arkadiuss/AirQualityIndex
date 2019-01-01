package network.model.GION

import model.IMappable
import model.SensorData
import java.util.*

class SensorDataGIONResponse(
    val key: String,
    val values: Array<Entry>
) : IMappable<List<SensorData>> {
    class Entry(
        val date: Date,
        val value: Double
    )

    override fun map(): List<SensorData> {
        return listOf(SensorData(date = values[0].date, value = values[0].value, name = key))
    }
}