package fr.cvlaminck.hwweather.core.exceptions.clients;

/**
 * Thrown if the city designed with the provided id does not exists.
 */
public class CityNotFoundException
        extends HwWeatherCoreClientException {

    private static final String MESSAGE = "City with id '%s' does not exists.";

    public CityNotFoundException(String cityId) {
        super(404, String.format(MESSAGE, cityId));
    }
}
