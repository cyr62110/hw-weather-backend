package fr.cvlaminck.hwweather.client;

import fr.cvlaminck.hwweather.client.exceptions.HwWeatherRequestException;
import fr.cvlaminck.hwweather.client.protocol.ExternalCityIdResource;
import fr.cvlaminck.hwweather.client.protocol.WeatherResponse;
import fr.cvlaminck.hwweather.client.requests.weather.GetWeatherRequest;
import fr.cvlaminck.hwweather.client.resources.weather.enums.WeatherDataType;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

public class HwWeatherWeatherRequests {
    private HwWeatherClient client;

    HwWeatherWeatherRequests(HwWeatherClient client) {
        this.client = client;
    }

    public WeatherResponse get(ExternalCityIdResource externalCityId, Collection<WeatherDataType> types) throws IOException, HwWeatherRequestException {
        GetWeatherRequest request = new GetWeatherRequest(client.getBaseUri(), client.getObjectMapper());
        request.setCity(externalCityId);
        request.setTypes(types);
        return request.call();
    }

    public WeatherResponse get(String cityId, Collection<WeatherDataType> types) throws IOException, HwWeatherRequestException {
        GetWeatherRequest request = new GetWeatherRequest(client.getBaseUri(), client.getObjectMapper());
        request.setCity(cityId);
        request.setTypes(types);
        return request.call();
    }

    public WeatherResponse get(ExternalCityIdResource externalCityId, WeatherDataType... types) throws IOException, HwWeatherRequestException {
        return get(externalCityId, Arrays.asList(types));
    }

    public WeatherResponse get(String cityId, WeatherDataType... types) throws IOException, HwWeatherRequestException {
        return get(cityId, Arrays.asList(types));
    }
}
