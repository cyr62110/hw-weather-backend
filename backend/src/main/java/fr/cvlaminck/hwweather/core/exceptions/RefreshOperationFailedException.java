package fr.cvlaminck.hwweather.core.exceptions;

import fr.cvlaminck.hwweather.data.model.WeatherDataType;
import fr.cvlaminck.hwweather.data.model.city.CityEntity;

import java.util.Collection;

/**
 * Throws if the refresh operation ends in an error.
 */
public class RefreshOperationFailedException
        extends HwWeatherCoreException {
    private static final String MESSAGE = "Cannot refresh %s for city '%s'. Refresh operation ended up in an error. Check your log message.";

    public RefreshOperationFailedException(CityEntity city, Collection<WeatherDataType> typesToRefresh) {
        super(String.format(MESSAGE, typesToRefresh.toString(), city.getId()));
    }
}
