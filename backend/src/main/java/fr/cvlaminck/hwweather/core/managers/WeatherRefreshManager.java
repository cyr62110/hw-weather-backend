package fr.cvlaminck.hwweather.core.managers;

import fr.cvlaminck.hwweather.core.exceptions.NoProviderAvailableForRefreshOperationException;
import fr.cvlaminck.hwweather.core.external.model.weather.*;
import fr.cvlaminck.hwweather.core.messages.WeatherRefreshOperationMessage;
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
import org.omg.CORBA.Current;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class WeatherRefreshManager {

    @Autowired
    private WeatherDataProviderManager weatherDataProviderManager;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private Queue weatherRefreshOperationQueue;

    @Autowired
    private Exchange hwWeatherExchange;

    @Autowired
    private CurrentWeatherRepository currentWeatherRepository;

    @Autowired
    private DailyForecastRepository dailyForecastRepository;

    @Autowired
    private HourlyForecastRepository hourlyForecastRepository;

    private Logger log = LoggerFactory.getLogger(WeatherRefreshManager.class);

    /**
     * Post a refresh operation message for the city with the data type of the entity if
     * the entity is at least in grace period
     */
    public void postRefreshIfNecessary(ExpirableEntity expirableEntity, CityEntity city) {
        if (expirableEntity.isExpiredOrInGracePeriod()) {
            Collection<WeatherDataType> typesToRefresh = Arrays.asList(getDataType(expirableEntity));
            postRefresh(city, typesToRefresh);
        }
    }

    private WeatherDataType getDataType(ExpirableEntity expirableEntity) {
        if (expirableEntity instanceof CurrentWeatherEntity) {
            return WeatherDataType.WEATHER;
        }
        if (expirableEntity instanceof HourlyForecastEntity) {
            return WeatherDataType.HOURLY_FORECAST;
        }
        if (expirableEntity instanceof DailyForecastEntity) {
            return WeatherDataType.DAILY_FORECAST;
        }
        throw new IllegalArgumentException(String.format("Expirable entity of type '%s' has no associated WeatherDataType.",
                expirableEntity.getClass().getSimpleName()));
    }

    /**
     * City are not refresh immediately by the front. To avoid useless simultaneous call to external weather API,
     * a message is posted in a queue of the message broker representing a refresh operation for a given city.
     */
    public void postRefresh(CityEntity city, Collection<WeatherDataType> typesToRefresh) {
        WeatherRefreshOperationMessage message = new WeatherRefreshOperationMessage();
        message.setCityId(city.getId());

        amqpTemplate.convertAndSend(hwWeatherExchange.getName(),
                weatherRefreshOperationQueue.getName(),
                message);
    }

    public Collection<WeatherDataType> refresh(CityEntity city, Collection<WeatherDataType> typesToRefresh) throws NoProviderAvailableForRefreshOperationException {
        List<WeatherDataType> refreshedTypes = new ArrayList<>();
        typesToRefresh = filterAlreadyRefreshedType(city, typesToRefresh);
        if (typesToRefresh.isEmpty()) {
            return refreshedTypes;
        }
        ExternalWeatherData data = weatherDataProviderManager.refresh(city.getLatitude(), city.getLongitude(), getExternalWeatherDataTypesToRefresh(typesToRefresh));

        CurrentWeatherEntity current = getCurrentWeatherFromData(city, data);
        if (current != null) {
            refreshedTypes.add(WeatherDataType.WEATHER);
            currentWeatherRepository.save(current);
        }
        HourlyForecastEntity hourly = getHourlyForecastFromData(city, data);
        if (hourly != null) {
            refreshedTypes.add(WeatherDataType.DAILY_FORECAST);
            hourlyForecastRepository.save(hourly);
        }
        DailyForecastEntity daily = getDailyForecastFromData(city, data);
        if (daily != null) {
            refreshedTypes.add(WeatherDataType.HOURLY_FORECAST);
            dailyForecastRepository.save(daily);
        }
        return refreshedTypes;
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
        current.setTemperature(resource.getTemperature());
        return current;
    }

    private HourlyForecastEntity getHourlyForecastFromData(CityEntity city, ExternalWeatherData data) {
        if (data.getHourly() == null || data.getHourly().isEmpty()) {
            return null;
        }

        int expiryInSeconds = getExpiryInSecond(WeatherDataType.HOURLY_FORECAST);
        int gracePeriodInSeconds = getGracePeriodInSeconds(WeatherDataType.HOURLY_FORECAST);

        HourlyForecastEntity hourly = new HourlyForecastEntity(expiryInSeconds, gracePeriodInSeconds);
        hourly.setCityId(city.getId());
        hourly.setDay(data.getHourly().iterator().next().getHour().toLocalDate());
        for (ExternalHourlyForecastResource resource : data.getHourly()) {
            HourlyForecastEntity.ByHourForecast byHour = new HourlyForecastEntity.ByHourForecast();
            byHour.setHour(resource.getHour());
            byHour.setWeatherCondition(toWeatherCondition(resource.getWeatherCondition()));
            byHour.setTemperature(resource.getTemperature());
            hourly.getHourByHourForecasts().add(byHour);
        }
        return hourly;
    }

    private DailyForecastEntity getDailyForecastFromData(CityEntity city, ExternalWeatherData data) {
        if (data.getDaily() == null || data.getDaily().isEmpty()) {
            return null;
        }

        int expiryInSeconds = getExpiryInSecond(WeatherDataType.DAILY_FORECAST);
        int gracePeriodInSeconds = getGracePeriodInSeconds(WeatherDataType.DAILY_FORECAST);

        LocalDate day = data.getDaily().iterator().next().getDay();
        LocalDate atStartOfWeek = day
                .minusDays(day.getDayOfWeek().getValue() - 1);

        DailyForecastEntity daily = new DailyForecastEntity(expiryInSeconds, gracePeriodInSeconds);
        daily.setCityId(city.getId());
        daily.setWeek(atStartOfWeek);
        for (ExternalDailyForecastResource resource : data.getDaily()) {
            DailyForecastEntity.ByDayForecast byDay = new DailyForecastEntity.ByDayForecast();
            byDay.setDay(resource.getDay());
            byDay.setWeatherCondition(toWeatherCondition(resource.getWeatherCondition()));
            byDay.setMaxTemperature(resource.getMaxTemperature());
            byDay.setMinTemperature(resource.getMinTemperature());
            daily.getDayByDayForecasts().add(byDay);
        }
        return daily;
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
