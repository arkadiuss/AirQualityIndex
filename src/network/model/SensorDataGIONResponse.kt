package network.model

import java.time.LocalDate

class SensorDataGIONResponse(
    val key: String,
    val values: Array<Entry>
) {
    class Entry(
        val date: LocalDate,
        val value: Double
    )
}