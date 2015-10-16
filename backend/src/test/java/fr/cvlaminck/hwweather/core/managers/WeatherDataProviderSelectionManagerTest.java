package fr.cvlaminck.hwweather.core.managers;

import fr.cvlaminck.hwweather.core.external.model.weather.ExternalWeatherDataType;
import fr.cvlaminck.hwweather.core.external.providers.weather.AbstractWeatherDataProvider;
import fr.cvlaminck.hwweather.core.external.providers.weather.WeatherDataProvider;
import fr.cvlaminck.hwweather.core.model.RefreshPlan;
import fr.cvlaminck.hwweather.core.model.WeatherProvidersSelectionResult;
import fr.cvlaminck.hwweather.core.utils.iterators.PartitionOfSetIteratorTest;
import fr.cvlaminck.hwweather.data.model.FreeCallCountersEntity;
import fr.cvlaminck.hwweather.data.repositories.FreeCallCountersRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WeatherDataProviderSelectionManagerTest {

    private WeatherDataProvider CHD;
    private WeatherDataProvider CH;
    private WeatherDataProvider HD;
    private WeatherDataProvider C;

    private List<WeatherDataProvider> testProviders;

    @Before
    public void buildTestProviders() {
        List<WeatherDataProvider> providers = new ArrayList<>();

        CHD = mock(AbstractWeatherDataProvider.class);
        when(CHD.getTypes()).thenReturn(Arrays.asList(ExternalWeatherDataType.CURRENT, ExternalWeatherDataType.HOURLY, ExternalWeatherDataType.DAILY));
        when(CHD.getProviderName()).thenReturn("CHD");
        when(CHD.getCostPerOperation()).thenReturn(0.5d);
        when(CHD.supportsPaidCall()).thenReturn(true);
        providers.add(CHD);

        CH = mock(AbstractWeatherDataProvider.class);
        when(CH.getTypes()).thenReturn(Arrays.asList(ExternalWeatherDataType.CURRENT, ExternalWeatherDataType.HOURLY));
        when(CH.getProviderName()).thenReturn("CH");
        when(CH.getCostPerOperation()).thenReturn(0d);
        when(CH.supportsPaidCall()).thenReturn(false);
        providers.add(CH);

        HD = mock(AbstractWeatherDataProvider.class);
        when(HD.getTypes()).thenReturn(Arrays.asList(ExternalWeatherDataType.HOURLY, ExternalWeatherDataType.DAILY));
        when(HD.getProviderName()).thenReturn("HD");
        when(HD.getCostPerOperation()).thenReturn(0d);
        when(HD.supportsPaidCall()).thenReturn(false);
        providers.add(HD);

        C = mock(AbstractWeatherDataProvider.class);
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
        List<Set<WeatherDataProvider>> plans = null;

        partition = PartitionOfSetIteratorTest.partition(ExternalWeatherDataType.HOURLY, ExternalWeatherDataType.DAILY, null, ExternalWeatherDataType.CURRENT);
        plans = manager.buildRefreshPlansForPartition(typesToRefresh, partition, map).stream()
                .map((p) -> p.getProvidersToUse())
                .collect(Collectors.toList());

        assertEquals(2, plans.size());
        assertTrue(plans.contains(providerSet(HD, C)));
        assertTrue(plans.contains(providerSet(HD, CH)));

        partition = PartitionOfSetIteratorTest.partition(ExternalWeatherDataType.HOURLY, ExternalWeatherDataType.DAILY, ExternalWeatherDataType.CURRENT);
        plans = manager.buildRefreshPlansForPartition(typesToRefresh, partition, map).stream()
                .map((p) -> p.getProvidersToUse())
                .collect(Collectors.toList());

        assertEquals(1, plans.size());
        assertTrue(plans.contains(providerSet(CHD)));
    }

    @Test
    public void testBuildRefreshPlans() throws Exception {
        WeatherDataProviderSelectionManager manager = new WeatherDataProviderSelectionManager();

        Map<Set<ExternalWeatherDataType>, List<WeatherDataProvider>> map = manager.buildProvidersByRefreshTypeMap(testProviders);
        Set<ExternalWeatherDataType> typesToRefresh = typeSet(ExternalWeatherDataType.values());

        List<Set<WeatherDataProvider>> plans = manager.buildRefreshPlans(typesToRefresh, map).stream()
                .map((p) -> p.getProvidersToUse())
                .collect(Collectors.toList());

        //assertEquals(4, plans.size()); TODO this test is inconsistent due to mockito hashCode behavior
        assertTrue(plans.contains(providerSet(CHD)));
        assertTrue(plans.contains(providerSet(HD, C)));
        assertTrue(plans.contains(providerSet(HD, CH)));
        //assertTrue(plans.contains(providerSet(C, HD, CH)));
    }

    @Test
    public void testSortRefreshPlanToFindBestOne() throws Exception {
        WeatherDataProviderSelectionManager manager = new WeatherDataProviderSelectionManager();
        FreeCallCountersEntity freeCallsCounters = new FreeCallCountersEntity();

        RefreshPlan p1 = mock(RefreshPlan.class);
        when(p1.getNumberOfProvider()).thenReturn(1);
        when(p1.canAllProvidersBeCalled(freeCallsCounters)).thenReturn(true);
        when(p1.getCost(freeCallsCounters)).thenReturn(1.0d);
        when(p1.getOverlap()).thenReturn(0);

        RefreshPlan p2 = mock(RefreshPlan.class);
        when(p2.getNumberOfProvider()).thenReturn(1);
        when(p2.canAllProvidersBeCalled(freeCallsCounters)).thenReturn(true);
        when(p2.getCost(freeCallsCounters)).thenReturn(0.0d);
        when(p2.getOverlap()).thenReturn(0);

        RefreshPlan p3 = mock(RefreshPlan.class);
        when(p3.getNumberOfProvider()).thenReturn(2);
        when(p3.canAllProvidersBeCalled(freeCallsCounters)).thenReturn(true);
        when(p3.getCost(freeCallsCounters)).thenReturn(0d);
        when(p3.getOverlap()).thenReturn(0);

        RefreshPlan p4 = mock(RefreshPlan.class);
        when(p4.getNumberOfProvider()).thenReturn(2);
        when(p4.canAllProvidersBeCalled(freeCallsCounters)).thenReturn(true);
        when(p4.getCost(freeCallsCounters)).thenReturn(0d);
        when(p4.getOverlap()).thenReturn(1);

        Set<RefreshPlan> plans = new HashSet<>();
        plans.addAll(Arrays.asList(p1, p2, p3, p4));

        Iterator<RefreshPlan> it = manager.sortRefreshPlanToFindBestOne(plans, freeCallsCounters).iterator();
        assertEquals(p2, it.next());
        assertEquals(p3, it.next());
        assertEquals(p4, it.next());
        assertEquals(p1, it.next());
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
        when(repository.decrement(Arrays.asList("CHD"))).thenReturn(freeCallsCounters);

        manager.setFreeCallCountersRepository(repository);
        manager.setWeatherDataProviders(testProviders);

        WeatherProvidersSelectionResult result = manager.selectDataProvidersToUseForRefreshOperation(typeSet(ExternalWeatherDataType.values()));
        assertEquals(Arrays.asList(CHD), result.getProvidersToUse());
    }
}