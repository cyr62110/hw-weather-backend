package fr.cvlaminck.hwweather.core.external.providers.weather.darksky.resources;

public abstract class DarkSkyData {
    private long time;
    private String summary;
    private String icon;
    private double precipIntensity;
    private double precipProbability;
    private double dewPoint;
    private double humidity;
    private double windSpeed;
    private double windBearing;
    private double visibility;
    private double cloudCover;
    private double pressure;
    private double ozone;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public double getPrecipIntensity() {
        return precipIntensity;
    }

    public void setPrecipIntensity(double precipIntensity) {
        this.precipIntensity = precipIntensity;
    }

    public double getPrecipProbability() {
        return precipProbability;
    }

    public void setPrecipProbability(double precipProbability) {
        this.precipProbability = precipProbability;
    }

    public double getDewPoint() {
        return dewPoint;
    }

    public void setDewPoint(double dewPoint) {
        this.dewPoint = dewPoint;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public double getWindBearing() {
        return windBearing;
    }

    public void setWindBearing(double windBearing) {
        this.windBearing = windBearing;
    }

    public double getVisibility() {
        return visibility;
    }

    public void setVisibility(double visibility) {
        this.visibility = visibility;
    }

    public double getCloudCover() {
        return cloudCover;
    }

    public void setCloudCover(double cloudCover) {
        this.cloudCover = cloudCover;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getOzone() {
        return ozone;
    }

    public void setOzone(double ozone) {
        this.ozone = ozone;
    }
}
