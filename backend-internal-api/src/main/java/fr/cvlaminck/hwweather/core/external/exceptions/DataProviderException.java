package fr.cvlaminck.hwweather.core.external.exceptions;

import fr.cvlaminck.hwweather.core.external.providers.DataProvider;

public class DataProviderException
        extends Exception {
    private final static String MESSAGE = "Exception occurred in provider '%s'";

    private DataProvider dataProvider;

    public DataProviderException(DataProvider provider, String additionalMessage, Throwable cause) {
        super(formatMessage(provider, additionalMessage), cause);
        this.dataProvider = provider;
    }

    public DataProviderException(DataProvider provider, String additionalMessage) {
        this(provider, additionalMessage, null);
    }

    public DataProviderException(DataProvider provider, Throwable cause) {
        this(provider, null, cause);
    }

    public DataProviderException(DataProvider provider) {
        this(provider, null, null);
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
