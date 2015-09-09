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

    Map<Set<ExternalWeatherDataType>, List<WeatherDataProvider>> buildProvidersByRefreshTypeMap(Collection<WeatherDataProvider> weatherDataProviders) {
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

    private List<RefreshPlan> getOrBuildRefreshPlans(Set<ExternalWeatherDataType> typesToRefresh, Map<Set<ExternalWeatherDataType>, List<WeatherDataProvider>> providersByRefreshTypeMap) {
        //TODO add caching of refresh plans since they are quite expensive to build.
        return buildRefreshPlans(typesToRefresh, providersByRefreshTypeMap);
    }

    List<RefreshPlan> buildRefreshPlans(Set<ExternalWeatherDataType> typesToRefresh, Map<Set<ExternalWeatherDataType>, List<WeatherDataProvider>> providersByRefreshTypeMap) {
        List <RefreshPlan> refreshPlans = new ArrayList<>();
        PartitionOfSetIterator<ExternalWeatherDataType> it = new PartitionOfSetIterator<>(typesToRefresh);
        while (it.hasNext()) {
            refreshPlans.addAll(buildRefreshPlansForPartition(typesToRefresh, it.next(), providersByRefreshTypeMap));
        }
        return refreshPlans;
    }

    List<RefreshPlan> buildRefreshPlansForPartition(Set<ExternalWeatherDataType> typesToRefresh, List<Set<ExternalWeatherDataType>> partition, Map<Set<ExternalWeatherDataType>, List<WeatherDataProvider>> providersByRefreshTypeMap) {
        List<RefreshPlan> refreshPlans = new ArrayList<>();

        List<List<WeatherDataProvider>> providersProvidingPartitionPart = partition.stream()
                .map((p) -> providersByRefreshTypeMap.get(p))
                .collect(Collectors.toList());

        boolean listContainsProviderForAllPartitionPart = providersProvidingPartitionPart.stream()
                .allMatch((p) -> p != null && !p.isEmpty());
        if (!listContainsProviderForAllPartitionPart) {
            return refreshPlans;
        }

        WeatherDataProvider[] providers = new WeatherDataProvider[partition.size()];
        Iterator<WeatherDataProvider>[] iterators = new Iterator[partition.size()];

        int i = 0;
        iterators[0] = providersByRefreshTypeMap.get(partition.get(0)).iterator();
        while (true) {
            if (!iterators[i].hasNext()) {
                iterators[i] = null;
                if (i == 0) {
                    break;
                }
                i --;
            } else {
                providers[i] = iterators[i].next();
                //If the provider is already in the list of provider we will user
                //TODO refactor in two boolean functions
                boolean isAlreadyInList = false;
                int j = i - 1;
                while (!isAlreadyInList && j >= 0) {
                    if (providers[i] == providers[j]) {
                        isAlreadyInList = true;
                    }
                    j --;
                }
                if (isAlreadyInList) {
                    continue;
                }
                //If the provider can refresh all the data by himself, it is useless to associate him to another provider
                //in case of a partition of more than 1 element.
                if (partition.size() > 1 && providers[i].getTypes().containsAll(typesToRefresh)) {
                    continue;
                }
                if (i != partition.size() - 1) {
                    iterators[i + 1] = providersProvidingPartitionPart.get(i + 1).iterator();
                    i++;
                } else {
                    refreshPlans.add(new RefreshPlan(providers));
                }
            }
        }

        return refreshPlans;
    }

    private void sortPlansByDescendingCost() {

    }

    /* package */ static class RefreshPlan {

        private List<WeatherDataProvider> providersToUse;

        public RefreshPlan(List<WeatherDataProvider> providersToUse) {
            this.providersToUse = providersToUse;
        }

        public RefreshPlan(WeatherDataProvider[] providers) {
            this.providersToUse = new ArrayList<>();
            for (WeatherDataProvider p : providers) {
                this.providersToUse.add(p);
            }
        }

        public List<WeatherDataProvider> getProvidersToUse() {
            return Collections.unmodifiableList(providersToUse);
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
