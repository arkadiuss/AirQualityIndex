package model

import java.io.Serializable
import java.time.LocalDateTime

class QualityIndex(val name: String,
                   val date: LocalDateTime,
                   val level: String): Serializable{

    class Builder{
        lateinit var name: String
        lateinit var date: LocalDateTime
        lateinit var level: String

        fun build(): QualityIndex{
            //TODO: check null
            return QualityIndex(name, date, level);
        }
    }

}