package fr.cvlaminck.hwweather.client.reponses;

import fr.cvlaminck.hwweather.client.resources.CityResource;
import fr.cvlaminck.hwweather.client.resources.weather.CurrentWeatherResource;

public class WeatherResponse {

    private CityResource city;

    private CurrentWeatherResource current;

    public CityResource getCity() {
        return city;
    }

    public void setCity(CityResource city) {
        this.city = city;
    }

    public CurrentWeatherResource getCurrent() {
        return current;
    }

    public void setCurrent(CurrentWeatherResource current) {
        this.current = current;
    }
}
