package network.model.GION

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import model.IMappable
import model.QualityIndex
import java.util.*
import kotlin.streams.toList


@JsonIgnoreProperties(ignoreUnknown = true)
class QualityIndexGIONResponse(
    @JsonIgnore
    val values: MutableMap<String, Any?> = HashMap()) : IMappable<List<QualityIndex>> {

    override fun map(): List<QualityIndex> {
        val cVal = HashMap(values)
        return cVal
            .filter { it.key.contains("CalcDate") }
            .entries
            .stream()
            .map {
                val name = it.key.substring(0, it.key.indexOf('C'))
                return@map QualityIndex(name = name, date = Date(), level = "LEVEL")
            }.toList()
    }

    @JsonAnyGetter
    fun getAdditionalProperties(): Map<String, Any?> {
        return this.values
    }

    @JsonAnySetter
    fun setAdditionalProperty(name: String, value: Any?) {
        this.values.put(name, value)
    }
}