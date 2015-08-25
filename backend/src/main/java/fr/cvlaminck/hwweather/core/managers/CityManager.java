package fr.cvlaminck.hwweather.core.managers;

import fr.cvlaminck.hwweather.core.exceptions.NoProviderWithNameException;
import fr.cvlaminck.hwweather.core.external.exceptions.CityDataProviderException;
import fr.cvlaminck.hwweather.core.external.model.city.ExternalCityResource;
import fr.cvlaminck.hwweather.data.model.CityEntity;
import fr.cvlaminck.hwweather.data.model.CityExternalIdEntity;
import fr.cvlaminck.hwweather.data.repositories.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CityManager {

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CityDataProviderManager cityDataProviderManager;

    public CityEntity getCity(String id, String languageCode) throws CityDataProviderException, NoProviderWithNameException {
        CityEntity city = null;
        if (CityExternalIdEntity.isExternalId(id)) {
            city = getOrImportCityWithExternalId(CityExternalIdEntity.parse(id));
        } else {
            city = cityRepository.findOne(id);
        }
        return refreshCityIfMissingI18NName(city, languageCode);
    }

    private CityEntity getOrImportCityWithExternalId(CityExternalIdEntity externalId, String languageCode) throws CityDataProviderException, NoProviderWithNameException {
        CityEntity city = cityRepository.findByExternalId(externalId);
        if(externalId == null) {
            city = importCityWithExternalId(externalId, languageCode);
        }
        return city;
    }

    private CityEntity importCityWithExternalId(CityExternalIdEntity externalId, String languageCode) throws CityDataProviderException, NoProviderWithNameException {
        ExternalCityResource externalCity = cityDataProviderManager.findById(externalId.getDataProvider(), externalId.getId());
        if (externalCity == null) {
            return null;
        }
        CityEntity city = convertFromExternalResource(externalCity, languageCode);
        return cityRepository.findByExternalIdAndModifyOrCreate(externalId, city);
    }

    private CityEntity convertFromExternalResource(ExternalCityResource externalCity, String languageCode) {
        CityEntity city = new CityEntity();
        city.setLocation(city.getLongitude(), city.getLatitude());

        CityEntity.InternationalizedInformation info = new CityEntity.InternationalizedInformation();
        info.setName(externalCity.getName());
        info.setCountry(externalCity.getCountry());
        city.addInternationalizedInformation(languageCode, info);

        return city;
    }

    private CityEntity refreshCityIfMissingI18NName(CityEntity city, String languageCode) {
        if (city == null) {
            return null;
        }
        //FIXME: For now, we do nothing for i18n :)
        return city;
    }

}
