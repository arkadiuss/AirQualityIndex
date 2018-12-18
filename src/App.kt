import service.AirQualityService
import service.GIONAirQualityService

fun main(args: Array<String>){
    val airQualityService: AirQualityService = GIONAirQualityService()
    airQualityService.getStations().forEach {
        println(it.id.toString() + " " + it.name)
    }
}