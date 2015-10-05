package fr.cvlaminck.hwweather.client;

import fr.cvlaminck.hwweather.client.exceptions.HwWeatherRequestException;
import fr.cvlaminck.hwweather.client.reponses.city.SearchCityResponse;
import fr.cvlaminck.hwweather.client.requests.city.SearchCityRequest;

import java.io.IOException;
import java.util.concurrent.Future;

public class HwWeatherCityRequests {

    private HwWeatherClient client;

    HwWeatherCityRequests(HwWeatherClient client) {
        this.client = client;
    }

    public SearchCityResponse search(String city) throws IOException, HwWeatherRequestException {
        SearchCityRequest request = new SearchCityRequest(client.getBaseUri(), client.getObjectMapper());
        request.setCity(city);
        return request.call();
    }

}
