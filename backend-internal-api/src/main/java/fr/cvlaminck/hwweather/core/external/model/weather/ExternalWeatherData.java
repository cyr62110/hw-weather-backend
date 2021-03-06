package fr.cvlaminck.hwweather.core.external.model.weather;

import java.util.Collection;

public class ExternalWeatherData {
    private ExternalCurrentWeatherResource current = null;
    private Collection<ExternalDailyForecastResource> daily = null;
    private Collection<ExternalHourlyForecastResource> hourly = null;

    private Metadata metadata = new Metadata();

    public boolean contains(ExternalWeatherDataType type) {
        switch (type) {
            case CURRENT:
                return current != null;
            case DAILY:
                return daily != null && !daily.isEmpty();
            case HOURLY:
                return hourly != null && !hourly.isEmpty();
        }
        return false;
    }

    public ExternalCurrentWeatherResource getCurrent() {
        return current;
    }

    public void setCurrent(ExternalCurrentWeatherResource current) {
        this.current = current;
    }

    public Collection<ExternalDailyForecastResource> getDaily() {
        return daily;
    }

    public void setDaily(Collection<ExternalDailyForecastResource> daily) {
        this.daily = daily;
    }

    public Collection<ExternalHourlyForecastResource> getHourly() {
        return hourly;
    }

    public void setHourly(Collection<ExternalHourlyForecastResource> hourly) {
        this.hourly = hourly;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public static class Metadata {
        private int numberOfProviderCalled = 0;
        private int numberOfFreeCallUsed = 0;
        private double operationCost = 0d;

        public int getNumberOfProviderCalled() {
            return numberOfProviderCalled;
        }

        public void setNumberOfProviderCalled(int numberOfProviderCalled) {
            this.numberOfProviderCalled = numberOfProviderCalled;
        }

        public int getNumberOfFreeCallUsed() {
            return numberOfFreeCallUsed;
        }

        public void setNumberOfFreeCallUsed(int numberOfFreeCallUsed) {
            this.numberOfFreeCallUsed = numberOfFreeCallUsed;
        }

        public double getOperationCost() {
            return operationCost;
        }

        public void setOperationCost(double operationCost) {
            this.operationCost = operationCost;
        }
    }
}
