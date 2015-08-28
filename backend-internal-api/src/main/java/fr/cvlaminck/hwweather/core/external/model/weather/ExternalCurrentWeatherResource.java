package fr.cvlaminck.hwweather.core.external.model.weather;

import java.util.Date;

public class ExternalCurrentWeatherResource
        extends ExternalWeatherResource {
    private Date time;
    private ExternalWeatherCondition weatherCondition;
    private double temperature; //In celsius;

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
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
