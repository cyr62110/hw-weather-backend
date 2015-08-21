package fr.cvlaminck.hwweather.data.model;

public class CityExternalIdEntity {
    private String dataProvider;
    private String id;

    public static boolean isExternalId(String id) {
        return id.startsWith("[");
    }

    public static CityExternalIdEntity parse(String id) {
        return null; //FIXME
    }

    public String getDataProvider() {
        return dataProvider;
    }
    public void setDataProvider(String dataProvider) {
        this.dataProvider = dataProvider;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CityExternalIdEntity)) return false;

        CityExternalIdEntity that = (CityExternalIdEntity) o;

        if (!dataProvider.equals(that.dataProvider)) return false;
        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = dataProvider.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("[%s:%s]", dataProvider, id);
    }
}
