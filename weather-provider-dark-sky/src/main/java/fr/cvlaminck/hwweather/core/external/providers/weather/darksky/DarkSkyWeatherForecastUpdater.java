package fr.cvlaminck.hwweather.core.external.providers.weather.darksky;

import fr.cvlaminck.hwweather.core.external.model.weather.ExternalWeatherData;
import fr.cvlaminck.hwweather.core.external.providers.weather.darksky.resources.DarkSkyForecastResponse;
import org.springframework.stereotype.Component;
import retrofit.RestAdapter;

@Component
public class DarkSkyWeatherForecastUpdater {

    public ExternalWeatherData refresh(double latitude, double longitude) {
        DarkSkyForecastResponse response = getRestApi().getForecast(getApiKey(), longitude, latitude);
        return null;
    }

    private DarkSkyWeatherWeatherAPI getRestApi() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://api.forecast.io")
                .build();

        return restAdapter.create(DarkSkyWeatherWeatherAPI.class);
    }

    private String getApiKey() {
        return ""; //FIXME Remove before pushing to GitHub
    }
}
