package fr.cvlaminck.hwweather.core.managers;

import fr.cvlaminck.hwweather.core.exceptions.NoResultForWeatherRefreshOperationException;
import fr.cvlaminck.hwweather.core.exceptions.RefreshOperationFailedException;
import fr.cvlaminck.hwweather.core.model.RefreshOperationSummary;
import fr.cvlaminck.hwweather.core.model.WeatherData;
import fr.cvlaminck.hwweather.core.utils.DateUtils;
import fr.cvlaminck.hwweather.data.model.WeatherDataType;
import fr.cvlaminck.hwweather.data.model.city.CityEntity;
import fr.cvlaminck.hwweather.data.model.weather.CurrentWeatherEntity;
import fr.cvlaminck.hwweather.data.model.weather.DailyForecastEntity;
import fr.cvlaminck.hwweather.data.model.weather.HourlyForecastEntity;
import fr.cvlaminck.hwweather.data.repositories.CurrentWeatherRepository;
import fr.cvlaminck.hwweather.data.repositories.DailyForecastRepository;
import fr.cvlaminck.hwweather.data.repositories.HourlyForecastRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class WeatherManager {

    @Autowired
    private CurrentWeatherRepository currentWeatherRepository;

    @Autowired
    private HourlyForecastRepository hourlyForecastRepository;

    @Autowired
    private DailyForecastRepository dailyForecastRepository;

    @Autowired
    private WeatherRefreshQueuesManager weatherRefreshQueuesManager;

    public WeatherData getWeather(CityEntity city, Collection<WeatherDataType> requestedTypes) throws NoResultForWeatherRefreshOperationException, RefreshOperationFailedException {
        return getWeather(city, requestedTypes, true);
    }

    public WeatherData getWeather(CityEntity city, Collection<WeatherDataType> requestedTypes, boolean refreshIfNecessary) throws NoResultForWeatherRefreshOperationException, RefreshOperationFailedException {
        WeatherData data = new WeatherData();
        data.setCity(city);
        data.setTypes(requestedTypes);

        fillWeatherDataWithTypesUsingDatabase(data, requestedTypes);

        if (refreshIfNecessary) {
            Collection<WeatherDataType> typesToRefresh = data.getMissingOrInGracePeriodTypes();
            if (!typesToRefresh.isEmpty()) {
                boolean waitForResult = !data.getMissingTypes().isEmpty();

                data.getMetadata().setTypesToRefresh(typesToRefresh);
                data.getMetadata().setHasCausedRefreshOperation(true);
                data.getMetadata().setHasWaitedForRefreshOperationToFinish(waitForResult);

                if (waitForResult) {
                    RefreshOperationSummary summary = weatherRefreshQueuesManager.postRefreshOperationForCityAndWaitForResult(city, typesToRefresh);

                    data.getMetadata().setRefreshedTypes(summary.getRefreshedTypes());
                    data.getMetadata().setNumberOfProviderCalled(summary.getNumberOfProviderCalled());
                    data.getMetadata().setNumberOfFreeCallUsed(summary.getNumberOfFreeCallUsed());
                    data.getMetadata().setOperationCost(summary.getOperationCost());

                    //Once every type has been refreshed, we retrieve the data for those types from the database once again.
                    fillWeatherDataWithTypesUsingDatabase(data, typesToRefresh);
                } else {
                    weatherRefreshQueuesManager.postRefreshForCity(city, typesToRefresh);
                }
            }
        }

        return data;
    }

    private void fillWeatherDataWithTypesUsingDatabase(WeatherData data, Collection<WeatherDataType> types) {
        for (WeatherDataType type : types) {
            switch (type) {
                case WEATHER:
                    fillWeatherDataWithCurrentUsingDatabase(data);
                    break;
                case HOURLY_FORECAST:
                    fillWeatherDataWithHourlyUsingDatabase(data);
                    break;
                case DAILY_FORECAST:
                    fillWeatherDataWithDailyUsingDatabase(data);
                    break;
            }
        }
    }

    private void fillWeatherDataWithCurrentUsingDatabase(WeatherData data) {
        CurrentWeatherEntity current = currentWeatherRepository.findByCityIdAndDay(data.getCity().getId(), DateUtils.today());
        data.setCurrent(current);
    }

    private void fillWeatherDataWithHourlyUsingDatabase(WeatherData data) {
        LocalDate today = DateUtils.today();
        Collection<HourlyForecastEntity> hourlyList = hourlyForecastRepository.findByCityIdAndDayGreaterThanEqual(data.getCity().getId(), today);
        hourlyList = hourlyList.stream()
                .sorted((a, b) -> a.getDay().compareTo(b.getDay()))
                .limit(2)
                .collect(Collectors.toList());
        data.setHourlyList(hourlyList);
    }

    private void fillWeatherDataWithDailyUsingDatabase(WeatherData data) {
        LocalDate firstDayOfWeek = DateUtils.firstDayOfWeek();
        Collection<DailyForecastEntity> dailyList = dailyForecastRepository.findByCityIdAndWeekGreaterThanEqual(data.getCity().getId(), firstDayOfWeek);
        dailyList = dailyList.stream()
                .sorted((a, b) -> a.getWeek().compareTo(b.getWeek()))
                .limit(2)
                .collect(Collectors.toList());
        data.setDailyList(dailyList);
    }
}
