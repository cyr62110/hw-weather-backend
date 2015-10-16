package fr.cvlaminck.hwweather.core.external.model.weather;

import java.time.LocalDateTime;

public class ExternalCurrentWeatherResource
        extends ExternalWeatherResource {
    private LocalDateTime time;
    private ExternalWeatherCondition weatherCondition;
    private double temperature; //In celsius;

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public ExternalWeatherCondition getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(ExternalWeatherCondition weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}
