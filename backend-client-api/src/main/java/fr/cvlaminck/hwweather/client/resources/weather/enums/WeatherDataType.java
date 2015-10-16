package fr.cvlaminck.hwweather.client.resources.weather.enums;

public enum WeatherDataType {
    CURRENT("CURRENT"),
    DAILY("DAILY"),
    HOURLY("HOURLY");

    private String name;

    WeatherDataType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
