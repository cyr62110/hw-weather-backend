package fr.cvlaminck.hwweather.client;

import fr.cvlaminck.hwweather.client.reponses.city.SearchCityResponse;
import fr.cvlaminck.hwweather.client.requests.city.SearchCityRequest;

import java.io.IOException;
import java.util.concurrent.Future;

/* package */ class HwWeatherCityRequests {

    private HwWeatherClient client;

    HwWeatherCityRequests(HwWeatherClient client) {
        this.client = client;
    }

    public SearchCityResponse search(String city) throws IOException {
        SearchCityRequest request = new SearchCityRequest(client.getBaseUri(), client.getObjectMapper());
        return request.execute();
    }

}
