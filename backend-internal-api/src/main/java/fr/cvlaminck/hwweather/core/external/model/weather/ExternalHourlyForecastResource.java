package fr.cvlaminck.hwweather.core.external.model.weather;

import java.util.Date;

public class ExternalHourlyForecastResource
        extends ExternalWeatherResource {
    private Date hour;
    private ExternalWeatherCondition weatherCondition;
    private double temperature; //In celsius

    public Date getHour() {
        return hour;
    }

    public void setHour(Date hour) {
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
