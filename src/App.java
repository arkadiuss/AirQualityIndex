import org.apache.commons.cli.*;
import service.AirQualityService;
import service.data.AirlyAirQualityDataService;
import service.data.GIONAirQualityDataService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

class App{
    private Options options = createOptions();
    private CommandLineParser parser = new DefaultParser();
    private CommandLine cmd;
    private static final String API = "api";
    private static final String STATION = "station";
    private static final String SENSOR = "sensor";
    private static final String STATIONS = "stations";
    private static final String START_DATE = "start-date";
    private static final String DATE = "date";
    private static final String END_DATE = "end-date";
    private static final String CURRECT_INDEX = "current-index";
    private static final String SENSOR_STATUS = "sensor-status";
    private static final String SENSOR_AVERAGE = "sensor-average";
    private static final String GREATEST_DIFF = "greatest-diff";
    private static final String MINIMAL_PARAM = "minimal-param";
    private static final String EXCEEDING = "exceeding";

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
        if(app.cmd.hasOption(API) &&
            app.cmd.getOptionValue(API).equals("airly")){
            airQualityService = new AirQualityService(new AirlyAirQualityDataService());
        } else{
            airQualityService= new AirQualityService(new GIONAirQualityDataService(false));
        }
        if(app.cmd.hasOption(CURRECT_INDEX)){
            app.validate(STATION);
            String station = app.cmd.getOptionValue(STATION);
            app.showCurrentIndex(airQualityService, station);
        }else if(app.cmd.hasOption(SENSOR_STATUS)){
            app.validate(STATION, SENSOR, DATE);
            String station = app.cmd.getOptionValue(STATION);
            String sensor = app.cmd.getOptionValue(SENSOR);
            LocalDateTime date = app.getDate(DATE);
            app.showSensorDataForStationAndParam(airQualityService, station, sensor, date);
        }else if(app.cmd.hasOption(SENSOR_AVERAGE)){
            app.validate(STATION, SENSOR, START_DATE, END_DATE);
            String station = app.cmd.getOptionValue(STATION);
            String sensor = app.cmd.getOptionValue(SENSOR);
            LocalDateTime startDate = app.getDate(START_DATE);
            LocalDateTime endDate = app.getDate(END_DATE);
            app.showSensorAverageForStation(airQualityService, station,sensor,startDate, endDate);
        }else if(app.cmd.hasOption(GREATEST_DIFF)){
            app.validate(STATIONS, START_DATE);
            String[] stations = app.cmd.getOptionValues(STATIONS);
            LocalDateTime startDate = app.getDate(START_DATE);
            app.showGreatestDiffForStations(airQualityService, stations,startDate);
        }else if(app.cmd.hasOption(MINIMAL_PARAM)){
            app.validate(DATE);
            LocalDateTime date = app.getDate(DATE);
            app.showMinimalParam(airQualityService, date);
        }else if(app.cmd.hasOption(EXCEEDING)){
            app.validate(STATION, DATE);
            String station = app.cmd.getOptionValue(STATION);
            LocalDateTime date = app.getDate(DATE);
            app.showExceeding(airQualityService, station, date);
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
                .longOpt(API)
                .desc("Select an api to use")
                .numberOfArgs(1)
                .build();

        Option currentIndex = Option.builder()
                .longOpt(CURRECT_INDEX)
                .desc("Show current index for a station")
                .build();
        Option sensorStatus = Option.builder()
                .longOpt(SENSOR_STATUS)
                .desc("Show current level for sensor")
                .build();
        Option sensorAverage = Option.builder()
                .longOpt(SENSOR_AVERAGE)
                .desc("Show average for a sensor")
                .build();
        Option greatestDiff = Option.builder()
                .longOpt(GREATEST_DIFF)
                .desc("Show greatest diff for stations")
                .required(false)
                .build();
        Option minimalParam = Option.builder()
                .longOpt(MINIMAL_PARAM)
                .desc("Show param with lowest value at the point of the time")
                .required(false)
                .build();
        Option exceeding = Option.builder()
                .longOpt(EXCEEDING)
                .desc("Parameters that exceed a norm for station and time")
                .required(false)
                .build();

        Option station = Option.builder("s")
                .longOpt(STATION)
                .desc("Select a station")
                .numberOfArgs(1)
                .required(false)
                .build();
        Option stations = Option.builder()
                .longOpt(STATIONS)
                .desc("Choose stations")
                .numberOfArgs(2)
                .required(false)
                .build();
        Option sensor = Option.builder("sn")
                .longOpt(SENSOR)
                .desc("Select a sensor")
                .numberOfArgs(1)
                .required(false)
                .build();

        Option date = Option.builder("d")
                .longOpt(DATE)
                .desc("Specify a date and time")
                .numberOfArgs(2)
                .required(false)
                .build();
        Option startDate = Option.builder()
                .longOpt(START_DATE)
                .desc("Specify a start date and time")
                .numberOfArgs(2)
                .required(false)
                .build();
        Option endDate = Option.builder()
                .longOpt(END_DATE)
                .desc("Specify a start date and time")
                .numberOfArgs(2)
                .required(false)
                .build();

        Stream.of(api, currentIndex, sensorStatus, sensorAverage,
                greatestDiff, minimalParam, exceeding, station, sensor,
                stations, date, startDate, endDate)
                .forEach(options::addOption);
        return options;
    }

    private LocalDateTime getDate(String name){
        String[] parts = cmd.getOptionValues(name);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(parts[0]+" "+parts[1], formatter);
    }

    private void validate(String... requiredOptions){
        for(String opt : requiredOptions){
            if(!cmd.hasOption(opt)){
                showHelp();
                System.exit(1);}
        }
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

    private void showMinimalParam(AirQualityService airQualityService, LocalDateTime date){
        airQualityService.getMinimalParameter(date, (sen, mini) -> {
            System.out.println(sen.getName() + " - sensor");
            System.out.println("Mini is: "+mini);
        });
    }

    private void showExceeding(AirQualityService airQualityService, String stationName, LocalDateTime date){
        airQualityService.getExceededParamsForStation(stationName, date)
                .thenAccept(triples -> {
                    System.out.println("Exceeding params");
                    triples.stream()
                            .forEach(triple -> {
                                System.out.println(triple.component1().getName() + " - station");
                                System.out.println(triple.component2().getName() + " - sensor");
                                System.out.println("Value is: "+triple.component3().getValue());
                            });
                });
    }

}