package network.model.Airly;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MeasurementAirly {
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
}
