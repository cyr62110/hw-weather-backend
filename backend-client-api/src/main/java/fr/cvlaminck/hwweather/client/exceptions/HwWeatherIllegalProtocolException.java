package fr.cvlaminck.hwweather.client.exceptions;

public class HwWeatherIllegalProtocolException
    extends RuntimeException {

    public HwWeatherIllegalProtocolException() {
    }

    public HwWeatherIllegalProtocolException(String message) {
        super(message);
    }

    public HwWeatherIllegalProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public HwWeatherIllegalProtocolException(Throwable cause) {
        super(cause);
    }
}
