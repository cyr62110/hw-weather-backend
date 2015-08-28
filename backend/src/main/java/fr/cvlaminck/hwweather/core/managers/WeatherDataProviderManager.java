package fr.cvlaminck.hwweather.core.managers;

import fr.cvlaminck.hwweather.core.external.model.weather.ExternalWeatherData;
import fr.cvlaminck.hwweather.core.external.model.weather.ExternalWeatherDataType;
import fr.cvlaminck.hwweather.core.external.providers.weather.WeatherDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class WeatherDataProviderManager {

    @Autowired
    private List<WeatherDataProvider> dataProviders;

    public ExternalWeatherData refresh(double latitude, double longitude) {
        return refresh(latitude, longitude, Arrays.asList(ExternalWeatherDataType.values()));
    }

    public ExternalWeatherData refresh(double latitude, double longitude, Collection<ExternalWeatherDataType> typesToRefresh) {
        ExternalWeatherData data = new ExternalWeatherData();

        //Set of types of weather data that has not been refreshed by another provider in the list.
        Collection<ExternalWeatherDataType> typesToRefreshLeft = new HashSet<>();
        typesToRefreshLeft.addAll(typesToRefresh);

        //FIXME
        for (WeatherDataProvider provider: getBestProvidersForRefreshOperation()) {
            //We use the next provider in the list to get data.
            ExternalWeatherData response = provider.refresh(latitude, longitude, typesToRefreshLeft);
            //We merge the data with the one provided by the other providers preceding this one
            //Then we update the list of missing data
        }
        return data;
    }

    private Collection<WeatherDataProvider> getBestProvidersForRefreshOperation() {
        return dataProviders;
    }

}
