package app

import kotlinx.coroutines.runBlocking
import org.apache.commons.cli.*
import service.airquality.AirQualityService
import service.airquality.IAirQualityService
import service.data.AirlyAirQualityDataService
import service.data.GIONAirQualityDataService
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.stream.Stream

class ConsoleApp: App {
    private val options = createOptions()
    private val parser = DefaultParser()
    private lateinit var cmd: CommandLine

    companion object {
        const val API = "api"
        const val STATION = "station"
        const val SENSOR = "sensor"
        const val STATIONS = "stations"
        const val START_DATE = "start-date"
        const val DATE = "date"
        const val END_DATE = "end-date"
        const val CURRECT_INDEX = "current-index"
        const val SENSOR_STATUS = "sensor-status"
        const val SENSOR_AVERAGE = "sensor-average"
        const val GREATEST_DIFF = "greatest-diff"
        const val MINIMAL_PARAM = "minimal-param"
        const val EXCEEDING = "exceeding"
        const val MINMAX = "minmax"
        const val GRAPH = "graph"
    }


    override fun run(args: Array<String>) {
        try {
            cmd = parser.parse(options, args)
        } catch (e: ParseException) {
            println("Unrecognized option")
            showHelp()
            System.exit(1)
        }

        val airQualityService: IAirQualityService
        if (cmd.hasOption(API) && cmd.getOptionValue(API) == "airly") {
            airQualityService = AirQualityService(AirlyAirQualityDataService())
        } else {
            airQualityService = AirQualityService(GIONAirQualityDataService())
        }
        when {
            cmd.hasOption(CURRECT_INDEX) -> {
                validate(STATION)
                val station = cmd.getOptionValue(STATION)
                showCurrentIndex(airQualityService, station)
            }
            cmd.hasOption(SENSOR_STATUS) -> {
                validate(STATION, SENSOR, DATE)
                val station = cmd.getOptionValue(STATION)
                val sensor = cmd.getOptionValue(SENSOR)
                val date = getDate(DATE)
                showSensorDataForStationAndParam(airQualityService, station, sensor, date)
            }
            cmd.hasOption(SENSOR_AVERAGE) -> {
                validate(STATION, SENSOR, START_DATE, END_DATE)
                val station = cmd.getOptionValue(STATION)
                val sensor = cmd.getOptionValue(SENSOR)
                val startDate = getDate(START_DATE)
                val endDate = getDate(END_DATE)
                showSensorAverageForStation(airQualityService, station, sensor, startDate, endDate)
            }
            cmd.hasOption(GREATEST_DIFF) -> {
                validate(STATIONS, START_DATE)
                val stations = cmd.getOptionValues(STATIONS)
                val startDate = getDate(START_DATE)
                showGreatestDiffForStations(airQualityService, stations, startDate)
            }
            cmd.hasOption(MINIMAL_PARAM) -> {
                validate(DATE)
                val date = getDate(DATE)
                showMinimalParam(airQualityService, date)
            }
            cmd.hasOption(EXCEEDING) -> {
                validate(STATION, DATE)
                val station = cmd.getOptionValue(STATION)
                val date = getDate(DATE)
                showExceeding(airQualityService, station, date)
            }
            cmd.hasOption(MINMAX) -> {
                validate(SENSOR)
                val sensor = cmd.getOptionValue(SENSOR)
                showMinAndMaxForParam(airQualityService, sensor)
            }
            cmd.hasOption(GRAPH) -> {
                validate(STATIONS, SENSOR, START_DATE, END_DATE)
                val stations = cmd.getOptionValues(STATIONS)
                val sensor = cmd.getOptionValue(SENSOR)
                val startDate = getDate(START_DATE)
                val endDate = getDate(END_DATE)
                showGraph(airQualityService, stations, sensor, startDate, endDate)
            }
            else -> {
                println("Unrecognized command")
                showHelp()
                System.exit(1)
            }
        }
    }

    fun showHelp() {
        val formatter = HelpFormatter()
        formatter.printHelp("airquality", options)
    }

