package fr.cvlaminck.hwweather.data.model.weather;

import fr.cvlaminck.hwweather.data.model.ExpirableEntity;
import fr.cvlaminck.hwweather.data.model.WeatherDataType;

public abstract class AbstractWeatherDataEntity
    extends ExpirableEntity {

    protected AbstractWeatherDataEntity() {
    }

    protected AbstractWeatherDataEntity(int expiryInSeconds, int gracePeriodInSeconds) {
        super(expiryInSeconds, gracePeriodInSeconds);
    }

    public abstract WeatherDataType getType();
}
