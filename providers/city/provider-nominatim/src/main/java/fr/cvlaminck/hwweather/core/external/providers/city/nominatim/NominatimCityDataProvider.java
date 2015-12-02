package fr.cvlaminck.hwweather.core.external.providers.city.nominatim;

import fr.cvlaminck.hwweather.core.external.annotations.DataProvider;
import fr.cvlaminck.hwweather.core.external.exceptions.DataProviderException;
import fr.cvlaminck.hwweather.core.external.model.location.city.ExternalCityResource;
import fr.cvlaminck.hwweather.core.external.providers.city.CityDataProvider;
import fr.cvlaminck.nominatim.Nominatim;
import fr.cvlaminck.nominatim.exceptions.NominatimAPIException;
import fr.cvlaminck.nominatim.model.OsmType;
import fr.cvlaminck.nominatim.model.Place;
import fr.cvlaminck.nominatim.model.PlaceType;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@DataProvider
public class NominatimCityDataProvider
        implements CityDataProvider {

    private final static Collection<PlaceType> SUPPORTED_PLACE_TYPES = Arrays.asList(PlaceType.CITY, PlaceType.HAMLET, PlaceType.TOWN);

    @Override
    public String getProviderName() {
        return "nominatim";
    }

    @Override
    public Collection<ExternalCityResource> searchByName(String name) throws DataProviderException {
        List<Place> results = null;
        try {
            results = Nominatim.search()
                    .city(name)
                    .acceptLanguage(Arrays.asList("en-US")) //FIXME For now, I dont want to care about the language of the result.
                    .get();
        } catch (IOException e) {
            throw new DataProviderException(this, "Error while communicating with API", e);
        } catch (NominatimAPIException e) {
            throw new DataProviderException(this, "Error in result format", e);
        }

        return results.stream()
                .filter((p) -> SUPPORTED_PLACE_TYPES.contains(p.getType()))
                .map((p) -> convertPlaceToResource(p))
                .collect(Collectors.toList());
    }

    @Override
    public ExternalCityResource findByExternalId(String id) throws DataProviderException {
        List<Place> results = null;
        try {
            results = Nominatim.lookUp()
                    .id(OsmType.RELATION, Long.parseLong(id))
                    .acceptLanguage(Arrays.asList("en-US")) //FIXME For now, I dont want to care about the language of the result.
                    .get();
        } catch (IOException e) {
            throw new DataProviderException(this, "Error while communicating with API", e);
        } catch (NominatimAPIException e) {
            throw new DataProviderException(this, "Error in result format", e);
        } catch (NumberFormatException e) {
            throw new DataProviderException(this, "Id format is not supported by this provider", e);
        }
        return results.stream()
                .map((p) -> convertPlaceToResource(p))
                .findFirst().orElse(null);
    }

    private ExternalCityResource convertPlaceToResource(Place p) {
        ExternalCityResource city = new ExternalCityResource();
        city.setExternalId(Long.toString(p.getOsmId()));
        city.setProvider(this.getProviderName());
        city.setName(getName(p));
        city.setCountry(p.getAddress().getCountry());
        city.setLatitude(p.getLatitude());
        city.setLongitude(p.getLongitude());
        return city;
    }

    private String getName(Place p) {
        if (p.getAddress().getCity() != null) {
            return p.getAddress().getCity();
        }
        if (p.getAddress().getTown() != null) {
            return p.getAddress().getTown();
        }
        if (p.getAddress().getHamlet() != null) {
            return p.getAddress().getHamlet();
        }
        throw new IllegalStateException("Cannot find name for city with external id " + p.getOsmId());
    }
}
