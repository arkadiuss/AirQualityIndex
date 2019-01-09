package network.model.GION

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import model.IMappable
import model.SensorData
import java.time.LocalDateTime

class SensorDataGIONResponse(
    val key: String = "",
    val values: Array<Entry> = emptyArray()
) : IMappable<List<SensorData>> {
    class Entry(
        @JsonIgnore
        var date: LocalDateTime = LocalDateTime.MIN,
        val value: Double = 0.0
    ){
        @JsonProperty("date")
        fun unpackDate(date: String){
            this.date = LocalDateTime.parse(date)
        }
    }

    override fun map(): List<SensorData> {
        return values.map { SensorData(date = it.date, value = it.value, name = key) }
    }
}