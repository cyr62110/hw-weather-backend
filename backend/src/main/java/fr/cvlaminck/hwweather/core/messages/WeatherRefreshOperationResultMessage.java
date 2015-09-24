package fr.cvlaminck.hwweather.core.messages;

import fr.cvlaminck.hwweather.data.model.WeatherDataType;

import java.util.Collection;
import java.util.Collections;

public class WeatherRefreshOperationResultMessage {
    private String cityId;
    private Collection<WeatherDataType> refreshedTypes = Collections.emptyList();

    private boolean success;

    private int numberOfProviderCalled = 0;
    private int numberOfFreeCallUsed = 0;
    private double operationCost = 0d;

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public Collection<WeatherDataType> getRefreshedTypes() {
        return refreshedTypes;
    }

    public void setRefreshedTypes(Collection<WeatherDataType> refreshedTypes) {
        this.refreshedTypes = refreshedTypes;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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
