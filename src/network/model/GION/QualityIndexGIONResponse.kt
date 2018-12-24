package network.model.GION

import com.fasterxml.jackson.annotation.JsonProperty
import model.IMappable
import model.QualityIndex
import java.util.*

class QualityIndexGIONResponse(
    @JsonProperty("stCalcDate")
    val date: Date,
    @JsonProperty("stIndexLevel.indexLevelName")
    val level: String) : IMappable<QualityIndex> {
    override fun map(): QualityIndex {
        return QualityIndex(
            date = date,
            level = level
        )
    }
}