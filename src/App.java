import org.apache.commons.cli.*;
import service.AirQualityService;
import service.data.AirlyAirQualityDataService;
import service.data.GIONAirQualityDataService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class App{
    private Options options = createOptions();
    private CommandLineParser parser = new DefaultParser();
    private CommandLine cmd;

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
            airQualityService = new AirQualityService(new AirlyAirQualityDataService());
        } else{
            airQualityService= new AirQualityService(new GIONAirQualityDataService());
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
            LocalDateTime date = app.getDate("date");
            app.showSensorDataForStationAndParam(airQualityService, station, sensor, date);
        }else if(app.cmd.hasOption("sensor-average")){
            if(!app.cmd.hasOption("station")||!app.cmd.hasOption("sensor")||
                    !app.cmd.hasOption("start-date")||!app.cmd.hasOption("end-date")){
                app.showHelp();
                System.exit(1);
            }
            String station = app.cmd.getOptionValue("station");
            String sensor = app.cmd.getOptionValue("sensor");
            LocalDateTime startDate = app.getDate("start-date");
            LocalDateTime endDate = app.getDate("end-date");
            app.showSensorAverageForStation(airQualityService, station,sensor,startDate, endDate);
        }else if(app.cmd.hasOption("greatest-diff")){
            if(!app.cmd.hasOption("stations")||!app.cmd.hasOption("start-date")){
                app.showHelp();
                System.exit(1);
            }
            String[] stations = app.cmd.getOptionValues("stations");
            LocalDateTime startDate = app.getDate("start-date");
            app.showGreatestDiffForStations(airQualityService, stations,startDate);
        }
        try{
            Thread.sleep(30000);
        }catch (InterruptedException e) {
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
        Option sensorStatus = Option.builder()
                .longOpt("sensor-status")
                .desc("Show current level for sensor")
                .build();
        Option sensorAverage = Option.builder()
                .longOpt("sensor-average")
                .desc("Show average for a sensor")
                .build();
        Option greatestDiff = Option.builder()
                .longOpt("greatest-diff")
                .desc("Show greatest diff for stations")
                .required(false)
                .build();

        Option station = Option.builder("s")
                .longOpt("station")
                .desc("Select a station")
                .numberOfArgs(1)
                .required(false)
                .build();
        Option stations = Option.builder()
                .longOpt("stations")
                .desc("Choose stations")
                .numberOfArgs(2)
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
        Option startDate = Option.builder()
                .longOpt("start-date")
                .desc("Specify a start date and time")
                .numberOfArgs(2)
                .required(false)
                .build();
        Option endDate = Option.builder()
                .longOpt("end-date")
                .desc("Specify a start date and time")
                .numberOfArgs(2)
                .required(false)
                .build();

        options.addOption(api);
        options.addOption(currentIndex);
        options.addOption(sensorStatus);
        options.addOption(sensorAverage);
        options.addOption(greatestDiff);

        options.addOption(station);
        options.addOption(sensor);
        options.addOption(stations);

        options.addOption(date);
        options.addOption(startDate);
        options.addOption(endDate);
        return options;
    }

    private LocalDateTime getDate(String name){
        String[] parts = cmd.getOptionValues(name);
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

    private void showSensorAverageForStation(AirQualityService airQualityService,
                                             String station, String sensor,
                                             LocalDateTime start, LocalDateTime end){
        airQualityService.getAverageForStationAndSensor(station, sensor, start, end, (st, average) -> {
            System.out.println(st.getName() + " "+ st.getAddress());
            System.out.println("Average is: "+average);
        });
    }

    private void showGreatestDiffForStations(AirQualityService airQualityService,
                                             String[] stations,
                                             LocalDateTime start){
        airQualityService.getMostUnstableParameter(stations, start,(st, average) -> {
            System.out.println(st.getName() + " - sensor");
            System.out.println("Diff is: "+average);
        });
    }

}