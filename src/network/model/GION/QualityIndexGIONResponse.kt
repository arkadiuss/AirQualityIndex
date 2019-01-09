package network.model.GION

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import model.IMappable
import model.QualityIndex
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.streams.toList

@JsonIgnoreProperties(ignoreUnknown = true)
class QualityIndexGIONResponse(
    @JsonIgnore
    val values: MutableMap<String, Any?> = HashMap()) : IMappable<List<QualityIndex>> {

    override fun map(): List<QualityIndex> {
        val cVal = HashMap(values)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return cVal
            .filter { it.key.contains("CalcDate") }
            .entries
            .stream()
            .map {
                val name = it.key.substring(0, it.key.indexOf('C'))
                val indexLevel = cVal["${name}IndexLevel"] as HashMap<*, String>?
                var date:LocalDateTime =
                    when {
                        it.value is Long -> Instant.ofEpochMilli(it.value as Long).atZone(ZoneId.systemDefault()).toLocalDateTime()
                        it.value is String -> LocalDateTime.parse(it.value as String, formatter)
                        else -> LocalDateTime.MIN
                    }
                return@map QualityIndex(name = name,
                    date = date,
                    level = indexLevel?.get("indexLevelName")?:"NO_DATA")
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