package fr.cvlaminck.hwweather.core.external.providers.weather.darksky.resources;

public class DarkSkyCurrentlyData
    extends DarkSkyData {
    private double temperature;
    private double apparentTemperature;
    private int nearestStormDistance;
    private int nearestStormBearing;

    @Override
    public double getTemperature() {
        return temperature;
    }

    @Override
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    @Override
    public double getApparentTemperature() {
        return apparentTemperature;
    }

    @Override
    public void setApparentTemperature(double apparentTemperature) {
        this.apparentTemperature = apparentTemperature;
    }

    public int getNearestStormDistance() {
        return nearestStormDistance;
    }

    public void setNearestStormDistance(int nearestStormDistance) {
        this.nearestStormDistance = nearestStormDistance;
    }

    public int getNearestStormBearing() {
        return nearestStormBearing;
    }

    public void setNearestStormBearing(int nearestStormBearing) {
        this.nearestStormBearing = nearestStormBearing;
    }
}
