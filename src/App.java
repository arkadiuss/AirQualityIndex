import org.apache.commons.cli.*;
import service.AirQualityService;
import service.AirlyAirQualityService;
import service.GIONAirQualityService;

import java.util.Scanner;

class App{
    private Options options = createOptions();
    private CommandLineParser parser = new DefaultParser();
    private CommandLine cmd;
    private Scanner scanner = new Scanner(System.in);

    public static void main(String[] args){
        App app = new App();
        try {
            app.cmd = app.parser.parse(app.options, args);
        } catch (ParseException e) {
            e.printStackTrace();
            app.showHelp();
            System.exit( 1);
        }
        AirQualityService airQualityService;
        if(app.cmd.hasOption("api") &&
            app.cmd.getOptionValue("api").equals("airly")){
            airQualityService = new AirlyAirQualityService();
        } else{
            airQualityService = new GIONAirQualityService();
        }
        if(app.cmd.hasOption("current-index")){
            app.showCurrentIndex(airQualityService);
        }
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
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void showHelp(){
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "airquality", options);
    }

    private Options createOptions(){
        Options options = new Options();
        Option api = Option.builder("a")
                .longOpt("api")
                .desc("Select an api to use")
                .numberOfArgs(1)
                .build();
        Option currentIndex = Option.builder()
                .longOpt("current-index")
                .desc("Show current index for a station")
                .build();
        options.addOption(api);
        options.addOption(currentIndex);
        return options;
    }

    private void showCurrentIndex(AirQualityService airQualityService){
        airQualityService.getStations(stations -> {
            stations.forEach(st -> System.out.println(st.getId()+" "+st.getName()));
            System.out.println("For which station? [id]");
            Long id = scanner.nextLong();
            airQualityService.getIndexes(id, indexes -> {
                indexes.forEach(st -> System.out.println(st.getDate() +" "+st.getName()+" "+st.getLevel()));
            });
        });
    }

}