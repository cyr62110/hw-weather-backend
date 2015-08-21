package fr.cvlaminck.hwweather.front.controllers;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/weather/{city}")
public class WeatherController {

    @RequestMapping(method = RequestMethod.GET, value = "/current")
    public String getCurrent(@PathVariable String city) {
        return null;
    }

}
