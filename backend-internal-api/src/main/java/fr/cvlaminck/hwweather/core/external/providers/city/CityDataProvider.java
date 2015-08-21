package fr.cvlaminck.hwweather.core.external.providers.city;

import fr.cvlaminck.hwweather.core.external.exceptions.SearchByNameException;
import fr.cvlaminck.hwweather.core.external.model.city.ExternalCityResource;
import fr.cvlaminck.hwweather.core.external.providers.DataProvider;

import java.util.Collection;

public interface CityDataProvider
        extends DataProvider {

    Collection<ExternalCityResource> searchByName(String name) throws SearchByNameException;

    ExternalCityResource findByExternalId(String id);

}
