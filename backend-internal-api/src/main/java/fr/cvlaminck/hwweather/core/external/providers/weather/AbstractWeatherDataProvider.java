package fr.cvlaminck.hwweather.core.external.providers.weather;

import java.util.Objects;

public abstract class AbstractWeatherDataProvider
    implements WeatherDataProvider {

    @Override
    public int hashCode() {
        return getProviderName().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WeatherDataProvider)) return false;
        WeatherDataProvider that = (WeatherDataProvider) o;
        return Objects.equals(getProviderName(), that.getProviderName());
    }
}
