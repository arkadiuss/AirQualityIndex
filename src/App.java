import org.apache.commons.cli.*;
import service.AirQualityService;
import service.AirlyAirQualityService;
import service.GIONAirQualityService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
            airQualityService = new GIONAirQualityService(true);
        }
        if(app.cmd.hasOption("current-index")){
            if(!app.cmd.hasOption("station")){
                app.showHelp();
                System.exit(1);
            }
            String station = app.cmd.getOptionValue("station");
            app.showCurrentIndex(airQualityService, station);
        }else if(app.cmd.hasOption("sensor-status")){
            if(!app.cmd.hasOption("station")||!app.cmd.hasOption("sensor")||!app.cmd.hasOption("date")){
                app.showHelp();
                System.exit(1);
            }
            String station = app.cmd.getOptionValue("station");
            String sensor = app.cmd.getOptionValue("sensor");
            LocalDateTime date = app.getDate();
            app.showSensorDataForStationAndParam(airQualityService, station, sensor, date);
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
        Option sensorStatus = Option.builder()
                .longOpt("sensor-status")
                .desc("Show current level for sensor")
                .build();

        Option station = Option.builder("s")
                .longOpt("station")
                .desc("Select a station")
                .numberOfArgs(1)
                .required(false)
                .build();
        Option sensor = Option.builder("sn")
                .longOpt("sensor")
                .desc("Select a sensor")
                .numberOfArgs(1)
                .required(false)
                .build();

        Option date = Option.builder("d")
                .longOpt("date")
                .desc("Specify a date and time")
                .numberOfArgs(2)
                .required(false)
                .build();
        options.addOption(api);
        options.addOption(currentIndex);
        options.addOption(station);
        options.addOption(sensorStatus);
        options.addOption(sensor);
        options.addOption(date);
        return options;
    }

    private LocalDateTime getDate(){
        String[] parts = cmd.getOptionValues("date");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(parts[0]+" "+parts[1], formatter);
    }

    private void showCurrentIndex(AirQualityService airQualityService, String station){
        airQualityService.getCurrentIndexForStation(station, qualityIndices ->
                qualityIndices.forEach(i -> System.out.println(i.getDate() +" "+i.getName()+" "+i.getLevel())));
    }

    private void showSensorDataForStationAndParam(AirQualityService airQualityService,
                                                  String station, String sensor, LocalDateTime date){
        airQualityService.getSensorDataForStationAndDate(station, sensor, date, (st, sensorData) -> {
            System.out.println(st.getName() + " "+ st.getAddress());
            System.out.println(sensorData.getDate() + " " +
                    sensorData.getName() + " " + sensorData.getValue());
        });
    }

}