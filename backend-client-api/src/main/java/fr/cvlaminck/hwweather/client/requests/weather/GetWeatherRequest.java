package fr.cvlaminck.hwweather.client.requests.weather;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.cvlaminck.builders.uri.Uri;
import fr.cvlaminck.hwweather.client.reponses.weather.WeatherResponse;
import fr.cvlaminck.hwweather.client.requests.HwWeatherRequest;

public class GetWeatherRequest
    extends HwWeatherRequest<WeatherResponse> {

    public GetWeatherRequest(Uri baseUri, ObjectMapper objectMapper) {
        super(baseUri, objectMapper, WeatherResponse.class);
    }

    @Override
    public Uri build() {
        return null; //FIXME
    }

}
