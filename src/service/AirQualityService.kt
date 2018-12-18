package service

import model.Station

interface AirQualityService {
    fun getStations(): List<Station>
}