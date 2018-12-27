import cache.CacheService;
import network.GIONRestService;
import service.AirQualityService;

class App{

    public static void main(String[] args){
        AirQualityService airQualityService =
                new AirQualityService(new GIONRestService(), new CacheService("GION"));
//                new AirQualityService(new AirlyRestService(), new CacheService("Airly"));
        airQualityService.getStations(stations -> {
            stations.forEach(st -> System.out.println(st.getId()+" "+st.getName()));
        });
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}