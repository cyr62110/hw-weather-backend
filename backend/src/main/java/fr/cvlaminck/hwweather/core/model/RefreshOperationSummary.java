package fr.cvlaminck.hwweather.core.model;

import fr.cvlaminck.hwweather.data.model.WeatherDataType;
import fr.cvlaminck.hwweather.data.model.city.CityEntity;

import java.util.Collection;
import java.util.Collections;

public class RefreshOperationSummary {
    private CityEntity city;
    private Collection<WeatherDataType> typesToRefresh = Collections.emptyList();
    private Collection<WeatherDataType> refreshedTypes = Collections.emptyList();

    private int numberOfProviderCalled = 0;
    private int numberOfFreeCallUsed = 0;
    private double operationCost = 0d;

    public CityEntity getCity() {
        return city;
    }

    public void setCity(CityEntity city) {
        this.city = city;
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
