package model

import java.io.Serializable

class Station(val id: Long,
              val name: String,
              val address: String?): Serializable