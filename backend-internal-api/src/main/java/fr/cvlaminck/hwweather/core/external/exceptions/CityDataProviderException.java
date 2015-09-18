package fr.cvlaminck.hwweather.core.external.exceptions;

import fr.cvlaminck.hwweather.core.external.providers.city.CityDataProvider;

public class CityDataProviderException
    extends Exception {
    private final static String MESSAGE = "Exception occurred in provider '%s'";

    public CityDataProviderException(Class<? extends CityDataProvider> providerClass, String additionalMessage, Throwable cause) {
        super(formatMessage(providerClass, additionalMessage), cause);
    }

    private static String formatMessage(Class<? extends CityDataProvider> providerClass, String additionalMessage) {
        String msg = String.format(MESSAGE, providerClass.getSimpleName());
        if (additionalMessage != null && !additionalMessage.isEmpty()) {
            msg += ":" + additionalMessage;
        }
        return msg;
    }
}
