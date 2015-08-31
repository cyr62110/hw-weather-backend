package fr.cvlaminck.hwweather.data.model.city;

public class CityExternalIdEntity {
    private String dataProvider;
    private String externalId;

    public CityExternalIdEntity() {
    }

    public CityExternalIdEntity(String dataProvider, String externalId) {
        this.dataProvider = dataProvider;
        this.externalId = externalId;
    }

    public static boolean isExternalId(String id) {
        return id.startsWith("[");
    }

    public static CityExternalIdEntity parse(String id) {
        int separatorIndex = id.indexOf(":");
        if (separatorIndex == -1) {
            throw new IllegalStateException(""); //FIXME
        }
        CityExternalIdEntity externalId = new CityExternalIdEntity();
        externalId.dataProvider = id.substring(1, separatorIndex);
        externalId.externalId = id.substring(separatorIndex + 1, id.length() - 1);
        return externalId;
    }

    public String getDataProvider() {
        return dataProvider;
    }
    public void setDataProvider(String dataProvider) {
        this.dataProvider = dataProvider;
    }
    public String getExternalId() {
        return externalId;
    }
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CityExternalIdEntity)) return false;

        CityExternalIdEntity that = (CityExternalIdEntity) o;

        if (!dataProvider.equals(that.dataProvider)) return false;
        if (!externalId.equals(that.externalId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = dataProvider.hashCode();
        result = 31 * result + externalId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("[%s:%s]", dataProvider, externalId);
    }
}
