package fr.cvlaminck.hwweather.core.external.exceptions;

import fr.cvlaminck.hwweather.core.external.providers.DataProvider;
import fr.cvlaminck.hwweather.core.external.providers.city.CityDataProvider;

public class CityDataProviderException
    extends Exception {
    private final static String MESSAGE = "Exception occurred in provider '%s'";

    private DataProvider dataProvider;

    public CityDataProviderException(DataProvider provider, String additionalMessage, Throwable cause) {
        super(formatMessage(provider, additionalMessage), cause);
        this.dataProvider = provider;
    }

    private static String formatMessage(DataProvider provider, String additionalMessage) {
        String msg = String.format(MESSAGE, provider.getProviderName());
        if (additionalMessage != null && !additionalMessage.isEmpty()) {
            msg += ":" + additionalMessage;
        }
        return msg;
    }

    public DataProvider getDataProvider() {
        return dataProvider;
    }
}
