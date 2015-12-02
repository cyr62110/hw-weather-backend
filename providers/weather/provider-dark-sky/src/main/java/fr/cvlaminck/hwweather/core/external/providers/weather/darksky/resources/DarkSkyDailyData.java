package fr.cvlaminck.hwweather.core.external.providers.weather.darksky.resources;

import java.util.Collection;

public class DarkSkyDailyData {
    private String summary;
    private String icon;
    private Collection<Data> data;

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

    public Collection<Data> getData() {
        return data;
    }

    public void setData(Collection<Data> data) {
        this.data = data;
    }

    public static class Data
            extends DarkSkyData {
        private long sunriseTime;
        private long sunsetTime;
        private double moonPhase;
        private double precipIntensityMax;
        private double temperatureMin;
        private double apparentTemperatureMin;
        private long apparentTemperatureMinTime;
        private double temperatureMax;
        private double apparentTemperatureMax;
        private long apparentTemperatureMaxTime;

        public long getSunriseTime() {
            return sunriseTime;
        }

        public void setSunriseTime(long sunriseTime) {
            this.sunriseTime = sunriseTime;
        }

        public long getSunsetTime() {
            return sunsetTime;
        }

        public void setSunsetTime(long sunsetTime) {
            this.sunsetTime = sunsetTime;
        }

        public double getMoonPhase() {
            return moonPhase;
        }

        public void setMoonPhase(double moonPhase) {
            this.moonPhase = moonPhase;
        }

        public double getPrecipIntensityMax() {
            return precipIntensityMax;
        }

        public void setPrecipIntensityMax(double precipIntensityMax) {
            this.precipIntensityMax = precipIntensityMax;
        }

        public double getTemperatureMin() {
            return temperatureMin;
        }

        public void setTemperatureMin(double temperatureMin) {
            this.temperatureMin = temperatureMin;
        }

        public double getApparentTemperatureMin() {
            return apparentTemperatureMin;
        }

        public void setApparentTemperatureMin(double apparentTemperatureMin) {
            this.apparentTemperatureMin = apparentTemperatureMin;
        }

        public long getApparentTemperatureMinTime() {
            return apparentTemperatureMinTime;
        }

        public void setApparentTemperatureMinTime(long apparentTemperatureMinTime) {
            this.apparentTemperatureMinTime = apparentTemperatureMinTime;
        }

        public double getTemperatureMax() {
            return temperatureMax;
        }

        public void setTemperatureMax(double temperatureMax) {
            this.temperatureMax = temperatureMax;
        }

        public double getApparentTemperatureMax() {
            return apparentTemperatureMax;
        }

        public void setApparentTemperatureMax(double apparentTemperatureMax) {
            this.apparentTemperatureMax = apparentTemperatureMax;
        }

        public long getApparentTemperatureMaxTime() {
            return apparentTemperatureMaxTime;
        }

        public void setApparentTemperatureMaxTime(long apparentTemperatureMaxTime) {
            this.apparentTemperatureMaxTime = apparentTemperatureMaxTime;
        }
    }
}
