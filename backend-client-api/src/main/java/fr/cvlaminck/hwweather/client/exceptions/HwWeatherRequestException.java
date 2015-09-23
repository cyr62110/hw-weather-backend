package fr.cvlaminck.hwweather.client.exceptions;

import java.net.URL;

public abstract class HwWeatherRequestException
    extends Exception {

    private URL requestUrl;
    private String requestContent;
    private int statusCode;

    protected HwWeatherRequestException(String message, URL requestUrl, String requestContent, int statusCode) {
        super(message);

        this.requestUrl = requestUrl;
        this.requestContent = requestContent;
        this.statusCode = statusCode;
    }

    public URL getRequestUrl() {
        return requestUrl;
    }

    public String getRequestContent() {
        return requestContent;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
