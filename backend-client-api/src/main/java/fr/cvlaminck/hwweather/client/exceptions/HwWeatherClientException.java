package fr.cvlaminck.hwweather.client.exceptions;

/**
 * Thrown if the query causes the server to returns a 400 error.
 */
public class HwWeatherClientException
        extends Exception {
    private int responseCode;



}
