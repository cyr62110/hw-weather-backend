package fr.cvlaminck.hwweather.core.external.providers.weather.darksky;

import fr.cvlaminck.hwweather.core.external.model.weather.*;
import fr.cvlaminck.hwweather.core.external.model.weather.enums.TemperatureUnit;
import fr.cvlaminck.hwweather.core.external.providers.weather.WeatherDataProvider;
import fr.cvlaminck.hwweather.core.external.providers.weather.darksky.resources.*;
import org.springframework.stereotype.Component;
import retrofit.RestAdapter;

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
        current.setTime(getNormalizedDate(data));
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
            resource.setHour(getNormalizedDate(data));
            resource.setTemperature(getNormalizedTemperature(data.getTemperature()));
            resource.setWeatherCondition(getNormalizedWeatherCondition(data));
            resources.add(resource);
        }
        return resources;
    }

    private double getNormalizedTemperature(double temperature) {
        return TemperatureUnit.FAHRENHEIT.convertToCelsius(temperature);
    }

    private Date getNormalizedDate(DarkSkyData data) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(data.getTime());
        return calendar.getTime();
    }

    private ExternalWeatherCondition getNormalizedWeatherCondition(DarkSkyData data) {
        System.out.println(data.getIcon());
        return null;
    }

    private String getApiKey() {
        return "2c0adcb3fa33fe9084bb29cfce5532e2"; //FIXME Remove before pushing to GitHub
    }
}
