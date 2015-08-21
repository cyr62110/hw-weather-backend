package fr.cvlaminck.hwweather.core.external.providers.city;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import fr.cvlaminck.hwweather.core.annotations.DataProvider;
import fr.cvlaminck.hwweather.core.external.exceptions.SearchByNameException;
import fr.cvlaminck.hwweather.core.external.model.city.ExternalCityResource;
import fr.cvlaminck.nominatim.Nominatim;
import fr.cvlaminck.nominatim.exceptions.NominatimAPIException;
import fr.cvlaminck.nominatim.model.Place;
import fr.cvlaminck.nominatim.model.PlaceType;

@DataProvider
public class NominatimCityDataProvider
        implements CityDataProvider {

    private final static Collection<PlaceType> SUPPORTED_PLACE_TYPES = Arrays.asList(PlaceType.CITY, PlaceType.HAMLET, PlaceType.TOWN);

    @Override
    public String getProviderName() {
        return "nominatim";
    }

    @Override
    public Collection<ExternalCityResource> searchByName(String name) throws SearchByNameException {
        List<Place> results = null;
        try {
            results = Nominatim.search()
                    .city(name)
                    .acceptLanguage(Arrays.asList("en-US")) //FIXME For now, I dont want to care about the language of the result.
                    .get();
        } catch (NominatimAPIException | IOException e) {
            throw new SearchByNameException(this.getClass(), e);
        }

        return results.stream()
                .filter((p) -> SUPPORTED_PLACE_TYPES.contains(p.getType()))
                .map((p) -> {
                    ExternalCityResource city = new ExternalCityResource();
                    city.setExternalId(Long.toString(p.getOsmId()));
                    city.setProvider(this.getProviderName());
                    city.setName(getName(p));
                    city.setCountry(p.getAddress().getCountry());
                    city.setLatitude(p.getLatitude());
                    city.setLongitude(p.getLongitude());
                    return city;
                })
                .collect(Collectors.toList());
    }

    private String getName(Place p) {
        switch (p.getType()) {
            case CITY:
                return p.getAddress().getCity();
            case TOWN:
                return p.getAddress().getTown();
            case HAMLET:
                return p.getAddress().getHamlet();
        }
        throw new IllegalStateException("Cannot find name for city with external id " + p.getOsmId());
    }

    @Override
    public ExternalCityResource findByExternalId(String id) {
        return null;
    }
}
