package fr.cvlaminck.hwweather.core.messages;

public class WeatherRefreshOperationMessage {
    public static final String QUEUE = "weather-refresh-operation";

    private String cityId;

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }
}
