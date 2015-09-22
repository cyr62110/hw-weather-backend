package fr.cvlaminck.hwweather.core.exceptions.clients;

import fr.cvlaminck.hwweather.core.exceptions.HwWeatherCoreException;

public class HwWeatherCoreClientException
    extends HwWeatherCoreException {

    private int responseCode;

    public HwWeatherCoreClientException(int responseCode, String message) {
        super(message);
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return responseCode;
    }
}
