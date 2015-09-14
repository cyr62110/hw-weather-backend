package fr.cvlaminck.hwweather.core.model;

import fr.cvlaminck.hwweather.core.external.model.weather.ExternalWeatherDataType;
import fr.cvlaminck.hwweather.core.external.providers.weather.WeatherDataProvider;
import fr.cvlaminck.hwweather.core.model.RefreshPlan;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RefreshPlanTest {

    @Test
    public void testGetOverlap() {
        WeatherDataProvider p1 = mock(WeatherDataProvider.class);
        when(p1.getTypes()).thenReturn(Arrays.asList(ExternalWeatherDataType.CURRENT, ExternalWeatherDataType.HOURLY));

        WeatherDataProvider p2 = mock(WeatherDataProvider.class);
        when(p2.getTypes()).thenReturn(Arrays.asList(ExternalWeatherDataType.CURRENT, ExternalWeatherDataType.DAILY));

        RefreshPlan plan = new RefreshPlan(Arrays.asList(p1, p2));
        assertEquals(1, plan.getOverlap());

        WeatherDataProvider p3 = mock(WeatherDataProvider.class);
        when(p3.getTypes()).thenReturn(Arrays.asList(ExternalWeatherDataType.CURRENT, ExternalWeatherDataType.DAILY, ExternalWeatherDataType.HOURLY));

        plan = new RefreshPlan(Arrays.asList(p1, p2, p3));
        assertEquals(4, plan.getOverlap());
    }

}