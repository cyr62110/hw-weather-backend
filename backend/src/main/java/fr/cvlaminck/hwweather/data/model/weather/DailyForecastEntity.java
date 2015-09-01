package fr.cvlaminck.hwweather.data.model.weather;

import fr.cvlaminck.hwweather.data.model.ExpirableEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Document(collection = "weather.daily")
public class DailyForecastEntity
    extends ExpirableEntity {

    @Id
    private String id;

    @Indexed
    private String cityId;

    @Indexed(expireAfterSeconds = (int) (7.5 * 24 * 60 * 60))
    private Date week;

    private Collection<ByDayForecast> dayByDayForecasts = Collections.emptyList();

    public String getId() {
        return id;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public Date getWeek() {
        return week;
    }

    public void setWeek(Date week) {
        this.week = week;
    }

    public Collection<ByDayForecast> getDayByDayForecasts() {
        return dayByDayForecasts;
    }

    public void setDayByDayForecasts(Collection<ByDayForecast> dayByDayForecasts) {
        this.dayByDayForecasts = dayByDayForecasts;
    }

    public static class ByDayForecast {

        private Date day;

        private double minTemperature;

        private double maxTemperature;

        private WeatherConditionEntity weatherCondition;

        public Date getDay() {
            return day;
        }

        public void setDay(Date day) {
            this.day = day;
        }

        public double getMinTemperature() {
            return minTemperature;
        }

        public void setMinTemperature(double minTemperature) {
            this.minTemperature = minTemperature;
        }

        public double getMaxTemperature() {
            return maxTemperature;
        }

        public void setMaxTemperature(double maxTemperature) {
            this.maxTemperature = maxTemperature;
        }

        public WeatherConditionEntity getWeatherCondition() {
            return weatherCondition;
        }

        public void setWeatherCondition(WeatherConditionEntity weatherCondition) {
            this.weatherCondition = weatherCondition;
        }
    }
}
