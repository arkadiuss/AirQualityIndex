package network.model.Airly

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class SensorDataAirly {
    var name: String? = null
    var value: Double? = null
}
