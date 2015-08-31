package fr.cvlaminck.hwweather.front.controllers.user;

import fr.cvlaminck.hwweather.core.managers.CityManager;
import fr.cvlaminck.hwweather.data.model.city.CityEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

import fr.cvlaminck.hwweather.core.external.model.city.ExternalCityResource;
import fr.cvlaminck.hwweather.core.managers.CityDataProviderManager;

@RestController
@RequestMapping("/cities")
public class CityController {

    @Autowired
    private CityManager cityManager;

    @Autowired
    private CityDataProviderManager cityDataProviderManager;

    @RequestMapping("/search/{name}")
    public Collection<ExternalCityResource> search(@PathVariable String name) throws Exception {
        return cityDataProviderManager.searchByName(name, null);
    }

    @RequestMapping("/{id}")
    public CityEntity get(@PathVariable String id) throws Exception { //FIXME
        return cityManager.getCity(id, "en-US");
    }

}
