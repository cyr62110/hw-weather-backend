package fr.cvlaminck.hwweather.core.external.providers.weather;

import fr.cvlaminck.hwweather.core.external.model.weather.ExternalWeatherData;
import fr.cvlaminck.hwweather.core.external.model.weather.ExternalWeatherDataType;
import fr.cvlaminck.hwweather.core.external.providers.DataProvider;

import java.util.Collection;

public interface WeatherDataProvider
        extends DataProvider {

    /**
     * Returns true if the provider can still be called once the number of free operations
     * per day is exhausted.
     */
    boolean supportsPaidCall();

    /**
     * Returns the cost in dollars when we are doing a request on this provider
     */
    Double getCostPerOperation();

    /**
     * Returns the number of request that can be done per day wihtout paying anything.
     */
    Long getNumberOfFreeOperationPerDay();

    /**
     * Returns the types of weather data that will be retrieved when we
     * call refresh on this provider.
     */
    Collection<ExternalWeatherDataType> getTypes();

    /**
     * Query the data provider for this geo location.
     * The weather data should contains all types described in typesToRefresh.
     * It may also contain more type
     */
    ExternalWeatherData refresh(double latitude, double longitude, Collection<ExternalWeatherDataType> typesToRefresh);

}
