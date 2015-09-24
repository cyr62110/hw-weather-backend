package fr.cvlaminck.hwweather.front.exceptions;

import fr.cvlaminck.hwweather.client.resources.weather.enums.WeatherDataType;
import fr.cvlaminck.hwweather.core.exceptions.clients.HwWeatherCoreClientException;

import java.util.Arrays;
import java.util.List;

/**
 * Thrown if a client ask to refresh an unknown type to the server.
 */
public class UnknownTypeException
    extends HwWeatherCoreClientException {
    private static final String MESSAGE = "Type '%s' is not recognized. Available types are %s";

    public UnknownTypeException(String type) {
        super(400, String.format(MESSAGE, type, Arrays.asList(WeatherDataType.values()).toString()));
    }
}
