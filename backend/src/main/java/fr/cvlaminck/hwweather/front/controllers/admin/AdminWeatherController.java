package fr.cvlaminck.hwweather.front.controllers.admin;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/weather")
public class AdminWeatherController {

    @RequestMapping("/{id}/update")
    public String forceUpdate(@PathVariable String id) {
        return "OK";
    }

}
