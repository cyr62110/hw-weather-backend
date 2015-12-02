package fr.cvlaminck.hwweather.core.external.providers.weather.darksky.resources;

public class DarkSkyForecastResponse {

    private double latitude;
    private double longitude;
    private String timezone;
    private long offset;

    private DarkSkyCurrentlyData currently;
    private DarkSkyDailyData daily;
    private DarkSkyHourlyData hourly;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public DarkSkyCurrentlyData getCurrently() {
        return currently;
    }

    public void setCurrently(DarkSkyCurrentlyData currently) {
        this.currently = currently;
    }

    public DarkSkyDailyData getDaily() {
        return daily;
    }

    public void setDaily(DarkSkyDailyData daily) {
        this.daily = daily;
    }

    public DarkSkyHourlyData getHourly() {
        return hourly;
    }

    public void setHourly(DarkSkyHourlyData hourly) {
        this.hourly = hourly;
    }
}
