package fr.cvlaminck.hwweather.client.exceptions;

import java.net.URL;

public abstract class HwWeatherRequestException
        extends Exception {

    private URL requestUrl;
    private byte[] requestContent;
    private int statusCode;

    protected HwWeatherRequestException(String message, URL requestUrl, byte[] requestContent, int statusCode) {
        super(message);

        this.requestUrl = requestUrl;
        this.requestContent = requestContent;
        this.statusCode = statusCode;
    }

    public URL getRequestUrl() {
        return requestUrl;
    }

    public byte[] getRequestContent() {
        return requestContent;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
