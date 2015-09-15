package fr.cvlaminck.hwweather.front.controllers.user;

import fr.cvlaminck.hwweather.core.exceptions.NoProviderWithNameException;
import fr.cvlaminck.hwweather.core.exceptions.NoResultForWeatherRefreshOperationException;
import fr.cvlaminck.hwweather.core.external.exceptions.CityDataProviderException;
import fr.cvlaminck.hwweather.core.managers.CityManager;
import fr.cvlaminck.hwweather.core.managers.WeatherManager;
import fr.cvlaminck.hwweather.core.managers.WeatherRefreshQueuesManager;
import fr.cvlaminck.hwweather.data.model.WeatherDataType;
import fr.cvlaminck.hwweather.data.model.city.CityEntity;
import fr.cvlaminck.hwweather.data.model.weather.CurrentWeatherEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/weather/{cityId}")
public class WeatherController {

    @Autowired
    private CityManager cityManager;

    @Autowired
    private WeatherManager weatherManager;

    @Autowired
    private WeatherRefreshQueuesManager weatherRefreshQueuesManager;

    @RequestMapping(method = RequestMethod.GET, value = "/current")
    public CurrentWeatherEntity getCurrent(@PathVariable String cityId) throws CityDataProviderException, NoProviderWithNameException, NoResultForWeatherRefreshOperationException {
        CityEntity city = cityManager.getCity(cityId, "en-US"); //TODO handle language
        CurrentWeatherEntity current = weatherManager.getCurrentWeather(city);
        weatherRefreshQueuesManager.postRefreshOperationForCityAndWaitIfNecessary(city, Arrays.asList(current), Arrays.asList(WeatherDataType.WEATHER));
        return current;
    }

}
