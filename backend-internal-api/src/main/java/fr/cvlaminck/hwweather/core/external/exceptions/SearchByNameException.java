package fr.cvlaminck.hwweather.core.external.exceptions;

import fr.cvlaminck.hwweather.core.external.providers.city.CityDataProvider;

public class SearchByNameException extends Exception {

    private final static String MESSAGE = "An exception occured while trying to search city using '%s'";

    public SearchByNameException(Class<? extends CityDataProvider> providerClass, Throwable cause) {
        super(String.format(MESSAGE, providerClass.getSimpleName()), cause);
    }
}
