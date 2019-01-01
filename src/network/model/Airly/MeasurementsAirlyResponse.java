package network.model.Airly;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MeasurementsAirlyResponse {
    private MeasurementAirly current;
    private MeasurementAirly[] history;
    private MeasurementAirly[] forecast;

    public MeasurementAirly getCurrent() {
        return current;
    }

    public void setCurrent(MeasurementAirly current) {
        this.current = current;
    }

    public MeasurementAirly[] getHistory() {
        return history;
    }

    public void setHistory(MeasurementAirly[] history) {
        this.history = history;
    }

    public MeasurementAirly[] getForecast() {
        return forecast;
    }

    public void setForecast(MeasurementAirly[] forecast) {
        this.forecast = forecast;
    }
}
