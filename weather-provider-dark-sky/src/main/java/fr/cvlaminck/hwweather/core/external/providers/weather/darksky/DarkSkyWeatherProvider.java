package fr.cvlaminck.hwweather.core.external.providers.weather.darksky;

import fr.cvlaminck.hwweather.core.external.model.weather.ExternalWeatherData;
import fr.cvlaminck.hwweather.core.external.model.weather.ExternalWeatherDataType;
import fr.cvlaminck.hwweather.core.external.providers.weather.WeatherDataProvider;

import java.util.Collection;

public class DarkSkyWeatherProvider
    implements WeatherDataProvider {

    @Override
    public String getProviderName() {
        return "darksky";
    }

    @Override
    public Double getCostPerOperation() {
        return 0d; //FIXME: Set the real price of an operation
    }

    @Override
    public Long getNumberOfFreeOperationPerDay() {
        return 1000l;
    }

    @Override
    public Collection<ExternalWeatherDataType> getTypes() {
        return null;
    }

    @Override
    public ExternalWeatherData refresh(double latitude, double longitude) {
        return null;
    }


}
