package network.model.Airly

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import model.IMappable
import model.QualityIndex

import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
class IndexAirly : IMappable<QualityIndex> {
    var name: String = ""
    var value: Double = 0.0
    var level: String = ""


    override fun map(): QualityIndex {
        return QualityIndex(name, LocalDateTime.now(), level)
    }
}
