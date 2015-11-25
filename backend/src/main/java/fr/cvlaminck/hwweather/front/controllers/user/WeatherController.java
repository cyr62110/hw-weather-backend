package fr.cvlaminck.hwweather.front.controllers.user;

import fr.cvlaminck.hwweather.client.protocol.WeatherResponse;
import fr.cvlaminck.hwweather.client.resources.weather.enums.WeatherDataType;
import fr.cvlaminck.hwweather.core.exceptions.HwWeatherCoreException;
import fr.cvlaminck.hwweather.core.managers.CityManager;
import fr.cvlaminck.hwweather.core.managers.WeatherManager;
import fr.cvlaminck.hwweather.core.managers.WeatherRefreshQueuesManager;
import fr.cvlaminck.hwweather.core.model.WeatherData;
import fr.cvlaminck.hwweather.data.model.city.CityEntity;
import fr.cvlaminck.hwweather.front.converters.CityConverter;
import fr.cvlaminck.hwweather.front.converters.WeatherDataConverter;
import fr.cvlaminck.hwweather.front.exceptions.MissingTypeException;
import fr.cvlaminck.hwweather.front.exceptions.UnknownTypeException;
import fr.cvlaminck.hwweather.front.utils.AvroMimeTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/weather/{cityId}")
public class WeatherController {

    @Autowired
    private CityManager cityManager;

    @Autowired
    private WeatherManager weatherManager;

    @Autowired
    private WeatherRefreshQueuesManager weatherRefreshQueuesManager;

    @Autowired
    private CityConverter cityConverter;

    @Autowired
    private WeatherDataConverter weatherDataConverter;

    //TODO Respond Forbidden on /weather, force clients to give types they want to refresh

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/{sTypes}",
            produces = {
                    AvroMimeTypes.BINARY_AVRO_MIME,
                    AvroMimeTypes.JSON_AVRO_MIME
            }
    )
    public WeatherResponse getTypes(@PathVariable String cityId,
                                    @PathVariable String sTypes,
                                    HttpServletResponse response)
            throws HwWeatherCoreException {
        if (sTypes == null || sTypes.isEmpty()) {
            throw new MissingTypeException();
        }
        Collection<WeatherDataType> types = getTypes(sTypes);
        return getWeather(cityId, convertToCoreTypes(types), response);
    }

    private Collection<WeatherDataType> getTypes(String sTypes) throws UnknownTypeException {
        Collection<WeatherDataType> types = new ArrayList<>();
        for (String sType : sTypes.split("\\+")) {
            try {
                types.add(WeatherDataType.valueOf(sType.toUpperCase()));
            } catch (IllegalArgumentException ex) {
                throw new UnknownTypeException(sType);
            }
        }
        return types;
    }

    private Collection<fr.cvlaminck.hwweather.data.model.WeatherDataType> convertToCoreTypes(Collection<WeatherDataType> types) {
        return types.stream()
                .map(t -> {
                    switch (t) {
                        case CURRENT:
                            return fr.cvlaminck.hwweather.data.model.WeatherDataType.WEATHER;
                        case HOURLY:
                            return fr.cvlaminck.hwweather.data.model.WeatherDataType.HOURLY_FORECAST;
                        case DAILY:
                            return fr.cvlaminck.hwweather.data.model.WeatherDataType.DAILY_FORECAST;
                    }
                    return null;
                })
                .filter(t -> t != null)
                .collect(Collectors.toList());
    }

    private WeatherResponse getWeather(String cityId,
                                       Collection<fr.cvlaminck.hwweather.data.model.WeatherDataType> types,
                                       HttpServletResponse response)
            throws HwWeatherCoreException {
        CityEntity city = cityManager.getCity(cityId, "en-US"); //TODO handle language
        WeatherData data = weatherManager.getWeather(city, types);

        WeatherResponse weatherResponse = new WeatherResponse();
        weatherResponse.setCity(cityConverter.getResourceFrom(data.getCity(), "en-US"));
        if (data.getCurrent() != null) {
            weatherResponse.setCurrent(weatherDataConverter.getResourceFrom(data.getCurrent()));
        }
        if (!data.getHourlyList().isEmpty()) {
            weatherResponse.setHourly(data.getHourlyList().stream()
                            .flatMap(h -> h.getHourByHourForecasts().stream())
                            .map(weatherDataConverter::getResourceFrom)
                            .collect(Collectors.toList())
            );
        }
        if (!data.getDailyList().isEmpty()) {
            weatherResponse.setDaily(data.getDailyList().stream()
                            .flatMap(d -> d.getDayByDayForecasts().stream())
                            .map(weatherDataConverter::getResourceFrom)
                            .collect(Collectors.toList())
            );
        }

        //TODO: If should include debug headers.
        response.addHeader("X-Refresh", Boolean.toString(data.getMetadata().hasCausedRefreshOperation()));
        response.addHeader("X-Wait-Refresh", Boolean.toString(data.getMetadata().hasWaitedForRefreshOperationToFinish()));
        if (data.getMetadata().hasCausedRefreshOperation()) {
            response.addHeader("X-Refresh-Types", data.getMetadata().getTypesToRefresh().toString());
        }
        if (data.getMetadata().hasWaitedForRefreshOperationToFinish()) {
            response.addHeader("X-Refreshed-Types", data.getMetadata().getRefreshedTypes().toString());
            response.addHeader("X-Call", Integer.toString(data.getMetadata().getNumberOfProviderCalled()));
            response.addHeader("X-Free-Call", Integer.toString(data.getMetadata().getNumberOfFreeCallUsed()));
            response.addHeader("X-Cost", Double.toString(data.getMetadata().getOperationCost()));
        }

        return weatherResponse;
    }

}
