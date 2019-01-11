package network.model.Airly;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MeasurementAirly {
    @JsonIgnore
    private LocalDateTime tillDateTime, fromDateTime;
    private IndexAirly[] indexes;
    private SensorDataAirly[] values;

    public LocalDateTime getTillDateTime() {
        return tillDateTime;
    }

    public void setTillDateTime(LocalDateTime tillDateTime) {
        this.tillDateTime = tillDateTime;
    }

    public LocalDateTime getFromDateTime() {
        return fromDateTime;
    }

    public void setFromDateTime(LocalDateTime fromDateTime) {
        this.fromDateTime = fromDateTime;
    }

    public IndexAirly[] getIndexes() {
        return indexes;
    }

    public void setIndexes(IndexAirly[] indexes) {
        this.indexes = indexes;
    }

    public SensorDataAirly[] getValues() {
        return values;
    }

    public void setValues(SensorDataAirly[] values) {
        this.values = values;
    }

    @JsonProperty("fromDateTime")
    private void unpackFromDate(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        fromDateTime = LocalDateTime.parse(date.substring(0,10)+" "+date.substring(11,19),formatter);
    }

    @JsonProperty("tillDateTime")
    private void unpackTillDate(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        tillDateTime = LocalDateTime.parse(date.substring(0,10)+" "+date.substring(11,19), formatter);
    }
}
