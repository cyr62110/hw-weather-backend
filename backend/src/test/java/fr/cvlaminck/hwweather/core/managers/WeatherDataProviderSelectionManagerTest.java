package fr.cvlaminck.hwweather.core.managers;

import fr.cvlaminck.hwweather.core.external.model.weather.ExternalWeatherData;
import fr.cvlaminck.hwweather.core.external.model.weather.ExternalWeatherDataType;
import fr.cvlaminck.hwweather.core.external.providers.weather.WeatherDataProvider;
import fr.cvlaminck.hwweather.core.utils.stats.PartitionOfSetIterator;
import fr.cvlaminck.hwweather.core.utils.stats.PartitionOfSetIteratorTest;
import fr.cvlaminck.hwweather.data.model.FreeCallCountersEntity;
import fr.cvlaminck.hwweather.data.model.WeatherDataType;
import fr.cvlaminck.hwweather.data.repositories.FreeCallCountersRepository;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
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

        CHD = mock(WeatherDataProvider.class);
        when(CHD.getTypes()).thenReturn(Arrays.asList(ExternalWeatherDataType.CURRENT, ExternalWeatherDataType.HOURLY, ExternalWeatherDataType.DAILY));
        when(CHD.getProviderName()).thenReturn("CHD");
        when(CHD.getCostPerOperation()).thenReturn(0.5d);
        when(CHD.supportsPaidCall()).thenReturn(true);
        providers.add(CHD);

        CH = mock(WeatherDataProvider.class);
        when(CH.getTypes()).thenReturn(Arrays.asList(ExternalWeatherDataType.CURRENT, ExternalWeatherDataType.HOURLY));
        when(CH.getProviderName()).thenReturn("CH");
        when(CH.getCostPerOperation()).thenReturn(0d);
        when(CH.supportsPaidCall()).thenReturn(false);
        providers.add(CH);

        HD = mock(WeatherDataProvider.class);
        when(HD.getTypes()).thenReturn(Arrays.asList(ExternalWeatherDataType.HOURLY, ExternalWeatherDataType.DAILY));
        when(HD.getProviderName()).thenReturn("HD");
        when(HD.getCostPerOperation()).thenReturn(0d);
        when(HD.supportsPaidCall()).thenReturn(false);
        providers.add(HD);

        C = mock(WeatherDataProvider.class);
        when(C.getTypes()).thenReturn(Arrays.asList(ExternalWeatherDataType.CURRENT));
        when(C.getProviderName()).thenReturn("C");
        when(C.getCostPerOperation()).thenReturn(0d);
        when(C.supportsPaidCall()).thenReturn(false);
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

    private Set<WeatherDataProvider> providerSet(WeatherDataProvider... providers) {
        Set<WeatherDataProvider> set = new HashSet<>();
        for (WeatherDataProvider p : providers) {
            set.add(p);
        }
        return set;
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

        List<Set<ExternalWeatherDataType>> partition = null;
        Set<WeatherDataProviderSelectionManager.RefreshPlan> plans = null;
        Iterator<WeatherDataProviderSelectionManager.RefreshPlan> it = null;

        partition = PartitionOfSetIteratorTest.partition(ExternalWeatherDataType.HOURLY, ExternalWeatherDataType.DAILY, null, ExternalWeatherDataType.CURRENT);
        plans = manager.buildRefreshPlansForPartition(typesToRefresh, partition, map);

        assertEquals(2, plans.size());
        it = plans.iterator();
        assertEquals(providerSet(HD, CH), it.next().getProvidersToUse());
        assertEquals(providerSet(HD, C), it.next().getProvidersToUse());

        partition = PartitionOfSetIteratorTest.partition(ExternalWeatherDataType.HOURLY, ExternalWeatherDataType.DAILY, ExternalWeatherDataType.CURRENT);
        plans = manager.buildRefreshPlansForPartition(typesToRefresh, partition, map);

        assertEquals(1, plans.size());
        it = plans.iterator();
        assertEquals(providerSet(CHD), it.next().getProvidersToUse());
    }

    @Test
    public void testBuildRefreshPlans() throws Exception {
        WeatherDataProviderSelectionManager manager = new WeatherDataProviderSelectionManager();

        Map<Set<ExternalWeatherDataType>, List<WeatherDataProvider>> map = manager.buildProvidersByRefreshTypeMap(testProviders);
        Set<ExternalWeatherDataType> typesToRefresh = typeSet(ExternalWeatherDataType.values());

        Set<WeatherDataProviderSelectionManager.RefreshPlan> plans = manager.buildRefreshPlans(typesToRefresh, map);

        assertEquals(4, plans.size());
        Iterator<WeatherDataProviderSelectionManager.RefreshPlan> it = plans.iterator();
        assertEquals(providerSet(HD, C), it.next().getProvidersToUse());
        assertEquals(providerSet(HD, CH), it.next().getProvidersToUse());
        assertEquals(providerSet(C, CH, HD), it.next().getProvidersToUse());
        assertEquals(providerSet(CHD), it.next().getProvidersToUse());
    }

    @Test
    public void testSelectDataProvidersToUseForRefreshOperation() throws Exception {
        WeatherDataProviderSelectionManager manager = new WeatherDataProviderSelectionManager();

        FreeCallCountersEntity freeCallsCounters = new FreeCallCountersEntity();
        Map<String, Integer> counters = new HashMap<>();
        counters.put("CHD", 100);
        counters.put("CH", 100);
        counters.put("HD", 100);
        counters.put("C", 100);
        freeCallsCounters.setCounters(counters);

        FreeCallCountersRepository repository = mock(FreeCallCountersRepository.class);
        when(repository.findFreeCallsLeftForToday()).thenReturn(freeCallsCounters);
        when(repository.decrement(Arrays.asList("CH", "C"))).thenReturn(new FreeCallCountersEntity());

        manager.setFreeCallCountersRepository(repository);
        manager.setWeatherDataProviders(testProviders);

        assertEquals(Arrays.asList(HD, C), manager.selectDataProvidersToUseForRefreshOperation(Arrays.asList(WeatherDataType.values())));
    }

    @Test
    public void testSelectDataProvidersToUseForRefreshOperation_withOneExhausted() throws Exception {
        WeatherDataProviderSelectionManager manager = new WeatherDataProviderSelectionManager();

        FreeCallCountersEntity freeCallsCounters = new FreeCallCountersEntity();
        Map<String, Integer> counters = new HashMap<>();
        counters.put("CHD", 100);
        counters.put("CH", 100);
        counters.put("HD", 100);
        counters.put("C", 0);
        freeCallsCounters.setCounters(counters);

        FreeCallCountersRepository repository = mock(FreeCallCountersRepository.class);
        when(repository.findFreeCallsLeftForToday()).thenReturn(freeCallsCounters);
        when(repository.decrement(Arrays.asList("CH", "HD"))).thenReturn(new FreeCallCountersEntity());

        manager.setFreeCallCountersRepository(repository);
        manager.setWeatherDataProviders(testProviders);

        assertEquals(Arrays.asList(HD, CH), manager.selectDataProvidersToUseForRefreshOperation(Arrays.asList(WeatherDataType.values())));
    }

    @Test
    public void testSelectDataProvidersToUseForRefreshOperation_withAllFreeExhausted() throws Exception {
        WeatherDataProviderSelectionManager manager = new WeatherDataProviderSelectionManager();

        FreeCallCountersEntity freeCallsCounters = new FreeCallCountersEntity();
        Map<String, Integer> counters = new HashMap<>();
        counters.put("CHD", 100);
        counters.put("CH", 100);
        counters.put("HD", 100);
        counters.put("C", 100);
        freeCallsCounters.setCounters(counters);

        FreeCallCountersRepository repository = mock(FreeCallCountersRepository.class);
        when(repository.findFreeCallsLeftForToday()).thenReturn(freeCallsCounters);

        manager.setFreeCallCountersRepository(repository);
        manager.setWeatherDataProviders(testProviders);

        assertEquals(Arrays.asList(CHD), manager.selectDataProvidersToUseForRefreshOperation(Arrays.asList(WeatherDataType.values())));
    }
}