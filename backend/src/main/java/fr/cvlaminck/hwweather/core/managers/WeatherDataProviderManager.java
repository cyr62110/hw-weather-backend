package fr.cvlaminck.hwweather.core.managers;

import fr.cvlaminck.hwweather.core.exceptions.NoProviderAvailableForRefreshOperationException;
import fr.cvlaminck.hwweather.core.external.model.weather.ExternalWeatherData;
import fr.cvlaminck.hwweather.core.external.model.weather.ExternalWeatherDataType;
import fr.cvlaminck.hwweather.core.external.providers.weather.WeatherDataProvider;
import fr.cvlaminck.hwweather.core.model.WeatherProvidersSelectionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class WeatherDataProviderManager {

    @Autowired
    private List<WeatherDataProvider> dataProviders;

    @Autowired
    private WeatherDataProviderSelectionManager weatherDataProviderSelectionManager;

    public ExternalWeatherData refresh(double latitude, double longitude, Set<ExternalWeatherDataType> typesToRefresh) throws NoProviderAvailableForRefreshOperationException {
        ExternalWeatherData data = new ExternalWeatherData();

        //Set of types of weather data that has not been refreshed by another provider in the list.
        Set<ExternalWeatherDataType> typesToRefreshLeft = new HashSet<>();
        typesToRefreshLeft.addAll(typesToRefresh);

        WeatherProvidersSelectionResult result = weatherDataProviderSelectionManager.selectDataProvidersToUseForRefreshOperation(typesToRefresh);
        data.getMetadata().setNumberOfProviderCalled(result.getProvidersToUse().size());
        data.getMetadata().setNumberOfFreeCallUsed(result.getNumberOfFreeCallUsed());
        data.getMetadata().setOperationCost(result.getOperationCost());

        for (WeatherDataProvider provider: result.getProvidersToUse()) {
            //We use the next provider in the list to get data.
            ExternalWeatherData response = provider.refresh(latitude, longitude, typesToRefreshLeft);
            //We merge the data with the one provided by the other providers preceding this one
            data = mergeExternalWeatherData(data, response);
            //Then we update the list of missing data
            typesToRefreshLeft = removeResolvedTypeFromTypesToRefresh(typesToRefreshLeft, data);
        }
        return data;
    }

    /**
     * Merge information returned by external weather API.
     * As a simple first implementation, once an API has answered an information(current, ...), we keep it
     * and never replace it even if another API has also provided the same information.
     */
    private ExternalWeatherData mergeExternalWeatherData(ExternalWeatherData d1, ExternalWeatherData d2) {
        if (d1.getCurrent() == null) {
            d1.setCurrent(d2.getCurrent());
        }
        if (d1.getHourly() == null || d1.getHourly().isEmpty()) {
            d1.setHourly(d2.getHourly());
        }
        if (d1.getDaily() == null || d1.getDaily().isEmpty()) {
            d1.setDaily(d2.getDaily());
        }
        return d1;
    }

    private Set<ExternalWeatherDataType> removeResolvedTypeFromTypesToRefresh(Set<ExternalWeatherDataType> typesToRefreshLeft, ExternalWeatherData data) {
        if (data.getCurrent() != null) {
            typesToRefreshLeft.remove(ExternalWeatherDataType.CURRENT);
        }
        if (!data.getHourly().isEmpty()) {
            typesToRefreshLeft.remove(ExternalWeatherDataType.HOURLY);
        }
        if (!data.getDaily().isEmpty()) {
            typesToRefreshLeft.remove(ExternalWeatherDataType.DAILY);
        }
        return typesToRefreshLeft;
    }

}
