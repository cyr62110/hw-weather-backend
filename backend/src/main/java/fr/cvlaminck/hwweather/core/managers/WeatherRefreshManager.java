package fr.cvlaminck.hwweather.core.managers;

import fr.cvlaminck.hwweather.core.external.model.weather.ExternalWeatherData;
import fr.cvlaminck.hwweather.core.external.model.weather.ExternalWeatherDataType;
import fr.cvlaminck.hwweather.core.external.providers.weather.WeatherDataProvider;
import fr.cvlaminck.hwweather.core.messages.WeatherRefreshOperationMessage;
import fr.cvlaminck.hwweather.data.model.ExpirableEntity;
import fr.cvlaminck.hwweather.data.model.WeatherDataType;
import fr.cvlaminck.hwweather.data.model.city.CityEntity;
import fr.cvlaminck.hwweather.data.model.weather.CurrentWeatherEntity;
import fr.cvlaminck.hwweather.data.model.weather.DailyForecastEntity;
import fr.cvlaminck.hwweather.data.model.weather.HourlyForecastEntity;
import fr.cvlaminck.hwweather.data.repositories.CurrentWeatherRepository;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class WeatherRefreshManager {

    @Autowired
    WeatherDataProviderManager weatherDataProviderManager;

    @Autowired
    AmqpTemplate amqpTemplate;

    @Autowired
    private Queue weatherRefreshOperationQueue;

    @Autowired
    private Exchange hwWeatherExchange;

    /**
     * Post a refresh operation message for the city with the data type of the entity if
     * the entity is at least in grace period
     */
    public void postUpdateIfNecessary(ExpirableEntity expirableEntity, CityEntity city) {
        if (expirableEntity.isExpiredOrInGracePeriod()) {
            Collection<WeatherDataType> typesToRefresh = Arrays.asList(getDataType(expirableEntity));
            postUpdate(city, typesToRefresh);
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
    public void postUpdate(CityEntity city, Collection<WeatherDataType> dataTypesToRefresh) {
        WeatherRefreshOperationMessage message = new WeatherRefreshOperationMessage();
        message.setCityId(city.getId());

        amqpTemplate.convertAndSend(hwWeatherExchange.getName(),
                weatherRefreshOperationQueue.getName(),
                message);
    }

    public void refresh(CityEntity city, Collection<WeatherDataType> weatherDataTypes) {
        //FIXME
    }

    private Collection<WeatherDataType> filterAlreadyRefreshedType(CityEntity city, Collection<WeatherDataType> typesToRefresh) {
        return typesToRefresh.stream()
                .filter((typeToRefresh) -> shouldRefreshTypeForCity(city, typeToRefresh))
                .collect(Collectors.toList());
    }

    private boolean shouldRefreshTypeForCity(CityEntity city, WeatherDataType typeToRefresh) {

    }

    private ExpirableEntity findExpirableEntityForType(CityEntity city, WeatherDataType typeToRefresh) {
        ExpirableEntity entity = null;
        switch (typeToRefresh) {
            case WEATHER:
                //FIXME get entity in db
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
        return null;
    }

    private Collection<ExternalWeatherDataType> getExternalWeatherDataTypesToRefresh(Collection<WeatherDataType> weatherDataTypes) {
        Collection<ExternalWeatherDataType> externalWeatherDataTypes = new ArrayList<>();
        for (WeatherDataType type : weatherDataTypes) {
            ExternalWeatherDataType externalType = null;
            switch (type) {
                case WEATHER:
                    externalType = ExternalWeatherDataType.CURRENT;
                    break;
                case HOURLY_FORECAST:
                    externalType = ExternalWeatherDataType.HOURLY;
                    break;
                case DAILY_FORECAST:
                    externalType = ExternalWeatherDataType.DAILY;
                    break;
            }
            externalWeatherDataTypes.add(externalType);
        }
        return externalWeatherDataTypes;
    }

    public void forceUpdate(CityEntity city) {
    }

}
