package fr.cvlaminck.hwweather.core.managers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Collection;

import fr.cvlaminck.hwweather.core.external.exceptions.SearchByNameException;
import fr.cvlaminck.hwweather.core.external.model.city.ExternalCityResource;
import fr.cvlaminck.hwweather.core.external.providers.city.CityDataProvider;

@Component
public class CityDataProviderManager {

    //FIXME Only support a single city data provider
    @Autowired
    private CityDataProvider dataProvider;

    public Collection<ExternalCityResource> searchByName(String name, Pageable pageable) throws SearchByNameException {
        //FIXME pageable is ignored for now
        Collection<ExternalCityResource> cities = dataProvider.searchByName(name);
        return cities;
    }

}
