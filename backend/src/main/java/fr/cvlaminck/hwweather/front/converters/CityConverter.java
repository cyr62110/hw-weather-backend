package fr.cvlaminck.hwweather.front.converters;

import fr.cvlaminck.hwweather.client.resources.CityResource;
import fr.cvlaminck.hwweather.client.resources.ExternalCityIdResource;
import fr.cvlaminck.hwweather.core.external.model.city.ExternalCityResource;
import fr.cvlaminck.hwweather.data.model.city.CityEntity;
import org.springframework.stereotype.Component;

@Component
public class CityConverter {

    public CityResource getResourceFrom(CityEntity entity, String languageCode) {
        CityEntity.InternationalizedInformation info = entity.getInternationalizedInformation(languageCode);

        CityResource resource = new CityResource();
        resource.setId(entity.getId());
        resource.setName(info.getName());
        resource.setCountry(info.getCountry());

        return resource;
    }

    public CityResource getResourceFrom(ExternalCityResource external) {
        ExternalCityIdResource id = new ExternalCityIdResource();
        id.setProvider(external.getProvider());
        id.setId(external.getExternalId());

        CityResource resource = new CityResource();
        resource.setExternalId(id);
        resource.setName(external.getName());
        resource.setCountry(external.getCountry());

        return resource;
    }

}
