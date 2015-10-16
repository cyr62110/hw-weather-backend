package fr.cvlaminck.hwweather.data.model.weather;

import fr.cvlaminck.hwweather.data.model.WeatherDataType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "current")
public class CurrentWeatherEntity
        extends AbstractWeatherDataEntity {

    @Id
    private String id;

    @Indexed
    private String cityId;

    @Indexed
    private LocalDate day;

    private double temperatureInCelsius;

    private WeatherConditionEntity weatherCondition;

    public CurrentWeatherEntity() {
    }

    public CurrentWeatherEntity(int expiryInSeconds, int gracePeriodInSeconds) {
        super(expiryInSeconds, gracePeriodInSeconds);
    }

    @Override
    public WeatherDataType getType() {
        return WeatherDataType.WEATHER;
    }

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
