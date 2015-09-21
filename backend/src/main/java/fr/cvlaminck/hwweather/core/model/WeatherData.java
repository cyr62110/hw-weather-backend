package fr.cvlaminck.hwweather.core.model;

import fr.cvlaminck.hwweather.data.model.ExpirableEntity;
import fr.cvlaminck.hwweather.data.model.WeatherDataType;
import fr.cvlaminck.hwweather.data.model.city.CityEntity;
import fr.cvlaminck.hwweather.data.model.weather.CurrentWeatherEntity;
import fr.cvlaminck.hwweather.data.model.weather.DailyForecastEntity;
import fr.cvlaminck.hwweather.data.model.weather.HourlyForecastEntity;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class WeatherData {

    private CityEntity city;

    private Collection<WeatherDataType> types = Collections.emptyList();

    private CurrentWeatherEntity current;

    private Collection<HourlyForecastEntity> hourlyList = Collections.emptyList();

    private Collection<DailyForecastEntity> dailyList = Collections.emptyList();

    private Metadata metadata = new Metadata();

    public Collection<WeatherDataType> getMissingTypes() {
        return types.stream()
                .filter((t) -> {
                    switch (t) {
                        case WEATHER:
                            return current == null;
                        case HOURLY_FORECAST:
                            return hourlyList.isEmpty();
                        case DAILY_FORECAST:
                            return dailyList.isEmpty();
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    public Collection<WeatherDataType> getMissingOrInGracePeriodTypes() {
        return types.stream()
                .filter((t) -> {
                    switch (t) {
                        case WEATHER:
                            return current == null || current.isInGracePeriod();
                        case HOURLY_FORECAST:
                            return hourlyList.isEmpty() || hourlyList.stream().anyMatch(ExpirableEntity::isInGracePeriod);
                        case DAILY_FORECAST:
                            return dailyList.isEmpty() || dailyList.stream().anyMatch(ExpirableEntity::isInGracePeriod);
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    public CityEntity getCity() {
        return city;
    }

    public void setCity(CityEntity city) {
        this.city = city;
    }

    public CurrentWeatherEntity getCurrent() {
        return current;
    }

    public void setCurrent(CurrentWeatherEntity current) {
        if (current != null && !current.isExpired()) {
            this.current = current;
        } else {
            this.current = null;
        }
    }

    public Collection<WeatherDataType> getTypes() {
        return types;
    }

    public void setTypes(Collection<WeatherDataType> types) {
        this.types = types;
    }

    public Collection<HourlyForecastEntity> getHourlyList() {
        return Collections.unmodifiableCollection(hourlyList);
    }

    public void setHourlyList(Collection<HourlyForecastEntity> hourlyList) {
        if (hourlyList != null && !hourlyList.stream().anyMatch(ExpirableEntity::isExpired)) {
            this.hourlyList = hourlyList;
        } else {
            this.hourlyList = Collections.emptyList();
        }
    }

    public Collection<DailyForecastEntity> getDailyList() {
        return Collections.unmodifiableCollection(dailyList);
    }

    public void setDailyList(Collection<DailyForecastEntity> dailyList) {
        if (dailyList != null && !dailyList.stream().anyMatch(ExpirableEntity::isExpired)) {
            this.dailyList = dailyList;
        } else {
            this.dailyList = Collections.emptyList();
        }
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public static class Metadata {
        private boolean hasCausedRefreshOperation = false;
        private boolean hasWaitedForRefreshOperationToFinish = false;

        private Collection<WeatherDataType> typesToRefresh = Collections.emptySet();

        private Collection<WeatherDataType> refreshedTypes = Collections.emptySet();
        private int numberOfProviderCalled = 0;
        private int numberOfFreeCallUsed = 0;
        private double operationCost = 0d;

        public boolean hasCausedRefreshOperation() {
            return hasCausedRefreshOperation;
        }

        public void setHasCausedRefreshOperation(boolean hasCausedRefreshOperation) {
            this.hasCausedRefreshOperation = hasCausedRefreshOperation;
        }

        public boolean hasWaitedForRefreshOperationToFinish() {
            return hasWaitedForRefreshOperationToFinish;
        }

        public void setHasWaitedForRefreshOperationToFinish(boolean hasWaitedForRefreshOperationToFinish) {
            this.hasWaitedForRefreshOperationToFinish = hasWaitedForRefreshOperationToFinish;
        }

        public Collection<WeatherDataType> getTypesToRefresh() {
            return typesToRefresh;
        }

        public void setTypesToRefresh(Collection<WeatherDataType> typesToRefresh) {
            this.typesToRefresh = typesToRefresh;
        }

        public Collection<WeatherDataType> getRefreshedTypes() {
            return refreshedTypes;
        }

        public void setRefreshedTypes(Collection<WeatherDataType> refreshedTypes) {
            this.refreshedTypes = refreshedTypes;
        }

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
