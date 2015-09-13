package fr.cvlaminck.hwweather.core.managers;

import fr.cvlaminck.hwweather.core.exceptions.NoProviderAvailableForRefreshOperationException;
import fr.cvlaminck.hwweather.core.external.model.weather.ExternalWeatherDataType;
import fr.cvlaminck.hwweather.core.external.providers.weather.WeatherDataProvider;
import fr.cvlaminck.hwweather.core.utils.stats.KSubsetOfNSetIterator;
import fr.cvlaminck.hwweather.core.utils.stats.PartitionOfSetIterator;
import fr.cvlaminck.hwweather.data.model.FreeCallCountersEntity;
import fr.cvlaminck.hwweather.data.model.WeatherDataType;
import fr.cvlaminck.hwweather.data.model.city.CityEntity;
import fr.cvlaminck.hwweather.data.repositories.FreeCallCountersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Ref;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class WeatherDataProviderSelectionManager {

    private Logger log = LoggerFactory.getLogger(WeatherDataProviderSelectionManager.class);

    private Collection<WeatherDataProvider> weatherDataProviders = Collections.emptyList();

    private Map<Set<ExternalWeatherDataType>, List<WeatherDataProvider>> providersByRefreshTypeMap = Collections.emptyMap();

    private FreeCallCountersRepository freeCallCountersRepository;

    @Autowired
    public void setFreeCallCountersRepository(FreeCallCountersRepository freeCallCountersRepository) {
        this.freeCallCountersRepository = freeCallCountersRepository;
    }

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

    public List<WeatherDataProvider> selectDataProvidersToUseForRefreshOperation(Collection<WeatherDataType> typesToRefresh) throws NoProviderAvailableForRefreshOperationException {
        //We build all possible refresh plan for the types we want to refresh
        Set<RefreshPlan> refreshPlans = getOrBuildRefreshPlans(convertTypes(typesToRefresh), providersByRefreshTypeMap);
        RefreshPlan refreshPlan = null;

        while (refreshPlan == null) {
            FreeCallCountersEntity freeCallCounters = freeCallCountersRepository.findFreeCallsLeftForToday();

            //Then, we select the best plan
            Optional<RefreshPlan> bestRefreshPlan = findBestRefreshPlan(refreshPlans, freeCallCounters);
            if (!bestRefreshPlan.isPresent()) {
                throw new NoProviderAvailableForRefreshOperationException();
            }
            refreshPlan = bestRefreshPlan.get();

            //We try to reserve the free calls for providers
            Collection<String> freeCallsRequiredForRefreshPlan = bestRefreshPlan.get().getFreeCalls(freeCallCounters);
            if (!freeCallsRequiredForRefreshPlan.isEmpty()) {
                freeCallCounters = freeCallCountersRepository.decrement(freeCallsRequiredForRefreshPlan);
                //If free calls are exhausted for some provider, we loop again
                //Counters will be refreshed and we will try to find a new best.
                if (freeCallCounters == null) {
                    refreshPlan = null;
                }
            }
        }
        return refreshPlan.getProvidersToUse().stream().collect(Collectors.toList());
    }

    private Set<ExternalWeatherDataType> convertTypes(Collection<WeatherDataType> types) {
        return types.stream()
                .map((type) -> {
                    switch (type) {
                        case WEATHER: return ExternalWeatherDataType.CURRENT;
                        case HOURLY_FORECAST: return ExternalWeatherDataType.HOURLY;
                        case DAILY_FORECAST: return ExternalWeatherDataType.DAILY;
                    }
                    throw new IllegalArgumentException(); //TODO do proper exception for this case
                })
                .collect(Collectors.toSet());
    }

    private Set<RefreshPlan> getOrBuildRefreshPlans(Set<ExternalWeatherDataType> typesToRefresh, Map<Set<ExternalWeatherDataType>, List<WeatherDataProvider>> providersByRefreshTypeMap) {
        //TODO add caching of refresh plans since they are quite expensive to build.
        return buildRefreshPlans(typesToRefresh, providersByRefreshTypeMap);
    }

    Set<RefreshPlan> buildRefreshPlans(Set<ExternalWeatherDataType> typesToRefresh, Map<Set<ExternalWeatherDataType>, List<WeatherDataProvider>> providersByRefreshTypeMap) {
        Set <RefreshPlan> refreshPlans = new HashSet<>();
        PartitionOfSetIterator<ExternalWeatherDataType> it = new PartitionOfSetIterator<>(typesToRefresh);
        while (it.hasNext()) {
            List<Set<ExternalWeatherDataType>> partition = it.next();
            Set<RefreshPlan> refreshPlansForPartition = buildRefreshPlansForPartition(typesToRefresh, partition, providersByRefreshTypeMap);
            refreshPlans.addAll(refreshPlansForPartition);
        }
        return refreshPlans;
    }

    Set<RefreshPlan> buildRefreshPlansForPartition(Set<ExternalWeatherDataType> typesToRefresh, List<Set<ExternalWeatherDataType>> partition, Map<Set<ExternalWeatherDataType>, List<WeatherDataProvider>> providersByRefreshTypeMap) {
        Set<RefreshPlan> refreshPlans = new HashSet<>();

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
                if (i != partition.size() - 1) {
                    //If we already have selected the provider, we ignore it.
                    if (doesFirstKProvidersContains(providers, i, providers[i])) {
                        continue;
                    }
                    //If the use of currently selected provider is already enough to refresh all requested types,
                    //we do not need to go down another level. So we ignore this provider for this level.
                    if (doesFirstKProvidersRefreshAllRequestedTypes(typesToRefresh, providers, i + 1)) {
                        continue;
                    }
                    iterators[i + 1] = providersProvidingPartitionPart.get(i + 1).iterator();
                    i++;
                } else {
                    //If the final provider can refresh all types by himself, we do not need to have multiple provider
                    //to do the refresh operation, so we ignore it. Unless the partition is composed of only one part.
                    if (partition.size() != 1 && doesProviderCanRefreshAllRequestedTypes(typesToRefresh, providers[i])) {
                        continue;
                    }
                    refreshPlans.add(new RefreshPlan(providers));
                }
            }
        }

        return refreshPlans;
    }

    private boolean doesFirstKProvidersContains(WeatherDataProvider[] providers, int k, WeatherDataProvider provider) {
        for (int i = 0; i < k; i++) {
            if (providers[i] == provider) {
                return true;
            }
        }
        return false;
    }

    private boolean doesFirstKProvidersRefreshAllRequestedTypes(Set<ExternalWeatherDataType> typesToRefresh, WeatherDataProvider[] providers, int k) {
        Set<ExternalWeatherDataType> refreshedTypes = new HashSet<>();
        for (int i = 0; i < k; i ++) {
            refreshedTypes.addAll(providers[i].getTypes());
        }
        return refreshedTypes.containsAll(typesToRefresh);
    }

    private boolean doesProviderCanRefreshAllRequestedTypes(Set<ExternalWeatherDataType> typesToRefresh, WeatherDataProvider provider) {
        return provider.getTypes().containsAll(typesToRefresh);
    }

    private Optional<RefreshPlan> findBestRefreshPlan(Set<RefreshPlan> refreshPlans, FreeCallCountersEntity freeCallCounters) {
         return refreshPlans.stream()
                .filter((p) -> p.canAllProvidersBeCalled(freeCallCounters))
                .sorted((p1, p2) -> {
                    int orderByCost = Double.compare(p1.getCost(freeCallCounters), p2.getCost(freeCallCounters));
                    if (orderByCost != 0) return orderByCost;
                    int orderByNumberOfProvider = Integer.compare(p1.getNumberOfProvider(), p2.getNumberOfProvider());
                    if (orderByNumberOfProvider != 0) return orderByNumberOfProvider;
                    int orderByOverlap = Integer.compare(p1.getOverlap(), p2.getOverlap());
                    if (orderByOverlap != 0) return orderByOverlap;
                    return Integer.compare(p1.hashCode(), p2.hashCode()); //TODO: By number of quer
                })
                .findFirst();
    }

    /* package */ static class RefreshPlan {

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

}
