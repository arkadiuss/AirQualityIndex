package model

import java.io.Serializable
import java.util.*

class QualityIndex(val name: String,
                   val date: Date,
                   val level: String): Serializable{

    class Builder{
        lateinit var name: String
        lateinit var date: Date
        lateinit var level: String

        fun build(): QualityIndex{
            //TODO: check null
            return QualityIndex(name, date, level);
        }
    }

}