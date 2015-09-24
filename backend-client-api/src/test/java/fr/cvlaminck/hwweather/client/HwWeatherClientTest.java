package fr.cvlaminck.hwweather.client;

import fr.cvlaminck.hwweather.client.reponses.city.SearchCityResponse;
import fr.cvlaminck.hwweather.client.reponses.weather.WeatherResponse;
import fr.cvlaminck.hwweather.client.resources.ExternalCityIdResource;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HwWeatherClientTest {

    HwWeatherClient client;

    @Before
    public void setUp() throws Exception {
        client = new HwWeatherClientFactory()
                .baseUrl("http://localhost:8080")
                .build();
    }

    @Test
    public void testSearchCity() throws Exception {
        SearchCityResponse response = client.cities().search("Paris");

        assertNotNull(response);
        assertNotNull(response.getResults());
        assertFalse(response.getResults().isEmpty());
        assertEquals("Paris", response.getResults().iterator().next().getName());
    }

    @Test
    public void testGetWeather() throws Exception {
        ExternalCityIdResource city = new ExternalCityIdResource();
        city.setProvider("nominatim");
        city.setId("58404");

        WeatherResponse response = client.weather().get(city);

        assertNotNull(response);
        assertNotNull(response.getCurrent());
        assertFalse(response.getHourly().isEmpty());
        assertFalse(response.getDaily().isEmpty());
        assertEquals("Lille", response.getCity().getName());
    }
}