package fr.cvlaminck.hwweather.front.controllers.admin;

import fr.cvlaminck.hwweather.core.managers.CityManager;
import fr.cvlaminck.hwweather.core.managers.WeatherManager;
import fr.cvlaminck.hwweather.core.managers.WeatherRefreshManager;
import fr.cvlaminck.hwweather.data.model.WeatherDataType;
import fr.cvlaminck.hwweather.data.model.city.CityEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/admin/weather")
public class AdminWeatherController {

    @Autowired
    private CityManager cityManager;

    @Autowired
    private WeatherRefreshManager weatherRefreshManager;

    @RequestMapping("/{id}/update")
    public String forceUpdate(@PathVariable String id) throws Exception{
        CityEntity city = cityManager.getCity(id, "en-US");
        weatherRefreshManager.refresh(city, Collections.singleton(WeatherDataType.WEATHER));
        return "OK";
    }

}
