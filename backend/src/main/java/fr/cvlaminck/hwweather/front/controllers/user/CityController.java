package fr.cvlaminck.hwweather.front.controllers.user;

import fr.cvlaminck.hwweather.client.protocol.PageInformation;
import fr.cvlaminck.hwweather.client.protocol.SearchCityResponse;
import fr.cvlaminck.hwweather.core.external.model.city.ExternalCityResource;
import fr.cvlaminck.hwweather.core.managers.CityDataProviderManager;
import fr.cvlaminck.hwweather.core.managers.CityManager;
import fr.cvlaminck.hwweather.data.model.city.CityEntity;
import fr.cvlaminck.hwweather.front.converters.CityConverter;
import fr.cvlaminck.hwweather.front.utils.AvroMimeTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cities")
public class CityController {

    @Autowired
    private CityManager cityManager;

    @Autowired
    private CityConverter cityConverter;

    @Autowired
    private CityDataProviderManager cityDataProviderManager;

    @RequestMapping(
            value = "/search/{name}",
            produces = {
                    AvroMimeTypes.BINARY_AVRO_MIME,
                    AvroMimeTypes.JSON_AVRO_MIME
            }
    )
    public SearchCityResponse search(@PathVariable String name,
                                     @RequestParam(required = false) Integer page) throws Exception {
        if (page == null || page < 1) {
            page = 1;
        }
        int numberOfResultsPerPage = 20; //TODO load it from configuration

        Collection<ExternalCityResource> cities = cityDataProviderManager.searchByName(name);

        PageInformation.Builder pageInfoBuilder = PageInformation.newBuilder();
        pageInfoBuilder.setQuery(name);
        pageInfoBuilder.setPage(page);
        pageInfoBuilder.setNumberOfResultPerPage(numberOfResultsPerPage);
        pageInfoBuilder.setTotalNumberOfResult(cities.size());

        SearchCityResponse.Builder responseBuilder = SearchCityResponse.newBuilder();
        responseBuilder.setCities(cities.stream()
                .skip((page - 1) * numberOfResultsPerPage)
                .limit(numberOfResultsPerPage)
                .map(c -> cityConverter.getResourceFrom(c))
                .collect(Collectors.toList()));
        responseBuilder.setPage(pageInfoBuilder.build());
        return responseBuilder.build();
    }

    @RequestMapping("/{id}")
    public CityEntity get(@PathVariable String id) throws Exception { //FIXME
        return cityManager.getCity(id, "en-US");
    }

}
