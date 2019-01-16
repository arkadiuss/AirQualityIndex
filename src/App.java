import org.apache.commons.cli.*;
import service.airquality.AirQualityService;
import service.airquality.IAirQualityService;
import service.data.AirlyAirQualityDataService;
import service.data.GIONAirQualityDataService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
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
    private static final String MINMAX = "minmax";
    private static final String GRAPH = "graph";

    public static void main(String[] args){
        App app = new App();
        try {
            app.cmd = app.parser.parse(app.options, args);
        } catch (ParseException e) {
            System.out.println("Unrecognized option");
            app.showHelp();
            System.exit( 1);
        }
        IAirQualityService airQualityService;
        if(app.cmd.hasOption(API) &&
            app.cmd.getOptionValue(API).equals("airly")){
            airQualityService = new AirQualityService(new AirlyAirQualityDataService());
        } else{
            airQualityService= new AirQualityService(new GIONAirQualityDataService());
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
        }else if(app.cmd.hasOption(MINMAX)){
            app.validate(SENSOR);
            String sensor = app.cmd.getOptionValue(SENSOR);
            app.showMinAndMaxForParam(airQualityService, sensor);
        }else if(app.cmd.hasOption(GRAPH)){
            app.validate(STATIONS, SENSOR, START_DATE, END_DATE);
            String[] stations = app.cmd.getOptionValues(STATIONS);
            String sensor = app.cmd.getOptionValue(SENSOR);
            LocalDateTime startDate = app.getDate(START_DATE);
            LocalDateTime endDate = app.getDate(END_DATE);
            app.showGraph(airQualityService, stations, sensor, startDate, endDate);
        }else{
            System.out.println("Unrecognized command");
            app.showHelp();
            System.exit(1);
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
        Option minmax = Option.builder()
                .longOpt(MINMAX)
                .desc("Minimal and maximal value for param")
                .required(false)
                .build();
        Option graph = Option.builder()
                .longOpt(GRAPH)
                .desc("Show graph for statios")
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
                greatestDiff, minimalParam, exceeding, minmax, graph, station, sensor,
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
                System.out.println("Argument required: "+opt);
                showHelp();
                System.exit(1);}
        }
    }

    private void showCurrentIndex(IAirQualityService airQualityService, String station){
        airQualityService.getCurrentIndexForStation(station).thenAccept(qualityIndices ->
                qualityIndices.forEach(i -> System.out.println(i.getDate() +" "+i.getName()+" "+i.getLevel())))
                .handle((aVoid, throwable) -> {
                    handleThrowable(throwable);
                    return aVoid;
                }).join();
    }

    private void showSensorDataForStationAndParam(IAirQualityService airQualityService,
                                                  String station, String sensor, LocalDateTime date){
        airQualityService.getSensorDataForStationAndDate(station, sensor, date).thenAccept(data -> {
            System.out.println(data.getFirst().getName() + " "+ data.getFirst().getAddress());
            System.out.println(data.getSecond().getDate() + " " +
                    data.getSecond().getName() + " " + data.getSecond().getValue());
        }).handle((aVoid, throwable) -> {
            handleThrowable(throwable);
            return aVoid;
        }).join();
    }

    private void showSensorAverageForStation(IAirQualityService airQualityService,
                                             String station, String sensor,
                                             LocalDateTime start, LocalDateTime end){
        airQualityService.getAverageForStationAndSensor(station, sensor, start, end).thenAccept(data -> {
            System.out.println(data.getFirst().getName() + " "+ data.getFirst().getAddress());
            System.out.println("Average is: "+data.getSecond());
        }).handle((aVoid, throwable) -> {
            handleThrowable(throwable);
            return aVoid;
        }).join();
    }

    private void showGreatestDiffForStations(IAirQualityService airQualityService,
                                             String[] stations,
                                             LocalDateTime start){
        airQualityService.getMostUnstableParameter(stations, start).thenAccept(data -> {
            System.out.println(data.getFirst().getName() + " - sensor");
            System.out.println("Diff is: " +  data.getSecond());
        }).handle((aVoid, throwable) -> {
            handleThrowable(throwable);
            return aVoid;
        }).join();
    }

    private void showMinimalParam(IAirQualityService airQualityService, LocalDateTime date){
        airQualityService.getMinimalParameter(date).thenAccept( data -> {
            System.out.println(data.getFirst().getName() + " - sensor");
            System.out.println("Mini is: "+data.getSecond());
        }).handle((aVoid, throwable) -> {
            handleThrowable(throwable);
            return aVoid;
        }).join();
    }

    private void showExceeding(IAirQualityService airQualityService, String stationName, LocalDateTime date){
        airQualityService.getExceededParamsForStation(stationName, date)
                .thenAccept(triples -> {
                    System.out.println("Exceeding params");
                    triples.stream()
                            .forEach(triple -> {
                                System.out.println(triple.component1().getName() + " - station");
                                System.out.println(triple.component2().getName() + " - sensor");
                                System.out.println("Value is: "+triple.component3().getValue());
                            });
                }).handle((aVoid, throwable) -> {
                    handleThrowable(throwable);
                    return aVoid;
                }).join();
    }

    private void showMinAndMaxForParam(IAirQualityService airQualityService, String sensorName){
        airQualityService.minMaxForParameter(sensorName)
                .thenAccept(minmax -> {
                    System.out.println("Minimal");
                    System.out.println(minmax.getFirst().getFirst().getName());
                    System.out.println(minmax.getFirst().getThird().getMin());
                    System.out.println("Maximal");
                    System.out.println(minmax.getSecond().getFirst().getName());
                    System.out.println(minmax.getSecond().getThird().getMax());
                }).handle((aVoid, throwable) -> {
                    handleThrowable(throwable);
                    return aVoid;
                }).join();
    }

    private void showGraph(IAirQualityService airQualityService, String[] stationNames, String sensorName,
                           LocalDateTime startDate, LocalDateTime endDate){
        airQualityService.getForStationsAndParam(stationNames, sensorName, startDate, endDate)
                .thenAccept(data -> {
                    DoubleSummaryStatistics summary = data.stream()
                            .mapToDouble(value -> value.getSecond().getValue())
                            .summaryStatistics();
                    Double step = (summary.getMax()-summary.getMin())/15;
                    data.sort(Comparator.comparing(o -> o.getSecond().getDate()));
                    data.forEach(entry -> {
                        System.out.print(entry.getFirst().getName()
                                + " ("+entry.getSecond().getDate()+") "
                                + " "+entry.getSecond().getValue());
                        printNStairs((int) Math.floor(entry.getSecond().getValue()/step));
                    });
                }).handle((aVoid, throwable) -> {
                    handleThrowable(throwable);
                    return aVoid;
                }).join();
    }

    private void printNStairs(int n){
        System.out.println(" "+"█".repeat(n));
    }

    private void handleThrowable(Throwable t){
        if(t != null)
            System.err.println("Couldn't receive data: "+ t.getMessage());
    }

}