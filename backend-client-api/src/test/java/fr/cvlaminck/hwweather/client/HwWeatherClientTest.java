package fr.cvlaminck.hwweather.client;

import fr.cvlaminck.hwweather.client.protocol.SearchCityResponse;
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
        assertNotNull(response.getCities());
        assertFalse(response.getCities().isEmpty());
        assertEquals("Paris", response.getCities().iterator().next().getName());
    }

    @Test
    public void testGetWeather() throws Exception {
        /* FIXME Broken until ported to Avro
        ExternalCityIdResource city = new ExternalCityIdResource();
        city.setProvider("nominatim");
        city.setId("58404");

        WeatherResponse response = client.weather().get(city);

        assertNotNull(response);
        assertNotNull(response.getCurrent());
        assertFalse(response.getHourly().isEmpty());
        assertFalse(response.getDaily().isEmpty());
        assertEquals("Lille", response.getCity().getName());
        */
    }
}