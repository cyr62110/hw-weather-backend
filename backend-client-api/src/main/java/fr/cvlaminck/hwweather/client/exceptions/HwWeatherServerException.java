package fr.cvlaminck.hwweather.client.exceptions;

import java.net.URL;

public class HwWeatherServerException
        extends HwWeatherRequestException {
    private static final String MESSAGE = "An internal server error occurred while processing the request. Retry the request later.";

    public HwWeatherServerException(URL requestUrl, int statusCode) {
        super(MESSAGE, requestUrl, null, statusCode);
    }

    public HwWeatherServerException(URL requestUrl, String requestContent, int statusCode) {
        super(MESSAGE, requestUrl, requestContent, statusCode);
    }
}
