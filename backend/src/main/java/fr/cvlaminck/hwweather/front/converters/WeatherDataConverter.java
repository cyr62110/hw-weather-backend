package fr.cvlaminck.hwweather.front.converters;

import fr.cvlaminck.hwweather.client.resources.weather.CurrentWeatherResource;
import fr.cvlaminck.hwweather.client.resources.weather.DailyForecastResource;
import fr.cvlaminck.hwweather.client.resources.weather.HourlyForecastResource;
import fr.cvlaminck.hwweather.client.resources.weather.WeatherConditionResource;
import fr.cvlaminck.hwweather.data.model.weather.CurrentWeatherEntity;
import fr.cvlaminck.hwweather.data.model.weather.DailyForecastEntity;
import fr.cvlaminck.hwweather.data.model.weather.HourlyForecastEntity;
import fr.cvlaminck.hwweather.data.model.weather.WeatherConditionEntity;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
public class WeatherDataConverter {

    public CurrentWeatherResource getResourceFrom(CurrentWeatherEntity entity) {
        CurrentWeatherResource resource = new CurrentWeatherResource();
        resource.setDate(entity.getRefreshTime().toInstant(ZoneOffset.UTC).toEpochMilli());
        resource.setTemperatureInCelsius(entity.getTemperatureInCelsius());
        resource.setWeatherCondition(getResourceFrom(entity.getWeatherCondition()));
        return resource;
    }

    public HourlyForecastResource getResourceFrom(HourlyForecastEntity.ByHourForecast entity) {
        HourlyForecastResource resource = new HourlyForecastResource();
        resource.setDate(entity.getHour().toInstant(ZoneOffset.UTC).toEpochMilli());
        resource.setTemperatureInCelsius(entity.getTemperatureInCelsius());
        resource.setWeatherCondition(getResourceFrom(entity.getWeatherCondition()));
        return resource;
    }

    public DailyForecastResource getResourceFrom(DailyForecastEntity.ByDayForecast entity)  {
        DailyForecastResource resource = new DailyForecastResource();
        resource.setDate(entity.getDay().atTime(0, 0).toInstant(ZoneOffset.UTC).toEpochMilli());
        resource.setMinTemperatureInCelsius(entity.getMinTemperatureInCelsius());
        resource.setMaxTemperatureInCelsius(entity.getMaxTemperatureInCelsius());
        resource.setWeatherCondition(getResourceFrom(entity.getWeatherCondition()));
        return resource;
    }

    public WeatherConditionResource getResourceFrom(WeatherConditionEntity entity) {
        WeatherConditionResource resource = new WeatherConditionResource();
        //FIXME Implements weather condition
        return resource;
    }

}
