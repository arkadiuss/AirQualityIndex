package network.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class StationGIONResponse(val id: Long = 0,
                          val stationName: String = "")