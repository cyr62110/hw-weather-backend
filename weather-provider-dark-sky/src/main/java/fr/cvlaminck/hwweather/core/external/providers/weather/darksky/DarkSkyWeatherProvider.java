package fr.cvlaminck.hwweather.core.external.providers.weather.darksky;

import fr.cvlaminck.hwweather.core.external.annotations.DataProvider;
import fr.cvlaminck.hwweather.core.external.exceptions.DataProviderException;
import fr.cvlaminck.hwweather.core.external.model.weather.ExternalWeatherData;
import fr.cvlaminck.hwweather.core.external.model.weather.ExternalWeatherDataType;
import fr.cvlaminck.hwweather.core.external.providers.weather.WeatherDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import retrofit.RestAdapter;

import java.util.Arrays;
import java.util.Collection;

@DataProvider
public class DarkSkyWeatherProvider
    implements WeatherDataProvider {

    private DarkSkyWeatherForecastUpdater updater = new DarkSkyWeatherForecastUpdater(this);

    @Override
    public String getProviderName() {
        return "darksky";
    }

    @Override
    public boolean supportsPaidCall() {
        return false; //FIXME return true once everything is written for non-free API.
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
        return Arrays.asList(
                ExternalWeatherDataType.CURRENT,
                ExternalWeatherDataType.DAILY,
                ExternalWeatherDataType.HOURLY
        );
    }

    @Override
    public ExternalWeatherData refresh(double latitude, double longitude, Collection<ExternalWeatherDataType> typesToRefresh)
        throws DataProviderException {
        return updater.refresh(latitude, longitude);
    }

}
