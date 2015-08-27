package fr.cvlaminck.hwweather.core.external.providers.weather.darksky;

import fr.cvlaminck.hwweather.core.external.providers.weather.darksky.resources.DarkSkyForecastResponse;
import retrofit.http.GET;
import retrofit.http.Path;

public interface DarkSkyWeatherWeatherAPI {
    @GET("/forecast/{apiKey}/{lat},{lon}")
    public DarkSkyForecastResponse getForecast(@Path("apiKey") String apiKey, @Path("lat") double lat, @Path("lon") double lon);
}
