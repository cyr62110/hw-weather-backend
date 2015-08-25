package fr.cvlaminck.hwweather.core.managers;

import fr.cvlaminck.hwweather.core.exceptions.NoProviderWithNameException;
import fr.cvlaminck.hwweather.core.external.exceptions.CityDataProviderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.cvlaminck.hwweather.core.external.model.city.ExternalCityResource;
import fr.cvlaminck.hwweather.core.external.providers.city.CityDataProvider;

import javax.annotation.PostConstruct;

@Component
public class CityDataProviderManager {

    @Autowired
    private List<CityDataProvider> dataProviders;

    private Map<String, CityDataProvider> dataProviderByNameMap;

    @PostConstruct
    private void postConstruct() {
        dataProviderByNameMap = new HashMap<>(dataProviders.size());
        for (CityDataProvider dataProvider : dataProviders) {
            dataProviderByNameMap.put(dataProvider.getProviderName(), dataProvider);
        }
    }

    private CityDataProvider getActiveProvider() {
        return dataProviders.get(0);
    }

    public Collection<ExternalCityResource> searchByName(String name, Pageable pageable) throws CityDataProviderException {
        //FIXME pageable is ignored for now
        Collection<ExternalCityResource> cities = getActiveProvider().searchByName(name);
        return cities;
    }

    public ExternalCityResource findById(String providerName, String cityId) throws NoProviderWithNameException, CityDataProviderException {
        CityDataProvider provider = dataProviderByNameMap.get(providerName);
        if( provider == null ) {
            throw new NoProviderWithNameException(providerName);
        }
        return provider.findByExternalId(cityId);
    }

}
