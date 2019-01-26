package network.model.Airly

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class MeasurementsAirlyResponse {
    var current: MeasurementAirly? = null
    var history: List<MeasurementAirly>? = null
    var forecast: List<MeasurementAirly>? = null
}
