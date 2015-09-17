package fr.cvlaminck.hwweather.client.resources.weather;

import fr.cvlaminck.hwweather.client.resources.weather.enums.TemperatureUnit;

public class HourlyForecastResource
    extends AbstractWeatherResource {
    private double temperatureInCelsius;

    public double getTemperature(TemperatureUnit unit) {
        return 0d; //FIXME
    }

    public double getTemperatureInCelsius() {
        return temperatureInCelsius;
    }

    public void setTemperatureInCelsius(double temperatureInCelsius) {
        this.temperatureInCelsius = temperatureInCelsius;
    }
}
