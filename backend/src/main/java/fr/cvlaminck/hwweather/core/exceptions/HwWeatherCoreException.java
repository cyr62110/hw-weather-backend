package fr.cvlaminck.hwweather.core.exceptions;

public abstract class HwWeatherCoreException extends Exception {

    protected HwWeatherCoreException() {
    }

    protected HwWeatherCoreException(String message) {
        super(message);
    }

    protected HwWeatherCoreException(String message, Throwable cause) {
        super(message, cause);
    }

    protected HwWeatherCoreException(Throwable cause) {
        super(cause);
    }

    protected HwWeatherCoreException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
