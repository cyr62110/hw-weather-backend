package fr.cvlaminck.hwweather.core.model;

import fr.cvlaminck.hwweather.data.model.WeatherDataType;
import fr.cvlaminck.hwweather.data.model.weather.CurrentWeatherEntity;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class WeatherDataTest {

    @Test
    public void testGetMissingTypes() throws Exception {
        WeatherData data = new WeatherData();
        data.setTypes(Arrays.asList(WeatherDataType.values()));

        CurrentWeatherEntity current = new CurrentWeatherEntity(60, 60);
        data.setCurrent(current);

        assertEquals(Arrays.asList(WeatherDataType.HOURLY_FORECAST, WeatherDataType.DAILY_FORECAST), data.getMissingTypes());
    }

    @Test
    public void testGetMissingOrInGracePeriodTypes() throws Exception {
        WeatherData data = new WeatherData();
        data.setTypes(Arrays.asList(WeatherDataType.values()));

        CurrentWeatherEntity current = new CurrentWeatherEntity(60, 60);
        data.setCurrent(current);

        assertEquals(Arrays.asList(WeatherDataType.HOURLY_FORECAST, WeatherDataType.DAILY_FORECAST), data.getMissingOrInGracePeriodTypes());
    }
}