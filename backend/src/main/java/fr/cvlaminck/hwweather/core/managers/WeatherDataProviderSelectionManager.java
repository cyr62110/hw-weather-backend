package fr.cvlaminck.hwweather.core.managers;

import fr.cvlaminck.hwweather.core.external.model.weather.ExternalWeatherDataType;
import fr.cvlaminck.hwweather.core.external.providers.weather.WeatherDataProvider;
import fr.cvlaminck.hwweather.data.model.FreeCallCountersEntity;
import fr.cvlaminck.hwweather.data.model.WeatherDataType;
import fr.cvlaminck.hwweather.data.repositories.FreeCallCountersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class WeatherDataProviderSelectionManager {

    @Autowired
    private Collection<WeatherDataProvider> weatherDataProviders;

    @Autowired
    private FreeCallCountersRepository freeCallCountersRepository;

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

    private List<RefreshPlan> buildRefreshPlans(Collection<WeatherDataType> typesToRefresh) {
        //FIXME build all possible lists of data providers that can refresh all types
        //FIXME fix the maximum in config.
        return null; //FIXME
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
        }

    }

}
