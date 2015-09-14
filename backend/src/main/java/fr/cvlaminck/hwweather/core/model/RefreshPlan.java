package fr.cvlaminck.hwweather.core.model;

import fr.cvlaminck.hwweather.core.external.providers.weather.WeatherDataProvider;
import fr.cvlaminck.hwweather.data.model.FreeCallCountersEntity;

import java.util.*;
import java.util.stream.Collectors;

public class RefreshPlan {
    private Set<WeatherDataProvider> providersToUse;

    RefreshPlan(List<WeatherDataProvider> providersToUse) {
        this.providersToUse = providersToUse.stream().collect(Collectors.toSet());
    }

    public RefreshPlan(WeatherDataProvider[] providers) {
        this.providersToUse = new HashSet<>();
        for (WeatherDataProvider p : providers) {
            this.providersToUse.add(p);
        }
    }

    public Set<WeatherDataProvider> getProvidersToUse() {
        return Collections.unmodifiableSet(providersToUse);
    }

    public Collection<String> getFreeCalls(FreeCallCountersEntity freeCallCounters) {
        return providersToUse.stream()
                .filter((p) -> freeCallCounters.getCounters().get(p.getProviderName()) > 0)
                .map(WeatherDataProvider::getProviderName)
                .collect(Collectors.toList());
    }

    public int getNumberOfProvider() {
        return providersToUse.size();
    }

    /**
     * Returns false if one of the provider required for this refresh plan cannot be called anymore.
     */
    public boolean canAllProvidersBeCalled(FreeCallCountersEntity freeCallCounters) {
        //TODO Add configuration to say if we authorize paid call for a given provider
        return providersToUse.stream()
                .filter((p) -> freeCallCounters.getCounters().get(p.getProviderName()) <= 0)
                .allMatch(WeatherDataProvider::supportsPaidCall);
    }

    /**
     * Returns the number of times a type will be refreshed by more
     * than one data provider in this plan.
     * <p>
     * ex: p1(CURRENT, DAILY); p2(CURRENT, MONTHLY) -> returns 1 since CURRENT will be refresh twice.
     */
    public int getOverlap() {
        return providersToUse.stream()
                .flatMap((provider) -> provider.getTypes().stream())
                .collect(Collectors.toMap(
                        (type) -> type,
                        (value) -> 1,
                        (a, b) -> a + b
                ))
                .entrySet().stream()
                .collect(Collectors.summingInt(entry -> entry.getValue() - 1));
    }

    /**
     * Returns the cost of this refresh plan
     */
    public double getCost(FreeCallCountersEntity freeCallCounters) {
        return providersToUse.stream()
                .filter((p) -> freeCallCounters.getCounters().get(p.getProviderName()) <= 0)
                .collect(Collectors.summingDouble(WeatherDataProvider::getCostPerOperation));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RefreshPlan)) return false;
        RefreshPlan that = (RefreshPlan) o;
        return Objects.equals(providersToUse, that.providersToUse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(providersToUse);
    }
}
