package fr.cvlaminck.hwweather.core.external.model.weather;

import java.time.LocalDate;
import java.util.Date;

public class ExternalDailyForecastResource
        extends ExternalWeatherResource {
    private LocalDate day;
    private ExternalWeatherCondition weatherCondition;
    private double minTemperature; //In celsius
    private double maxTemperature; //In celsius

    public LocalDate getDay() {
        return day;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }

    public ExternalWeatherCondition getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(ExternalWeatherCondition weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(double minTemperature) {
        this.minTemperature = minTemperature;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }
}
