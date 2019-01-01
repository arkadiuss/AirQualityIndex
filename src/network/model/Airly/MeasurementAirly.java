package network.model.Airly;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MeasurementAirly {
    private Date tillDateTime, fromDateTime;
    private IndexAirly[] indexes;
    private SensorDataAirly[] values;

    public Date getTillDateTime() {
        return tillDateTime;
    }

    public void setTillDateTime(Date tillDateTime) {
        this.tillDateTime = tillDateTime;
    }

    public Date getFromDateTime() {
        return fromDateTime;
    }

    public void setFromDateTime(Date fromDateTime) {
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
