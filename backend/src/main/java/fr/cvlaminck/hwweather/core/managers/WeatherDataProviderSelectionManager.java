package fr.cvlaminck.hwweather.core.managers;

import fr.cvlaminck.hwweather.core.external.providers.weather.WeatherDataProvider;
import fr.cvlaminck.hwweather.data.model.WeatherDataType;
import fr.cvlaminck.hwweather.data.repositories.FreeCallCounterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WeatherDataProviderSelectionManager {

    @Autowired
    private Collection<WeatherDataProvider> weatherDataProviders;

    @Autowired
    private FreeCallCounterRepository freeCallCounterRepository;

    public List<WeatherDataProvider> selectDataProvidersToUseForRefreshOperation(Collection<WeatherDataType> typesToRefresh) {
        freeCallCounterRepository.getFreeCallsLeftForProviders();
        //Score operation by cost and number of API to call. Also by number of types refreshed even if they are not required.
        //Take the best options
        //Try to decrement all counters for all free data provider that will be called
        //If free calls for an API are exhausted, update the cost of the operation and back to the selection of the best.
        //If the exhausted free API does not have paid option, then remove this solution from the list.
        return weatherDataProviders.stream()
                .collect(Collectors.toList()); //FIXME
    }

}
