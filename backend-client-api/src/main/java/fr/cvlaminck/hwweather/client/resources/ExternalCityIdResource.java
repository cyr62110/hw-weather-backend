package fr.cvlaminck.hwweather.client.resources;

public class ExternalCityIdResource {
    private String provider;
    private String id;

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
        return "["+provider+":"+id+"]";
    }
}
