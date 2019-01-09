package model

import java.io.Serializable
import java.time.LocalDateTime

class SensorData(val name: String,
                 val date: LocalDateTime,
                 val value: Double): Serializable