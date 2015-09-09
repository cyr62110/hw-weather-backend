package fr.cvlaminck.hwweather.core.managers;

import fr.cvlaminck.hwweather.core.external.model.weather.ExternalWeatherData;
import fr.cvlaminck.hwweather.core.external.model.weather.ExternalWeatherDataType;
import fr.cvlaminck.hwweather.core.external.providers.weather.WeatherDataProvider;
import fr.cvlaminck.hwweather.core.utils.stats.PartitionOfSetIterator;
import fr.cvlaminck.hwweather.core.utils.stats.PartitionOfSetIteratorTest;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class WeatherDataProviderSelectionManagerTest {

    private WeatherDataProvider CHD;
    private WeatherDataProvider CH;
    private WeatherDataProvider HD;
    private WeatherDataProvider C;

    private List<WeatherDataProvider> testProviders;

    @Before
    public void buildTestProviders() {
        List<WeatherDataProvider> providers = new ArrayList<>();
        WeatherDataProvider provider = null;

        CHD = mock(WeatherDataProvider.class);
        when(CHD.getTypes()).thenReturn(Arrays.asList(ExternalWeatherDataType.CURRENT, ExternalWeatherDataType.HOURLY, ExternalWeatherDataType.DAILY));
        providers.add(CHD);

        CH = mock(WeatherDataProvider.class);
        when(CH.getTypes()).thenReturn(Arrays.asList(ExternalWeatherDataType.CURRENT, ExternalWeatherDataType.HOURLY));
        providers.add(CH);

        HD = mock(WeatherDataProvider.class);
        when(HD.getTypes()).thenReturn(Arrays.asList(ExternalWeatherDataType.HOURLY, ExternalWeatherDataType.DAILY));
        providers.add(HD);

        C = mock(WeatherDataProvider.class);
        when(C.getTypes()).thenReturn(Arrays.asList(ExternalWeatherDataType.CURRENT));
        providers.add(C);

        testProviders = providers;
    }

    private Set<ExternalWeatherDataType> typeSet(ExternalWeatherDataType... types) {
        Set<ExternalWeatherDataType> typeSet = new HashSet<>();
        for (ExternalWeatherDataType type : types) {
            typeSet.add(type);
        }
        return typeSet;
    }

    @Test
    public void testBuildProvidersByRefreshTypeMap() throws Exception {
        WeatherDataProviderSelectionManager manager = new WeatherDataProviderSelectionManager();

        Map<Set<ExternalWeatherDataType>, List<WeatherDataProvider>> map = manager.buildProvidersByRefreshTypeMap(testProviders);
        assertEquals(Arrays.asList(CHD), map.get(typeSet(ExternalWeatherDataType.CURRENT, ExternalWeatherDataType.HOURLY, ExternalWeatherDataType.DAILY)));
        assertEquals(Arrays.asList(CHD, CH), map.get(typeSet(ExternalWeatherDataType.CURRENT, ExternalWeatherDataType.HOURLY)));
        assertEquals(Arrays.asList(CHD, HD), map.get(typeSet(ExternalWeatherDataType.HOURLY, ExternalWeatherDataType.DAILY)));
        assertEquals(Arrays.asList(CHD, CH, C), map.get(typeSet(ExternalWeatherDataType.CURRENT)));
    }

    @Test
    public void testBuildRefreshPlansForPartition() throws Exception {
        WeatherDataProviderSelectionManager manager = new WeatherDataProviderSelectionManager();

        Map<Set<ExternalWeatherDataType>, List<WeatherDataProvider>> map = manager.buildProvidersByRefreshTypeMap(testProviders);
        Set<ExternalWeatherDataType> typesToRefresh = typeSet(ExternalWeatherDataType.values());
        List<Set<ExternalWeatherDataType>> partition = PartitionOfSetIteratorTest.partition(ExternalWeatherDataType.HOURLY, ExternalWeatherDataType.DAILY, null, ExternalWeatherDataType.CURRENT);

        List<WeatherDataProviderSelectionManager.RefreshPlan> plans = manager.buildRefreshPlansForPartition(typesToRefresh, partition, map);

        assertEquals(2, plans.size());
        assertEquals(Arrays.asList(HD, CH), plans.get(0).getProvidersToUse());
        assertEquals(Arrays.asList(HD, C), plans.get(1).getProvidersToUse());
    }

    @Test
    public void testBuildRefreshPlans() throws Exception {
        WeatherDataProviderSelectionManager manager = new WeatherDataProviderSelectionManager();

        Map<Set<ExternalWeatherDataType>, List<WeatherDataProvider>> map = manager.buildProvidersByRefreshTypeMap(testProviders);
        Set<ExternalWeatherDataType> typesToRefresh = typeSet(ExternalWeatherDataType.values());

        List<WeatherDataProviderSelectionManager.RefreshPlan> plans = manager.buildRefreshPlans(typesToRefresh, map);

        for (WeatherDataProviderSelectionManager.RefreshPlan p : plans) {
            System.out.println(p.getProvidersToUse());
        }

        assertEquals(4, plans.size());
        assertEquals(Arrays.asList(CHD), plans.get(0).getProvidersToUse());
        assertEquals(Arrays.asList(CH, HD), plans.get(1).getProvidersToUse());
        assertEquals(Arrays.asList(HD, CH), plans.get(2).getProvidersToUse());
        assertEquals(Arrays.asList(HD, C), plans.get(3).getProvidersToUse());
    }
}