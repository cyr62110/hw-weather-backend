package fr.cvlaminck.hwweather.core.exceptions;

public class NoProviderWithNameException
        extends HwWeatherCoreException {

    private final static String MESSAGE = "No provider with name '%s' is registered.";

    public NoProviderWithNameException(String providerName) {
        super(String.format(MESSAGE, providerName));
    }
}
