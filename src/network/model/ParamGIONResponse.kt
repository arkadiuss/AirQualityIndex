package network.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class ParamGIONResponse(
    @JsonProperty("idParam")
    val id: Long,
    val paramName: String,
    val paramFormula: String,
    val paramCode: String)