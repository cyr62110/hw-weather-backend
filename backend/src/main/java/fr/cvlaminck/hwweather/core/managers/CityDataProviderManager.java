package fr.cvlaminck.hwweather.core.managers;

import fr.cvlaminck.hwweather.core.exceptions.DataProviderException;
import fr.cvlaminck.hwweather.core.exceptions.NoProviderWithNameException;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Collection<ExternalCityResource> searchByName(String name) throws DataProviderException {
        try {
            return getActiveProvider().searchByName(name);
        } catch (fr.cvlaminck.hwweather.core.external.exceptions.DataProviderException e) {
            throw new DataProviderException(e.getDataProvider(), e);
        }
    }

    public ExternalCityResource findById(String providerName, String cityId) throws NoProviderWithNameException, DataProviderException {
        CityDataProvider provider = dataProviderByNameMap.get(providerName);
        if( provider == null ) {
            throw new NoProviderWithNameException(providerName);
        }
        try {
            return provider.findByExternalId(cityId);
        } catch (fr.cvlaminck.hwweather.core.external.exceptions.DataProviderException e) {
            throw new DataProviderException(e.getDataProvider(), e);
        }
    }

}
