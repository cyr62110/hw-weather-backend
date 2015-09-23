package fr.cvlaminck.hwweather.front.exceptions;

import fr.cvlaminck.hwweather.client.resources.weather.enums.WeatherDataType;
import fr.cvlaminck.hwweather.core.exceptions.clients.HwWeatherCoreClientException;

import java.util.Arrays;

/**
 * Throw if the client has not specified any type to refresh for a GetWeather operation.
 */
public class MissingTypeException
        extends HwWeatherCoreClientException {
    private static final String MESSAGE = "Missing types to refresh. Available types: %s";

    public MissingTypeException() {
        super(400, String.format(MESSAGE, Arrays.asList(WeatherDataType.values()).toString()));
    }
}
