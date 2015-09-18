package fr.cvlaminck.hwweather.core.model;

import fr.cvlaminck.hwweather.core.external.providers.weather.WeatherDataProvider;

import java.util.Collection;
import java.util.Collections;

public class WeatherProvidersSelectionResult {
    private Collection<WeatherDataProvider> providersToUse = Collections.emptyList();

    private int numberOfFreeCallUsed = 0;
    private double operationCost = 0d;

    public Collection<WeatherDataProvider> getProvidersToUse() {
        return providersToUse;
    }

    public void setProvidersToUse(Collection<WeatherDataProvider> providersToUse) {
        this.providersToUse = providersToUse;
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
