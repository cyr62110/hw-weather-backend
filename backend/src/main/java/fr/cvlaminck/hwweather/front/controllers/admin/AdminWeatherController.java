package fr.cvlaminck.hwweather.front.controllers.admin;

import fr.cvlaminck.hwweather.core.managers.CityManager;
import fr.cvlaminck.hwweather.core.managers.WeatherManager;
import fr.cvlaminck.hwweather.data.model.CityEntity;
import fr.cvlaminck.hwweather.front.controllers.user.WeatherController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/weather")
public class AdminWeatherController {

    @Autowired
    private CityManager cityManager;

    @Autowired
    private WeatherManager weatherManager;

    @RequestMapping("/{id}/update")
    public String forceUpdate(@PathVariable String id) throws Exception{
        CityEntity city = cityManager.getCity(id, "en-US");
        weatherManager.forceUpdate(city);
        return "OK";
    }

}
