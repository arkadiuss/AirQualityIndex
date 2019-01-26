package network.model.Airly

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@JsonIgnoreProperties(ignoreUnknown = true)
class MeasurementAirly {
    @JsonIgnore
    var tillDateTime: LocalDateTime? = null
    @JsonIgnore
    var fromDateTime: LocalDateTime? = null
    var indexes: List<IndexAirly>? = null
    var values: List<SensorDataAirly>? = null

    @JsonProperty("fromDateTime")
    private fun unpackFromDate(date: String) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        fromDateTime = LocalDateTime.parse(date.substring(0, 10) + " " + date.substring(11, 19), formatter)
    }

    @JsonProperty("tillDateTime")
    private fun unpackTillDate(date: String) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        tillDateTime = LocalDateTime.parse(date.substring(0, 10) + " " + date.substring(11, 19), formatter)
    }
}
