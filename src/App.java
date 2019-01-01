import service.AirQualityService;
import service.GIONAirQualityService;

class App{

    public static void main(String[] args){
        AirQualityService airQualityService =
                new GIONAirQualityService();
//                new AirlyAirQualityService();
//        airQualityService.getStations(stations -> {
//            stations.forEach(st -> System.out.println(st.getId()+" "+st.getName()));
//        });
//        airQualityService.getSensors(401L, sensors -> {
//            System.out.println("received");
//            sensors.forEach(st -> System.out.println(st.getId()+" "+st.getName()+" "+st.getStationId()));
//        });
//        airQualityService.getSensorData(new Sensor(2770, 401, "PM10"), sensorData -> {
//            sensorData.forEach(st -> System.out.println(st.getDate()+" "+st.getName()+" "+st.getValue() ));
//        });
        airQualityService.getIndexes(401L, indexes -> {
            indexes.forEach(st -> System.out.println(st.getDate()+" "+st.getName()+" "+st.getLevel()));
        });
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}