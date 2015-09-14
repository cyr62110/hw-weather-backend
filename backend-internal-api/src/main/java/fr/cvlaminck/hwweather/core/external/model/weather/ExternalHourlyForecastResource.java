package fr.cvlaminck.hwweather.core.external.model.weather;

import java.time.LocalDateTime;
import java.util.Date;

public class ExternalHourlyForecastResource
        extends ExternalWeatherResource {
    private LocalDateTime hour;
    private ExternalWeatherCondition weatherCondition;
    private double temperature; //In celsius

    public LocalDateTime getHour() {
        return hour;
    }

    public void setHour(LocalDateTime hour) {
        this.hour = hour;
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
