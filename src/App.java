import cache.CacheService;
import network.GIONRestService;
import service.AirQualityService;

class App{

    public static void main(String[] args){
        AirQualityService airQualityService = new AirQualityService(new GIONRestService(), new CacheService("GION"));
        airQualityService.getStations(stations -> {
            stations.forEach(st -> System.out.println(st.getId()));
        });

    }

}