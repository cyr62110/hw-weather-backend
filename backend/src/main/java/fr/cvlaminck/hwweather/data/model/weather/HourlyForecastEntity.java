package fr.cvlaminck.hwweather.data.model.weather;

import fr.cvlaminck.hwweather.data.model.ExpirableEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Document(collection = "weather.hourly")
public class HourlyForecastEntity
    extends ExpirableEntity {

    @Id
    private String id;

    @Indexed
    private String cityId;

    @Indexed(expireAfterSeconds = 25 * 60 * 60)
    private Date day;

    private Collection<ByHourForecast> hourByHourForecasts = new ArrayList<>();

    public String getId() {
        return id;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public Collection<ByHourForecast> getHourByHourForecasts() {
        return hourByHourForecasts;
    }

    public void setHourByHourForecasts(Collection<ByHourForecast> hourByHourForecasts) {
        this.hourByHourForecasts = hourByHourForecasts;
    }

    public static class ByHourForecast {

        private Date hour;

        private double temperature;

        private WeatherConditionEntity weatherCondition;

        public Date getHour() {
            return hour;
        }

        public void setHour(Date hour) {
            this.hour = hour;
        }

        public double getTemperature() {
            return temperature;
        }

        public void setTemperature(double temperature) {
            this.temperature = temperature;
        }

        public WeatherConditionEntity getWeatherCondition() {
            return weatherCondition;
        }

        public void setWeatherCondition(WeatherConditionEntity weatherCondition) {
            this.weatherCondition = weatherCondition;
        }
    }
}
