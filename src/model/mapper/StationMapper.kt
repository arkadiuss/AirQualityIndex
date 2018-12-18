package model.mapper

import model.Station
import network.model.StationGIONResponse

class StationMapper : Mapper<StationGIONResponse, Station>{

    override fun map(target: StationGIONResponse): Station {
        return Station(
            id = target.id,
            name = target.stationName
        )
    }

}