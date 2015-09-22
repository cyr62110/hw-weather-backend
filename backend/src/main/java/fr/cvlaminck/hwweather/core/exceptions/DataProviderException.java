package fr.cvlaminck.hwweather.core.exceptions;

import fr.cvlaminck.hwweather.core.external.providers.DataProvider;

public class DataProviderException extends HwWeatherCoreException {

    private static final String MESSAGE = "Exception occurred while calling data provider '%s'";

    public DataProviderException(DataProvider provider, Throwable cause) {
        super(String.format(MESSAGE, provider.getProviderName()), cause);
    }
}
