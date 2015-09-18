package fr.cvlaminck.hwweather.front.controllers.user;

import fr.cvlaminck.hwweather.client.reponses.city.SearchCityResponse;
import fr.cvlaminck.hwweather.core.managers.CityManager;
import fr.cvlaminck.hwweather.data.model.city.CityEntity;
import fr.cvlaminck.hwweather.front.converters.CityConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.stream.Collectors;

import fr.cvlaminck.hwweather.core.external.model.city.ExternalCityResource;
import fr.cvlaminck.hwweather.core.managers.CityDataProviderManager;

@RestController
@RequestMapping("/cities")
public class CityController {

    @Autowired
    private CityManager cityManager;

    @Autowired
    private CityConverter cityConverter;

    @Autowired
    private CityDataProviderManager cityDataProviderManager;

    @RequestMapping("/search/{name}")
    public SearchCityResponse search(@PathVariable String name,
                                                   @RequestParam(required = false) Integer page) throws Exception {
        if (page == null || page < 1) {
            page = 1;
        }
        int numberOfResultsPerPage = 20; //TODO load it from configuration

        Collection<ExternalCityResource> cities = cityDataProviderManager.searchByName(name);

        SearchCityResponse response = new SearchCityResponse();
        response.setQuery(name);
        response.setPage(page);
        response.setNumberOfResultPerPage(numberOfResultsPerPage);
        response.setTotalNumberOfResult(cities.size());
        response.setResults(cities.stream()
                .skip((page - 1) * numberOfResultsPerPage)
                .limit(numberOfResultsPerPage)
                .map(c -> cityConverter.getResourceFrom(c))
                .collect(Collectors.toList())
        );
        return response;
    }

    @RequestMapping("/{id}")
    public CityEntity get(@PathVariable String id) throws Exception { //FIXME
        return cityManager.getCity(id, "en-US");
    }

}
