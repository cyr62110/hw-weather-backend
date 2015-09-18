package fr.cvlaminck.hwweather.client.reponses.weather;

import fr.cvlaminck.hwweather.client.resources.CityResource;
import fr.cvlaminck.hwweather.client.resources.weather.CurrentWeatherResource;
import fr.cvlaminck.hwweather.client.resources.weather.DailyForecastResource;
import fr.cvlaminck.hwweather.client.resources.weather.HourlyForecastResource;

import java.util.Collection;

public class WeatherResponse {

    private CityResource city;

    private CurrentWeatherResource current;

    private Collection<HourlyForecastResource> hourly;

    private Collection<DailyForecastResource> daily;

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

    public Collection<HourlyForecastResource> getHourly() {
        return hourly;
    }

    public void setHourly(Collection<HourlyForecastResource> hourly) {
        this.hourly = hourly;
    }

    public Collection<DailyForecastResource> getDaily() {
        return daily;
    }

    public void setDaily(Collection<DailyForecastResource> daily) {
        this.daily = daily;
    }
}
