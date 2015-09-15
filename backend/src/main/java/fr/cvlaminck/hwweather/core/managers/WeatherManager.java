package fr.cvlaminck.hwweather.core.managers;

import fr.cvlaminck.hwweather.core.utils.DateUtils;
import fr.cvlaminck.hwweather.data.model.city.CityEntity;
import fr.cvlaminck.hwweather.data.model.weather.CurrentWeatherEntity;
import fr.cvlaminck.hwweather.data.repositories.CurrentWeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WeatherManager {

    @Autowired
    private CurrentWeatherRepository currentWeatherRepository;

    @Autowired
    private WeatherRefreshManager refreshManager;

    public CurrentWeatherEntity getCurrentWeather(CityEntity city) {
        return currentWeatherRepository.findByCityIdAndDay(city.getId(), DateUtils.today());
    }

}
