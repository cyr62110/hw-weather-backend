package fr.cvlaminck.hwweather.core.managers;

import fr.cvlaminck.hwweather.data.model.CityEntity;
import fr.cvlaminck.hwweather.data.model.CityExternalIdEntity;
import fr.cvlaminck.hwweather.data.repositories.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CityManager {

    @Autowired
    private CityRepository cityRepository;

    public CityEntity getCity(String id, String languageCode) {
        CityEntity city = null;
        if (CityExternalIdEntity.isExternalId(id)) {
            city = getOrImportCityWithExternalId(CityExternalIdEntity.parse(id));
        } else {
            city = cityRepository.findOne(id);
        }
        return refreshCityIfMissingI18NName(city, languageCode);
    }

    private CityEntity getOrImportCityWithExternalId(CityExternalIdEntity externalId) {
        return null;
    }

    private CityEntity refreshCityIfMissingI18NName(CityEntity city, String languageCode) {
        if (city == null) {
            return null;
        }
        //FIXME: For now, we do nothing for i18n :)
        return city;
    }

}
