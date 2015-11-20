package fr.cvlaminck.hwweather.client.exceptions;

import java.net.URL;

/**
 * Thrown if the query causes the server to returns a 4xx error.
 */
public class HwWeatherClientException
        extends HwWeatherRequestException {
    public static final String MESSAGE = "Server responded with code '%d' to the request. ";

    public HwWeatherClientException(URL requestUrl, byte[] requestContent, int statusCode) {
        super(String.format(MESSAGE, statusCode), requestUrl, requestContent, statusCode);
    }

    public HwWeatherClientException(URL requestUrl, byte[] requestContent, int statusCode, String details) {
        super(String.format(MESSAGE, statusCode) + details, requestUrl, requestContent, statusCode);
    }
}
