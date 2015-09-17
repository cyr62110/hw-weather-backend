package fr.cvlaminck.hwweather.client.resources.weather;

/*package*/ abstract class AbstractWeatherResource {
    private WeatherConditionResource weatherCondition;
    private long date;

    public WeatherConditionResource getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(WeatherConditionResource weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
