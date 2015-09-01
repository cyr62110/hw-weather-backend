package fr.cvlaminck.hwweather.data.model.weather;

public class WeatherConditionEntity {
    public enum PrimaryWeatherCondition {
        CLEAR,
        RAIN,
        STORM,
    }
    public enum SecondaryWeatherCondition {
        PARTLY_CLOUDY,
    }

    private PrimaryWeatherCondition primary;
    private SecondaryWeatherCondition secondary;

    public PrimaryWeatherCondition getPrimary() {
        return primary;
    }

    public void setPrimary(PrimaryWeatherCondition primary) {
        this.primary = primary;
    }

    public SecondaryWeatherCondition getSecondary() {
        return secondary;
    }

    public void setSecondary(SecondaryWeatherCondition secondary) {
        this.secondary = secondary;
    }
}
