package fr.cvlaminck.hwweather.data.model.weather;

import fr.cvlaminck.hwweather.data.model.WeatherDataType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

@Document(collection = "daily")
public class DailyForecastEntity
    extends AbstractWeatherDataEntity {

    @Id
    private String id;

    @Indexed
    private String cityId;

    @Indexed
    private LocalDate week;

    private Collection<ByDayForecast> dayByDayForecasts = new ArrayList<>();

    public DailyForecastEntity() {
    }

    public DailyForecastEntity(int expiryInSeconds, int gracePeriodInSeconds) {
        super(expiryInSeconds, gracePeriodInSeconds);
    }

    @Override
    public WeatherDataType getType() {
        return WeatherDataType.DAILY_FORECAST;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public LocalDate getWeek() {
        return week;
    }

    public void setWeek(LocalDate week) {
        this.week = week;
    }

    public Collection<ByDayForecast> getDayByDayForecasts() {
        return dayByDayForecasts;
    }

    public void setDayByDayForecasts(Collection<ByDayForecast> dayByDayForecasts) {
        this.dayByDayForecasts = dayByDayForecasts;
    }

    public static class ByDayForecast {

        private LocalDate day;

        private double minTemperatureInCelsius;

        private double maxTemperatureInCelsius;

        private WeatherConditionEntity weatherCondition;

        public LocalDate getDay() {
            return day;
        }

        public void setDay(LocalDate day) {
            this.day = day;
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

        public WeatherConditionEntity getWeatherCondition() {
            return weatherCondition;
        }

        public void setWeatherCondition(WeatherConditionEntity weatherCondition) {
            this.weatherCondition = weatherCondition;
        }
    }
}
