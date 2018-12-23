import kotlin.Unit;
import network.GIONRestService;

class App{

    public static void main(String[] args){
        GIONRestService gionRestService = new GIONRestService();
        gionRestService.getStations(stations -> {
            stations.forEach(st -> System.out.println(st.getId()));
            return Unit.INSTANCE;
        });

    }

}