    private fun createOptions(): Options {
        val options = Options()
        val api = Option.builder("a")
            .longOpt(API)
            .desc("Select an api to use")
            .numberOfArgs(1)
            .build()
        val currentIndex = Option.builder()
            .longOpt(CURRECT_INDEX)
            .desc("Show current index for a station")
            .build()
        val sensorStatus = Option.builder()
            .longOpt(SENSOR_STATUS)
            .desc("Show current level for sensor")
            .build()
        val sensorAverage = Option.builder()
            .longOpt(SENSOR_AVERAGE)
            .desc("Show average for a sensor")
            .build()
        val greatestDiff = Option.builder()
            .longOpt(GREATEST_DIFF)
            .desc("Show greatest diff for stations")
            .required(false)
            .build()
        val minimalParam = Option.builder()
            .longOpt(MINIMAL_PARAM)
            .desc("Show param with lowest value at the point of the time")
            .required(false)
            .build()
        val exceeding = Option.builder()
            .longOpt(EXCEEDING)
            .desc("Parameters that exceed a norm for station and time")
            .required(false)
            .build()
        val minmax = Option.builder()
            .longOpt(MINMAX)
            .desc("Minimal and maximal value for param")
            .required(false)
            .build()
        val graph = Option.builder()
            .longOpt(GRAPH)
            .desc("Show graph for statios")
            .required(false)
            .build()

        val station = Option.builder("s")
            .longOpt(STATION)
            .desc("Select a station")
            .numberOfArgs(1)
            .required(false)
            .build()
        val stations = Option.builder()
            .longOpt(STATIONS)
            .desc("Choose stations")
            .numberOfArgs(2)
            .required(false)
            .build()
        val sensor = Option.builder("sn")
            .longOpt(SENSOR)
            .desc("Select a sensor")
            .numberOfArgs(1)
            .required(false)
            .build()

        val date = Option.builder("d")
            .longOpt(DATE)
            .desc("Specify a date and time")
            .numberOfArgs(2)
            .required(false)
            .build()
        val startDate = Option.builder()
            .longOpt(START_DATE)
            .desc("Specify a start date and time")
            .numberOfArgs(2)
            .required(false)
            .build()
        val endDate = Option.builder()
            .longOpt(END_DATE)
            .desc("Specify a start date and time")
            .numberOfArgs(2)
            .required(false)
            .build()

        Stream.of(
            api, currentIndex, sensorStatus, sensorAverage,
            greatestDiff, minimalParam, exceeding, minmax, graph, station, sensor,
            stations, date, startDate, endDate
        ).forEach { options.addOption(it) }
        return options
    }

    private fun getDate(name: String): LocalDateTime {
        val parts = cmd.getOptionValues(name)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return LocalDateTime.parse(parts[0] + " " + parts[1], formatter)
    }

    private fun validate(vararg requiredOptions: String) {
        for (opt in requiredOptions) {
            if (!cmd.hasOption(opt)) {
                println("Argument required: $opt")
                showHelp()
                System.exit(1)
            }
        }
    }

    //TODO: Error handling
    private fun showCurrentIndex(airQualityService: IAirQualityService, station: String) {
        runBlocking {
            val qualityIndices = airQualityService.getCurrentIndexForStation(station)
            qualityIndices.forEach { i -> println(i.date.toString() + " " + i.name + " " + i.level) }
        }
    }


    private fun showSensorDataForStationAndParam(
        airQualityService: IAirQualityService,
        station: String, sensor: String, date: LocalDateTime
    ) {
        runBlocking {
            val data = airQualityService.getSensorDataForStationAndDate(station, sensor, date)
            println(data.first.name + " " + data.first.address)
            println(
                data.third?.date.toString() + " " +
                        data.second.name + " " + data.third?.value
            )
        }
    }

    private fun showSensorAverageForStation(
        airQualityService: IAirQualityService,
        station: String, sensor: String,
        start: LocalDateTime, end: LocalDateTime
    ) {
        runBlocking {
            val data = airQualityService.getAverageForStationAndSensor(station, sensor, start, end)
            println(data.first.name + " " + data.first.address)
            System.out.println("Average is: " + data.second)
        }
    }

    private fun showGreatestDiffForStations(
        airQualityService: IAirQualityService,
        stations: Array<String>,
        start: LocalDateTime
    ) {
        runBlocking {
            val data = airQualityService.getMostUnstableParameter(stations, start)
            println(data.first.name + " - sensor")
            System.out.println("Diff is: " + data.second)
        }
    }

    private fun showMinimalParam(airQualityService: IAirQualityService, date: LocalDateTime) {
        runBlocking {
            val data = airQualityService.getMinimalParameter(date)
            println(data.first.name + " - sensor")
            System.out.println("Mini is: " + data.second)
        }
    }

    private fun showExceeding(airQualityService: IAirQualityService, stationName: String, date: LocalDateTime) {
        runBlocking {
            val triples = airQualityService.getExceededParamsForStation(stationName, date)
            println("Exceeding params")
            triples.stream()
                .forEach { triple ->
                    println(triple.component1().name + " - station")
                    println(triple.component2().name + " - sensor")
                    System.out.println("Value is: " + triple.third?.value)
                }
        }
    }

    private fun showMinAndMaxForParam(airQualityService: IAirQualityService, sensorName: String) {
        runBlocking {
            val minmax = airQualityService.minMaxForParameter(sensorName)
            println("Minimal")
            System.out.println(minmax.first.first.name)
            System.out.println(minmax.first.third.min)
            println("Maximal")
            System.out.println(minmax.second.first.name)
            System.out.println(minmax.second.third.max)
        }

    }

    private fun showGraph(
        airQualityService: IAirQualityService, stationNames: Array<String>, sensorName: String,
        startDate: LocalDateTime, endDate: LocalDateTime
    ) {
        runBlocking {
            val data = airQualityService.getForStationsAndParam(stationNames, sensorName, startDate, endDate)
            val summary = data.stream()
                .mapToDouble { value -> value.second.value }
                .summaryStatistics()
            val step = (summary.max - summary.min) / 15
            data.sortedBy { it.second.date }
            data.forEach { entry ->
                print(
                    entry.first.name
                            + " (" + entry.second.date + ") "
                            + " " + entry.second.value
                )
                printNStairs(Math.floor(entry.second.value / step).toInt())
            }
        }
    }

    private fun printNStairs(n: Int) {
        println(" " + "█".repeat(n))
    }

    private fun handleThrowable(t: Throwable?) {
        if (t != null)
            System.err.println("Couldn't receive data: " + t.message)
    }
}