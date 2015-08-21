package fr.cvlaminck.hwweather.core.external.providers.weather.darksky.resources;

public class DarkSkyResponse {

    private double latitude;
    private double longitude;
    private String timezone;
    private long offset;

    private DarkSkyCurrentlyData currently;
    private DarkSkyHourlyData hourly;

}
