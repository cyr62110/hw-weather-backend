package fr.cvlaminck.hwweather.core.managers;

import fr.cvlaminck.hwweather.core.external.model.weather.ExternalWeatherDataType;
import fr.cvlaminck.hwweather.core.external.providers.weather.WeatherDataProvider;
import fr.cvlaminck.hwweather.core.utils.stats.KSubsetOfNSetIterator;
import fr.cvlaminck.hwweather.core.utils.stats.PartitionOfSetIterator;
import fr.cvlaminck.hwweather.data.model.FreeCallCountersEntity;
import fr.cvlaminck.hwweather.data.model.WeatherDataType;
import fr.cvlaminck.hwweather.data.repositories.FreeCallCountersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class WeatherDataProviderSelectionManager {

    private Collection<WeatherDataProvider> weatherDataProviders = Collections.emptyList();

    private Map<Set<ExternalWeatherDataType>, List<WeatherDataProvider>> providersByRefreshTypeMap = Collections.emptyMap();

    @Autowired
    private FreeCallCountersRepository freeCallCountersRepository;

    @Autowired
    public void setWeatherDataProviders(Collection<WeatherDataProvider> weatherDataProviders) {
        this.weatherDataProviders = weatherDataProviders;
        this.providersByRefreshTypeMap = buildProvidersByRefreshTypeMap(weatherDataProviders);
    }

    private Map<Set<ExternalWeatherDataType>, List<WeatherDataProvider>> buildProvidersByRefreshTypeMap(Collection<WeatherDataProvider> weatherDataProviders) {
        Map<Set<ExternalWeatherDataType>, List<WeatherDataProvider>> providersByRefreshTypeMap = new HashMap<>();
        Set<ExternalWeatherDataType> types = Arrays.asList(ExternalWeatherDataType.values()).stream().collect(Collectors.toSet());
        for (int i = 1; i <= types.size(); i++) {
            final int j = i;
            KSubsetOfNSetIterator<ExternalWeatherDataType> it = new KSubsetOfNSetIterator<>(types, i);
            it.forEachRemaining((subset) -> {
                List<WeatherDataProvider> providersProvidingSubsetOfTypes = weatherDataProviders.stream()
                        .filter((p) -> p.getTypes().containsAll(subset))
                        .collect(Collectors.toList());
                providersByRefreshTypeMap.put(subset, providersProvidingSubsetOfTypes);
            });
        }
        return providersByRefreshTypeMap;
    }

    public List<WeatherDataProvider> selectDataProvidersToUseForRefreshOperation(Collection<WeatherDataType> typesToRefresh) {
        FreeCallCountersEntity freeCallCounters = freeCallCountersRepository.findFreeCallsLeftForToday();
        //Score operation by cost and number of API to call. Also by number of types refreshed even if they are not required.
        //Take the best options
        //Try to decrement all counters for all free data provider that will be called
        //If free calls for an API are exhausted, update the cost of the operation and back to the selection of the best.
        //If the exhausted free API does not have paid option, then remove this solution from the list.
        return weatherDataProviders.stream()
                .collect(Collectors.toList()); //FIXME
    }

    private List<RefreshPlan> buildRefreshPlans(Set<ExternalWeatherDataType> typesToRefresh) {
        List <RefreshPlan> refreshPlans = new ArrayList<>();
        //First, we build all possible combinaison of providers that will be able to refresh the requested types.
        PartitionOfSetIterator<ExternalWeatherDataType> it = new PartitionOfSetIterator<>(typesToRefresh);
        while (it.hasNext()) {
            refreshPlans.addAll(buildRefreshPlansForPartition(it.next()));
        }
        //FIXME fix the maximum in config.
        return refreshPlans;
    }

    private List<RefreshPlan> buildRefreshPlansForPartition(List<Set<ExternalWeatherDataType>> partition) {
        List<RefreshPlan> refreshPlans = new ArrayList<>();
        //FIXME
        return refreshPlans;
    }

    private void sortPlansByDescendingCost() {

    }

    /* package */ static class RefreshPlan {

        private List<WeatherDataProvider> providersToUse;

        public RefreshPlan(List<WeatherDataProvider> providersToUse) {
            this.providersToUse = providersToUse;
        }

        public int getNumberOfProvider() {
            return providersToUse.size();
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

        public double getCost() {
            //FIXME
            return 0d;
        }

    }

}
