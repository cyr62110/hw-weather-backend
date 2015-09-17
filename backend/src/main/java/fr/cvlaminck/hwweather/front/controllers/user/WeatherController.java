package fr.cvlaminck.hwweather.front.controllers.user;

import fr.cvlaminck.hwweather.client.resources.weather.enums.WeatherDataType;
import fr.cvlaminck.hwweather.core.exceptions.NoProviderWithNameException;
import fr.cvlaminck.hwweather.core.exceptions.NoResultForWeatherRefreshOperationException;
import fr.cvlaminck.hwweather.core.external.exceptions.CityDataProviderException;
import fr.cvlaminck.hwweather.core.managers.CityManager;
import fr.cvlaminck.hwweather.core.managers.WeatherManager;
import fr.cvlaminck.hwweather.core.managers.WeatherRefreshQueuesManager;
import fr.cvlaminck.hwweather.core.model.WeatherData;
import fr.cvlaminck.hwweather.data.model.city.CityEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(method = RequestMethod.GET)
    public WeatherData get(@PathVariable String cityId, @RequestParam(required = false) Collection<WeatherDataType> type) throws NoResultForWeatherRefreshOperationException, CityDataProviderException, NoProviderWithNameException {
        //TODO if type is null or empty
        Collection<fr.cvlaminck.hwweather.data.model.WeatherDataType> types = convertToCoreTypes(type);
        return getWeather(cityId, types);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/current")
    public WeatherData getCurrent(@PathVariable String cityId) throws CityDataProviderException, NoProviderWithNameException, NoResultForWeatherRefreshOperationException {
        return getWeather(cityId, Arrays.asList(fr.cvlaminck.hwweather.data.model.WeatherDataType.WEATHER));
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

    private WeatherData getWeather(String cityId, Collection<fr.cvlaminck.hwweather.data.model.WeatherDataType> types) throws CityDataProviderException, NoProviderWithNameException, NoResultForWeatherRefreshOperationException {
        CityEntity city = cityManager.getCity(cityId, "en-US"); //TODO handle language
        WeatherData data = weatherManager.getWeather(city, types);
        //TODO transform into response
        return data;
    }

}
