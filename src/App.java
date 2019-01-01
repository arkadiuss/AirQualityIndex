import cache.CacheService;
import network.AirlyRestService;
import service.AirQualityService;

class App{

    public static void main(String[] args){
        AirQualityService airQualityService =
//                new AirQualityService(new GIONRestService(), new CacheService("GION"));
                new AirQualityService(new AirlyRestService(), new CacheService("Airly"));
//        airQualityService.getStations(stations -> {
//            stations.forEach(st -> System.out.println(st.getId()+" "+st.getName()));
//        });
//        airQualityService.getSensors(401L, sensors -> {
//            System.out.println("received");
//            sensors.forEach(st -> System.out.println(st.getId()+" "+st.getName()+" "+st.getStationId()));
//        });

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}