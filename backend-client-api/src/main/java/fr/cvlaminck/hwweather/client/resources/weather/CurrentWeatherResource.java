package fr.cvlaminck.hwweather.client.resources.weather;

public class CurrentWeatherResource
    extends AbstractWeatherResource {
    private double temperature;

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}
