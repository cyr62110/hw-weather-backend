package fr.cvlaminck.hwweather.client.resources.weather;

import fr.cvlaminck.hwweather.client.resources.weather.enums.TemperatureUnit;

public class DailyForecastResource
        extends AbstractWeatherResource {
    private double minTemperatureInCelsius;
    private double maxTemperatureInCelsius;

    public double getMinTemperature(TemperatureUnit unit) {
        return 0d; //FIXME
    }

    public double getMaxTemperature(TemperatureUnit unit) {
        return 0d; //FIXME
    }

    public double getMinTemperatureInCelsius() {
        return minTemperatureInCelsius;
    }

    public void setMinTemperatureInCelsius(double minTemperatureInCelsius) {
        this.minTemperatureInCelsius = minTemperatureInCelsius;
    }

    public double getMaxTemperatureInCelsius() {
        return maxTemperatureInCelsius;
    }

    public void setMaxTemperatureInCelsius(double maxTemperatureInCelsius) {
        this.maxTemperatureInCelsius = maxTemperatureInCelsius;
    }
}
