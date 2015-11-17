package fr.cvlaminck.hwweather.front.converters;

import fr.cvlaminck.hwweather.client.resources.CityResource;
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

    public fr.cvlaminck.hwweather.client.protocol.CityResource getResourceFrom(ExternalCityResource external) {
        fr.cvlaminck.hwweather.client.protocol.ExternalCityIdResource.Builder externalIdBuilder = fr.cvlaminck.hwweather.client.protocol.ExternalCityIdResource.newBuilder();
        externalIdBuilder.setProvider(external.getProvider());
        externalIdBuilder.setId(external.getExternalId());

        fr.cvlaminck.hwweather.client.protocol.CityResource.Builder resourceBuilder = fr.cvlaminck.hwweather.client.protocol.CityResource.newBuilder();
        resourceBuilder.setId(null);
        resourceBuilder.setExternalId(externalIdBuilder.build());
        resourceBuilder.setName(external.getName());
        resourceBuilder.setCountry(external.getCountry());

        return resourceBuilder.build();
    }

}
