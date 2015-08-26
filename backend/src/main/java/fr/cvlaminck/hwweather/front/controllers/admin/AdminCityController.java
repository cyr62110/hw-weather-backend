package fr.cvlaminck.hwweather.front.controllers.admin;

import fr.cvlaminck.hwweather.core.managers.CityManager;
import fr.cvlaminck.hwweather.data.model.CityEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/cities")
public class AdminCityController {

    @Autowired
    private CityManager cityManager;

    @RequestMapping("/{id}/update")
    public CityEntity update(@PathVariable String id) throws Exception {
        return cityManager.updateOrAddCityWithExternalId(id, "en-US");
    }

}
