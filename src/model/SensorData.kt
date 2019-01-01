package model

import java.io.Serializable
import java.util.*

class SensorData(val name: String,
                 val date: Date,
                 val value: Double): Serializable