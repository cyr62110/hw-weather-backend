package fr.cvlaminck.hwweather.core.managers;

import fr.cvlaminck.hwweather.core.exceptions.DataProviderException;
import fr.cvlaminck.hwweather.core.exceptions.NoProviderAvailableForRefreshOperationException;
import fr.cvlaminck.hwweather.core.external.model.weather.*;
import fr.cvlaminck.hwweather.core.model.RefreshOperationSummary;
import fr.cvlaminck.hwweather.data.model.ExpirableEntity;
import fr.cvlaminck.hwweather.data.model.WeatherDataType;
import fr.cvlaminck.hwweather.data.model.city.CityEntity;
import fr.cvlaminck.hwweather.data.model.weather.CurrentWeatherEntity;
import fr.cvlaminck.hwweather.data.model.weather.DailyForecastEntity;
import fr.cvlaminck.hwweather.data.model.weather.HourlyForecastEntity;
import fr.cvlaminck.hwweather.data.model.weather.WeatherConditionEntity;
import fr.cvlaminck.hwweather.data.repositories.CurrentWeatherRepository;
import fr.cvlaminck.hwweather.data.repositories.DailyForecastRepository;
import fr.cvlaminck.hwweather.data.repositories.HourlyForecastRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class WeatherRefreshManager {

    @Autowired
    private WeatherDataProviderManager weatherDataProviderManager;

    @Autowired
    private CurrentWeatherRepository currentWeatherRepository;

    @Autowired
    private DailyForecastRepository dailyForecastRepository;

    @Autowired
    private HourlyForecastRepository hourlyForecastRepository;

    public RefreshOperationSummary refresh(CityEntity city, Collection<WeatherDataType> typesToRefresh) throws NoProviderAvailableForRefreshOperationException, DataProviderException {
        RefreshOperationSummary summary = new RefreshOperationSummary();
        summary.setCity(city);
        summary.setTypesToRefresh(typesToRefresh);

        List<WeatherDataType> refreshedTypes = new ArrayList<>();
        typesToRefresh = filterAlreadyRefreshedType(city, typesToRefresh);
        if (typesToRefresh.isEmpty()) {
            return summary;
        }
        ExternalWeatherData data = weatherDataProviderManager.refresh(city.getLatitude(), city.getLongitude(), getExternalWeatherDataTypesToRefresh(typesToRefresh));
        summary.setNumberOfProviderCalled(data.getMetadata().getNumberOfProviderCalled());
        summary.setNumberOfFreeCallUsed(data.getMetadata().getNumberOfFreeCallUsed());
        summary.setOperationCost(data.getMetadata().getOperationCost());

        CurrentWeatherEntity current = getCurrentWeatherFromData(city, data);
        if (current != null) {
            refreshedTypes.add(WeatherDataType.WEATHER);
            CurrentWeatherEntity olderCurrent = currentWeatherRepository.findByCityIdAndDay(current.getCityId(), current.getDay());
            if (olderCurrent != null) {
                current.setId(olderCurrent.getId());
            }
            currentWeatherRepository.save(current);
        }
        Collection<HourlyForecastEntity> hourlyForecasts = getHourlyForecastsFromData(city, data);
        if (hourlyForecasts != null) {
            refreshedTypes.add(WeatherDataType.HOURLY_FORECAST);
            for (HourlyForecastEntity hourly : hourlyForecasts) {
                HourlyForecastEntity olderHourly = hourlyForecastRepository.findByCityIdAndDay(hourly.getCityId(), hourly.getDay());
                if (olderHourly != null) {
                    hourly.setId(olderHourly.getId());
                }
                hourlyForecastRepository.save(hourly);
            }
        }
        Collection<DailyForecastEntity> dailyForecasts = getDailyForecastsFromData(city, data);
        if (dailyForecasts != null) {
            refreshedTypes.add(WeatherDataType.DAILY_FORECAST);
            for (DailyForecastEntity daily : dailyForecasts) {
                DailyForecastEntity olderDaily = dailyForecastRepository.findByCityIdAndWeek(daily.getCityId(), daily.getWeek());
                if (olderDaily != null) {
                    daily.setId(olderDaily.getId());
                }
                dailyForecastRepository.save(daily);
            }
        }

        summary.setRefreshedTypes(refreshedTypes);
        return summary;
    }

    private Collection<WeatherDataType> filterAlreadyRefreshedType(CityEntity city, Collection<WeatherDataType> typesToRefresh) {
        return typesToRefresh.stream()
                .filter((typeToRefresh) -> shouldRefreshTypeForCity(city, typeToRefresh))
                .collect(Collectors.toList());
    }

    private boolean shouldRefreshTypeForCity(CityEntity city, WeatherDataType typeToRefresh) {
        ExpirableEntity expirableEntity = findExpirableEntityForType(city, typeToRefresh);
        if (expirableEntity == null) {
            return true;
        }
        return expirableEntity.isExpiredOrInGracePeriod();
    }

    private ExpirableEntity findExpirableEntityForType(CityEntity city, WeatherDataType typeToRefresh) {
        ExpirableEntity entity = null;
        switch (typeToRefresh) {
            case WEATHER:
                LocalDate day = LocalDate.now(ZoneId.of("UTC"));
                entity = currentWeatherRepository.findByCityIdAndDay(city.getId(), day);
                break;
            case HOURLY_FORECAST:
                //FIXME get entity in db
                break;
            case DAILY_FORECAST:
                //FIXME get entity in db
                break;
            default:
                throw new IllegalArgumentException(); //FIXME exception message
        }
        return entity;
    }

    private Set<ExternalWeatherDataType> getExternalWeatherDataTypesToRefresh(Collection<WeatherDataType> weatherDataTypes) {
        return weatherDataTypes.stream()
                .map((type) -> {
                    switch (type) {
                        case WEATHER:
                            return ExternalWeatherDataType.CURRENT;
                        case HOURLY_FORECAST:
                            return ExternalWeatherDataType.HOURLY;
                        case DAILY_FORECAST:
                            return ExternalWeatherDataType.DAILY;
                    }
                    return null;
                })
                .collect(Collectors.toSet());
    }

    private CurrentWeatherEntity getCurrentWeatherFromData(CityEntity city, ExternalWeatherData data) {
        if (data.getCurrent() == null) {
            return null;
        }
        ExternalCurrentWeatherResource resource = data.getCurrent();

        int expiryInSeconds = getExpiryInSecond(WeatherDataType.WEATHER);
        int gracePeriodInSeconds = getGracePeriodInSeconds(WeatherDataType.WEATHER);

        CurrentWeatherEntity current = new CurrentWeatherEntity(expiryInSeconds, gracePeriodInSeconds);
        current.setCityId(city.getId());
        current.setDay(resource.getTime().toLocalDate());
        current.setWeatherCondition(toWeatherCondition(resource.getWeatherCondition()));
        current.setTemperatureInCelsius(resource.getTemperature());
        return current;
    }

    private Collection<HourlyForecastEntity> getHourlyForecastsFromData(CityEntity city, ExternalWeatherData data) {
        if (data.getHourly() == null || data.getHourly().isEmpty()) {
            return null;
        }

        Map<LocalDate, HourlyForecastEntity> hourlyMap = new HashMap<>();

        int expiryInSeconds = getExpiryInSecond(WeatherDataType.HOURLY_FORECAST);
        int gracePeriodInSeconds = getGracePeriodInSeconds(WeatherDataType.HOURLY_FORECAST);

        for (ExternalHourlyForecastResource resource : data.getHourly()) {
            LocalDate date = resource.getHour().toLocalDate();
            HourlyForecastEntity hourly = hourlyMap.get(date);
            if (hourly == null) {
                hourly = new HourlyForecastEntity(expiryInSeconds, gracePeriodInSeconds);
                hourly.setCityId(city.getId());
                hourly.setDay(date);
                hourlyMap.put(date, hourly);
            }

            HourlyForecastEntity.ByHourForecast byHour = new HourlyForecastEntity.ByHourForecast();
            byHour.setHour(resource.getHour());
            byHour.setWeatherCondition(toWeatherCondition(resource.getWeatherCondition()));
            byHour.setTemperatureInCelsius(resource.getTemperature());

            hourly.getHourByHourForecasts().add(byHour);
        }
        return hourlyMap.values();
    }

    private Collection<DailyForecastEntity> getDailyForecastsFromData(CityEntity city, ExternalWeatherData data) {
        if (data.getDaily() == null || data.getDaily().isEmpty()) {
            return null;
        }

        Map<LocalDate, DailyForecastEntity> dailyMap = new HashMap<>();

        int expiryInSeconds = getExpiryInSecond(WeatherDataType.DAILY_FORECAST);
        int gracePeriodInSeconds = getGracePeriodInSeconds(WeatherDataType.DAILY_FORECAST);

        for (ExternalDailyForecastResource resource : data.getDaily()) {
            LocalDate day = resource.getDay();
            LocalDate atStartOfWeek = day
                    .minusDays(day.getDayOfWeek().getValue() - 1);
            DailyForecastEntity daily = dailyMap.get(atStartOfWeek);
            if (daily == null) {
                daily = new DailyForecastEntity(expiryInSeconds, gracePeriodInSeconds);
                daily.setCityId(city.getId());
                daily.setWeek(atStartOfWeek);
                dailyMap.put(atStartOfWeek, daily);
            }

            DailyForecastEntity.ByDayForecast byDay = new DailyForecastEntity.ByDayForecast();
            byDay.setDay(resource.getDay());
            byDay.setWeatherCondition(toWeatherCondition(resource.getWeatherCondition()));
            byDay.setMaxTemperatureInCelsius(resource.getMaxTemperature());
            byDay.setMinTemperatureInCelsius(resource.getMinTemperature());
            daily.getDayByDayForecasts().add(byDay);
        }
        return dailyMap.values();
    }

    private WeatherConditionEntity toWeatherCondition(ExternalWeatherCondition weatherCondition) {
        return null;
    }

    private int getExpiryInSecond(WeatherDataType type) {
        //TODO read values for a configuration
        switch (type) {
            case WEATHER:
                return 15 * 60;
            case HOURLY_FORECAST:
                return 1 * 60 * 60;
            case DAILY_FORECAST:
                return 6 * 60 * 60;
        }
        return Integer.MAX_VALUE;
    }

    private int getGracePeriodInSeconds(WeatherDataType type) {
        //TODO read values for a configuration
        switch (type) {
            case WEATHER:
                return 15 * 60;
            case HOURLY_FORECAST:
                return 30 * 60;
            case DAILY_FORECAST:
                return 3 * 60 * 60;
        }
        return 0;
    }

    public void forceUpdate(CityEntity city) {
    }

}
