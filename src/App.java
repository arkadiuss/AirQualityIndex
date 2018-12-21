import network.GIONRestService;
import service.AirQualityService;
import service.GIONAirQualityService;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

class App{

    public static void main(String[] args){
        GIONRestService gionRestService = new GIONRestService();
        Arrays.stream(gionRestService.getStations().get()).forEach(stationGIONResponse -> {

        });

    }

}