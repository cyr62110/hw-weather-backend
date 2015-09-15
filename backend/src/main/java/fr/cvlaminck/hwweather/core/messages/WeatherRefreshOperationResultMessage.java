package fr.cvlaminck.hwweather.core.messages;

import fr.cvlaminck.hwweather.data.model.WeatherDataType;

import java.util.Collection;
import java.util.Collections;

public class WeatherRefreshOperationResultMessage {
    private String cityId;
    private Collection<WeatherDataType> refreshedTypes = Collections.emptyList();

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public Collection<WeatherDataType> getRefreshedTypes() {
        return refreshedTypes;
    }

    public void setRefreshedTypes(Collection<WeatherDataType> refreshedTypes) {
        this.refreshedTypes = refreshedTypes;
    }
}
