package fr.cvlaminck.hwweather.front.converters;

import fr.cvlaminck.hwweather.client.protocol.CityResource;
import fr.cvlaminck.hwweather.client.protocol.ExternalCityIdResource;
import fr.cvlaminck.hwweather.core.external.model.city.ExternalCityResource;
import fr.cvlaminck.hwweather.data.model.city.CityEntity;
import org.springframework.stereotype.Component;

@Component
public class CityConverter {

    public CityResource getResourceFrom(CityEntity entity, String languageCode) {
        CityEntity.InternationalizedInformation info = entity.getInternationalizedInformation(languageCode);

        CityResource.Builder resourceBuilder = CityResource.newBuilder();
        resourceBuilder.setId(entity.getId());
        resourceBuilder.setName(info.getName());
        resourceBuilder.setCountry(info.getCountry());

        return resourceBuilder.build();
    }

    public CityResource getResourceFrom(ExternalCityResource external) {
        ExternalCityIdResource.Builder externalIdBuilder = ExternalCityIdResource.newBuilder();
        externalIdBuilder.setProvider(external.getProvider());
        externalIdBuilder.setId(external.getExternalId());

        CityResource.Builder resourceBuilder = CityResource.newBuilder();
        resourceBuilder.setId(null);
        resourceBuilder.setExternalId(externalIdBuilder.build());
        resourceBuilder.setName(external.getName());
        resourceBuilder.setCountry(external.getCountry());

        return resourceBuilder.build();
    }

}
