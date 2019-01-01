package model

import java.io.Serializable

class Sensor(
    val id: Long,
    val stationId: Long,
    val name: String) : Serializable