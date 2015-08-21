package fr.cvlaminck.hwweather.core.external.providers.weather.darksky.resources;

import java.util.Collection;
import java.util.Collections;

public class DarkSkyHourlyData {
    private String summary;
    private String icon;
    private Collection<Data> data = Collections.emptyList();

    public static class Data
        extends DarkSkyData {
        private double temperature;
        private double apparentTemperature;

        public double getTemperature() {
            return temperature;
        }

        public void setTemperature(double temperature) {
            this.temperature = temperature;
        }

        public double getApparentTemperature() {
            return apparentTemperature;
        }

        public void setApparentTemperature(double apparentTemperature) {
            this.apparentTemperature = apparentTemperature;
        }
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

    public Collection<Data> getData() {
        return data;
    }

    public void setData(Collection<Data> data) {
        this.data = data;
    }
}
