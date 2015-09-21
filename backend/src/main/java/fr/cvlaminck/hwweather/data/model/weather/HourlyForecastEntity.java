package fr.cvlaminck.hwweather.data.model.weather;

import fr.cvlaminck.hwweather.data.model.WeatherDataType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Document(collection = "hourly")
public class HourlyForecastEntity
    extends AbstractWeatherDataEntity {

    @Id
    private String id;

    @Indexed
    private String cityId;

    @Indexed
    private LocalDate day;

    public HourlyForecastEntity() {
    }

    public HourlyForecastEntity(int expiryInSeconds, int gracePeriodInSeconds) {
        super(expiryInSeconds, gracePeriodInSeconds);
    }

    @Override
    public WeatherDataType getType() {
        return WeatherDataType.HOURLY_FORECAST;
    }

    private Collection<ByHourForecast> hourByHourForecasts = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public LocalDate getDay() {
        return day;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }

    public Collection<ByHourForecast> getHourByHourForecasts() {
        return hourByHourForecasts;
    }

    public void setHourByHourForecasts(Collection<ByHourForecast> hourByHourForecasts) {
        this.hourByHourForecasts = hourByHourForecasts;
    }

    public static class ByHourForecast {

        private LocalDateTime hour;

        private double temperatureInCelsius;

        private WeatherConditionEntity weatherCondition;

        public LocalDateTime getHour() {
            return hour;
        }

        public void setHour(LocalDateTime hour) {
            this.hour = hour;
        }

        public double getTemperatureInCelsius() {
            return temperatureInCelsius;
        }

        public void setTemperatureInCelsius(double temperatureInCelsius) {
            this.temperatureInCelsius = temperatureInCelsius;
        }

        public WeatherConditionEntity getWeatherCondition() {
            return weatherCondition;
        }

        public void setWeatherCondition(WeatherConditionEntity weatherCondition) {
            this.weatherCondition = weatherCondition;
        }
    }
}
