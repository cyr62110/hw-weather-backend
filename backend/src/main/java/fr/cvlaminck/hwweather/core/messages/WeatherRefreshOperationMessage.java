package fr.cvlaminck.hwweather.core.messages;

import fr.cvlaminck.hwweather.data.model.WeatherDataType;

import java.util.Collection;
import java.util.Collections;

public class WeatherRefreshOperationMessage {
    public static final String QUEUE = "weather-refresh-operation";

    private String cityId;
    private Collection<WeatherDataType> typesToRefresh = Collections.emptyList();

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public Collection<WeatherDataType> getTypesToRefresh() {
        return typesToRefresh;
    }

    public void setTypesToRefresh(Collection<WeatherDataType> typesToRefresh) {
        this.typesToRefresh = typesToRefresh;
    }
}
