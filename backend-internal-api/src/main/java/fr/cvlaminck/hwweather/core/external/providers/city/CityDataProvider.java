package fr.cvlaminck.hwweather.core.external.providers.city;

import fr.cvlaminck.hwweather.core.external.exceptions.CityDataProviderException;
import fr.cvlaminck.hwweather.core.external.model.city.ExternalCityResource;
import fr.cvlaminck.hwweather.core.external.providers.DataProvider;

import java.util.Collection;

public interface CityDataProvider
        extends DataProvider {

    Collection<ExternalCityResource> searchByName(String name) throws CityDataProviderException;

    ExternalCityResource findByExternalId(String id) throws CityDataProviderException;

}
