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
            if(!app.cmd.hasOption("station")){
                app.showHelp();
                System.exit(1);
            }
            String station = app.cmd.getOptionValue("station");
            app.showCurrentIndex(airQualityService, station);
        }
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
        Option station = Option.builder("s")
                .longOpt("station")
                .desc("Show current index for a station")
                .required(false)
                .build();
        options.addOption(api);
        options.addOption(currentIndex);
        options.addOption(station);
        return options;
    }

    private void showCurrentIndex(AirQualityService airQualityService, String station){
        airQualityService.getCurrentIndexForStation(station, qualityIndices ->
                qualityIndices.forEach(i -> System.out.println(i.getDate() +" "+i.getName()+" "+i.getLevel())));
    }

}