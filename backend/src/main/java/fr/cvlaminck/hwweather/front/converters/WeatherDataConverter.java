package fr.cvlaminck.hwweather.front.converters;

import fr.cvlaminck.hwweather.client.protocol.CurrentWeatherResource;
import fr.cvlaminck.hwweather.client.protocol.DailyForecastResource;
import fr.cvlaminck.hwweather.client.protocol.HourlyForecastResource;
import fr.cvlaminck.hwweather.client.protocol.WeatherConditionResource;
import fr.cvlaminck.hwweather.core.utils.DateUtils;
import fr.cvlaminck.hwweather.data.model.weather.CurrentWeatherEntity;
import fr.cvlaminck.hwweather.data.model.weather.DailyForecastEntity;
import fr.cvlaminck.hwweather.data.model.weather.HourlyForecastEntity;
import fr.cvlaminck.hwweather.data.model.weather.WeatherConditionEntity;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
public class WeatherDataConverter {

    public CurrentWeatherResource getResourceFrom(CurrentWeatherEntity entity) {
        CurrentWeatherResource.Builder resourceBuilder = CurrentWeatherResource.newBuilder();
        resourceBuilder.setDate(DateUtils.toTimestamp(entity.getRefreshTime()));
        resourceBuilder.setTemperatureInCelsius(entity.getTemperatureInCelsius());
        resourceBuilder.setWeatherCondition(getResourceFrom(entity.getWeatherCondition()));
        return resourceBuilder.build();
    }

    public HourlyForecastResource getResourceFrom(HourlyForecastEntity.ByHourForecast entity) {
        HourlyForecastResource.Builder resourceBuilder = HourlyForecastResource.newBuilder();
        resourceBuilder.setDate(DateUtils.toTimestamp(entity.getHour()));
        resourceBuilder.setTemperatureInCelsius(entity.getTemperatureInCelsius());
        resourceBuilder.setWeatherCondition(getResourceFrom(entity.getWeatherCondition()));
        return resourceBuilder.build();
    }

    public DailyForecastResource getResourceFrom(DailyForecastEntity.ByDayForecast entity) {
        DailyForecastResource.Builder resourceBuilder = DailyForecastResource.newBuilder();
        resourceBuilder.setDate(DateUtils.toTimestamp(entity.getDay().atTime(0, 0)));
        resourceBuilder.setMinTemperatureInCelsius(entity.getMinTemperatureInCelsius());
        resourceBuilder.setMaxTemperatureInCelsius(entity.getMaxTemperatureInCelsius());
        resourceBuilder.setWeatherCondition(getResourceFrom(entity.getWeatherCondition()));
        return resourceBuilder.build();
    }

    public WeatherConditionResource getResourceFrom(WeatherConditionEntity entity) {
        WeatherConditionResource.Builder resourceBuilder = WeatherConditionResource.newBuilder();
        //FIXME Implements weather condition
        return resourceBuilder.build();
    }

}
