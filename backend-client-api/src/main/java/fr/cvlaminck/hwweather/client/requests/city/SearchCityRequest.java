package fr.cvlaminck.hwweather.client.requests.city;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.cvlaminck.builders.uri.Uri;
import fr.cvlaminck.hwweather.client.exceptions.HwWeatherIllegalProtocolException;
import fr.cvlaminck.hwweather.client.reponses.city.SearchCityResponse;
import fr.cvlaminck.hwweather.client.requests.HwWeatherRequest;

public class SearchCityRequest
        extends HwWeatherRequest<SearchCityResponse> {

    private String city;

    public SearchCityRequest(Uri baseUri, ObjectMapper mapper) {
        super(baseUri, mapper, SearchCityResponse.class);
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public Uri build() {
        if (city == null || city.isEmpty()) {
            throw new HwWeatherIllegalProtocolException("city must not be null nor empty.");
        }

        return getBaseUriBuilder()
                .appendPathSegment("cities")
                .appendPathSegment("search")
                .appendPathSegment(city)
                .build();
    }
}
