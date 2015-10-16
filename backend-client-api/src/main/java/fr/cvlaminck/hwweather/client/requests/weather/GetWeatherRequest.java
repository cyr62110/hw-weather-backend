package fr.cvlaminck.hwweather.client.requests.weather;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.cvlaminck.builders.uri.Uri;
import fr.cvlaminck.hwweather.client.exceptions.HwWeatherIllegalProtocolException;
import fr.cvlaminck.hwweather.client.reponses.weather.WeatherResponse;
import fr.cvlaminck.hwweather.client.requests.HwWeatherRequest;
import fr.cvlaminck.hwweather.client.resources.ExternalCityIdResource;
import fr.cvlaminck.hwweather.client.resources.weather.enums.WeatherDataType;

import java.util.Arrays;
import java.util.Collection;

public class GetWeatherRequest
        extends HwWeatherRequest<WeatherResponse> {

    private ExternalCityIdResource externalCityId;
    private String cityId;

    private Collection<WeatherDataType> types;

    public GetWeatherRequest(Uri baseUri, ObjectMapper objectMapper) {
        super(baseUri, objectMapper, WeatherResponse.class);
    }

    public void setCity(String cityId) {
        this.cityId = cityId;
        this.externalCityId = null;
    }

    public void setCity(ExternalCityIdResource externalCityId) {
        this.cityId = null;
        this.externalCityId = externalCityId;
    }

    public void setTypes(Collection<WeatherDataType> types) {
        this.types = types;
    }

    @Override
    public Uri build() {
        if (cityId == null && externalCityId == null) {
            throw new HwWeatherIllegalProtocolException("You must provide a city id or an external city id.");
        }
        if (cityId != null && cityId.isEmpty()) {
            throw new HwWeatherIllegalProtocolException("You must provide a non empty city id.");
        }
        if (externalCityId != null && !externalCityId.isValid()) {
            throw new HwWeatherIllegalProtocolException("You must provide a valid external id: Both provider and id filled.");
        }

        if (types == null || types.isEmpty()) {
            types = Arrays.asList(WeatherDataType.values());
        }

        String cityId = null;
        if (this.cityId != null) {
            cityId = this.cityId;
        } else {
            cityId = this.externalCityId.toString();
        }

        return getBaseUriBuilder()
                .appendPathSegment("weather")
                .appendPathSegment(cityId)
                .appendPathSegment(typesToPathSegment(types))
                .build();
    }

    private String typesToPathSegment(Collection<WeatherDataType> types) {
        StringBuilder sb = new StringBuilder();
        for (WeatherDataType type : types) {
            if (sb.length() > 0) {
                sb.append('+');
            }
            sb.append(type.getName());
        }
        return sb.toString();
    }

}
