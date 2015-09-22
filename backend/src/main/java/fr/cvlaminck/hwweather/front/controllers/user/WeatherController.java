package fr.cvlaminck.hwweather.front.controllers.user;

import fr.cvlaminck.hwweather.client.reponses.weather.WeatherResponse;
import fr.cvlaminck.hwweather.client.resources.weather.enums.WeatherDataType;
import fr.cvlaminck.hwweather.core.exceptions.DataProviderException;
import fr.cvlaminck.hwweather.core.exceptions.NoProviderWithNameException;
import fr.cvlaminck.hwweather.core.exceptions.NoResultForWeatherRefreshOperationException;
import fr.cvlaminck.hwweather.core.exceptions.clients.CityNotFoundException;
import fr.cvlaminck.hwweather.core.external.exceptions.CityDataProviderException;
import fr.cvlaminck.hwweather.core.managers.CityManager;
import fr.cvlaminck.hwweather.core.managers.WeatherManager;
import fr.cvlaminck.hwweather.core.managers.WeatherRefreshQueuesManager;
import fr.cvlaminck.hwweather.core.model.WeatherData;
import fr.cvlaminck.hwweather.data.model.city.CityEntity;
import fr.cvlaminck.hwweather.front.converters.CityConverter;
import fr.cvlaminck.hwweather.front.converters.WeatherDataConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
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

    @RequestMapping(method = RequestMethod.GET)
    public WeatherResponse get(@PathVariable String cityId,
                               @RequestParam(required = false) Collection<WeatherDataType> type,
                               HttpServletResponse response)
            throws CityNotFoundException, DataProviderException, NoResultForWeatherRefreshOperationException, NoProviderWithNameException {
        //TODO if type is null or empty
        Collection<fr.cvlaminck.hwweather.data.model.WeatherDataType> types = convertToCoreTypes(type);
        return getWeather(cityId, types, response);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/current")
    public WeatherResponse getCurrent(@PathVariable String cityId,
                                      HttpServletResponse response)
            throws CityNotFoundException, DataProviderException, NoResultForWeatherRefreshOperationException, NoProviderWithNameException {
        Collection<fr.cvlaminck.hwweather.data.model.WeatherDataType> types = Arrays.asList(fr.cvlaminck.hwweather.data.model.WeatherDataType.WEATHER);
        return getWeather(cityId, types, response);
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
            throws CityNotFoundException, NoResultForWeatherRefreshOperationException, DataProviderException, NoProviderWithNameException {
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
