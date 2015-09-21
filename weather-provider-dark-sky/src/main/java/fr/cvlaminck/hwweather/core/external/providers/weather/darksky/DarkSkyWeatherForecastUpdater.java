package fr.cvlaminck.hwweather.core.external.providers.weather.darksky;

import fr.cvlaminck.hwweather.core.external.model.weather.*;
import fr.cvlaminck.hwweather.core.external.model.weather.enums.TemperatureUnit;
import fr.cvlaminck.hwweather.core.external.providers.weather.WeatherDataProvider;
import fr.cvlaminck.hwweather.core.external.providers.weather.darksky.resources.*;
import org.springframework.stereotype.Component;
import retrofit.RestAdapter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class DarkSkyWeatherForecastUpdater {

    private WeatherDataProvider dataProvider;

    public DarkSkyWeatherForecastUpdater(WeatherDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public ExternalWeatherData refresh(double latitude, double longitude) {
        DarkSkyForecastResponse response = getRestApi().getForecast(getApiKey(), latitude, longitude);
        return convertResponseToResource(response);
    }

    private DarkSkyWeatherWeatherAPI getRestApi() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://api.forecast.io")
                .build();

        return restAdapter.create(DarkSkyWeatherWeatherAPI.class);
    }

    private ExternalWeatherData convertResponseToResource(DarkSkyForecastResponse response) {
        ExternalWeatherData data = new ExternalWeatherData();
        data.setCurrent(convertCurrentlyToResource(response.getCurrently()));
        data.setDaily(convertDailyToResources(response.getDaily()));
        data.setHourly(convertHourlyToResources(response.getHourly()));
        return data;
    }

    private ExternalCurrentWeatherResource convertCurrentlyToResource(DarkSkyCurrentlyData data) {
        ExternalCurrentWeatherResource current = new ExternalCurrentWeatherResource();
        current.setProviderName(dataProvider.getProviderName());
        current.setTime(getNormalizedDateTime(data));
        current.setTemperature(getNormalizedTemperature(data.getTemperature()));
        current.setWeatherCondition(getNormalizedWeatherCondition(data));
        return current;
    }

    private Collection<ExternalDailyForecastResource> convertDailyToResources(DarkSkyDailyData dailyData) {
        List<ExternalDailyForecastResource> resources = new ArrayList<>();
        for (DarkSkyDailyData.Data data : dailyData.getData()) {
            ExternalDailyForecastResource resource = new ExternalDailyForecastResource();
            resource.setProviderName(dataProvider.getProviderName());
            resource.setDay(getNormalizedDate(data));
            resource.setMinTemperature(getNormalizedTemperature(data.getTemperatureMin()));
            resource.setMaxTemperature(getNormalizedTemperature(data.getTemperatureMax()));
            resource.setWeatherCondition(getNormalizedWeatherCondition(data));
            resources.add(resource);
        }
        return resources;
    }

    private Collection<ExternalHourlyForecastResource> convertHourlyToResources(DarkSkyHourlyData hourlyData) {
        List<ExternalHourlyForecastResource> resources = new ArrayList<>();
        for (DarkSkyHourlyData.Data data : hourlyData.getData()) {
            ExternalHourlyForecastResource resource = new ExternalHourlyForecastResource();
            resource.setProviderName(dataProvider.getProviderName());
            resource.setHour(getNormalizedDateTime(data));
            resource.setTemperature(getNormalizedTemperature(data.getTemperature()));
            resource.setWeatherCondition(getNormalizedWeatherCondition(data));
            resources.add(resource);
        }
        return resources;
    }

    private double getNormalizedTemperature(double temperature) {
        return TemperatureUnit.FAHRENHEIT.convertToCelsius(temperature);
    }

    private LocalDate getNormalizedDate(DarkSkyData data) {
        Instant instant = Instant.ofEpochMilli(data.getTime() * 1000l);
        return instant.atZone(ZoneId.of("UTC")).toLocalDate();
    }

    private LocalDateTime getNormalizedDateTime(DarkSkyData data) {
        Instant instant = Instant.ofEpochMilli(data.getTime() * 1000l);
        return instant.atZone(ZoneId.of("UTC")).toLocalDateTime();
    }

    private ExternalWeatherCondition getNormalizedWeatherCondition(DarkSkyData data) {
        System.out.println(data.getIcon());
        return null;
    }

    private String getApiKey() {
        return "31bc40bc737612697c579145c96591e5"; //FIXME Remove before pushing to GitHub
    }
}
