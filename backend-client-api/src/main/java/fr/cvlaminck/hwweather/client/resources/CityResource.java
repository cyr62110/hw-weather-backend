package fr.cvlaminck.hwweather.client.resources;

public class CityResource {
    private String id;
    private ExternalCityIdResource externalId;
    private String name;
    private String country;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ExternalCityIdResource getExternalId() {
        return externalId;
    }

    public void setExternalId(ExternalCityIdResource externalId) {
        this.externalId = externalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
