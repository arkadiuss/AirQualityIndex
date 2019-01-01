package network.model.GION

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import model.IMappable
import model.SensorData
import java.util.*

class SensorDataGIONResponse(
    val key: String = "",
    val values: Array<Entry> = emptyArray()
) : IMappable<List<SensorData>> {
    class Entry(
        @JsonIgnore
        var date: Date = Date(),
        val value: Double = 0.0
    ){
        @JsonProperty("date")
        fun unpackDate(date: String){
            this.date = Date()
        }
    }

    override fun map(): List<SensorData> {
        return values.map { SensorData(date = it.date, value = it.value, name = key) }
    }
}