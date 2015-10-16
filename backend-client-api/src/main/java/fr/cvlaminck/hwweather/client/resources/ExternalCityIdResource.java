package fr.cvlaminck.hwweather.client.resources;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ExternalCityIdResource {
    private String provider;
    private String id;

    @JsonIgnore
    public boolean isValid() {
        return (id != null && !id.isEmpty()) && (provider != null && !provider.isEmpty());
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "[" + provider + ":" + id + "]";
    }
}
