package fr.cvlaminck.hwweather.core.managers;

import fr.cvlaminck.hwweather.core.messages.WeatherRefreshOperationMessage;
import fr.cvlaminck.hwweather.core.messages.WeatherRefreshOperationResultMessage;
import fr.cvlaminck.hwweather.data.model.WeatherDataType;
import fr.cvlaminck.hwweather.data.model.city.CityEntity;
import fr.cvlaminck.hwweather.data.model.weather.AbstractWeatherDataEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

@Component
public class WeatherRefreshQueuesManager {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private Queue weatherRefreshOperationQueue;

    @Autowired
    private TopicExchange weatherRefreshOperationExchange;

    @Autowired
    private TopicExchange weatherRefreshOperationResultExchange;

    private Logger log = LoggerFactory.getLogger(WeatherRefreshQueuesManager.class);

    private Queue getQueueForCity(CityEntity city) {
        String queueName = "weather-refresh-operation-result-" + city.getId()+ "-" + new Random().nextInt(); //TODO: find a way to make it uniq for a thread.

        Queue resultQueue = new Queue(queueName, false, false, true);
        Binding binding = BindingBuilder.bind(resultQueue).to(weatherRefreshOperationResultExchange).with(city.getId());

        amqpAdmin.declareQueue(resultQueue);
        amqpAdmin.declareBinding(binding);

        return resultQueue;
    }

    public void postRefreshOperationForCityIfNecessary(CityEntity city, List<AbstractWeatherDataEntity> weatherData, Collection<WeatherDataType> wantedTypes) {
        Collection<WeatherDataType> typesToRefresh = new ArrayList<>();
        typesToRefresh.addAll(wantedTypes);

        boolean waitForResult = false;

        for (AbstractWeatherDataEntity data : weatherData) {
            if (data == null || data.isExpired()) {
                waitForResult = true;
            } else if (!data.isExpiredOrInGracePeriod()) {
                typesToRefresh.remove(data.getType());
            }
        }

        if (!typesToRefresh.isEmpty()) {
            log.info("Posting refresh operation for city '{}'. Types to refresh: {}", city.getId(), typesToRefresh);
            postRefreshForCity(city, typesToRefresh);
            if (waitForResult) {
                waitUntilCityWeatherIsRefreshed(city, typesToRefresh);
            }
        } else {
            log.info("All weather data are available for city '{}'. Types: {}", wantedTypes);
        }
    }

    /**
     * City are not refresh immediately by the front. To avoid useless simultaneous call to external weather API,
     * a message is posted in a queue of the message broker representing a refresh operation for a given city.
     */
    private void postRefreshForCity(CityEntity city, Collection<WeatherDataType> typesToRefresh) {
        WeatherRefreshOperationMessage message = new WeatherRefreshOperationMessage();
        message.setCityId(city.getId());
        message.setTypesToRefresh(typesToRefresh);

        amqpTemplate.convertAndSend(weatherRefreshOperationQueue.getName(),
                message);
    }

    public void waitUntilCityWeatherIsRefreshed(CityEntity city, Collection<WeatherDataType> typesToRefresh) {
        Queue resultQueue = getQueueForCity(city);

        WeatherRefreshOperationResultMessage message = (WeatherRefreshOperationResultMessage) amqpTemplate.receiveAndConvert(resultQueue.getName());
        log.debug("{} {}", message.getCityId(), message.getRefreshedTypes());
        if (!message.getRefreshedTypes().containsAll(typesToRefresh)) {
            //TODO wait for another message
            //TODO add a maximum time to wait for a refresh operation: ex. 60s.
            throw new RuntimeException("All types not refreshed");
        }
    }

    public void postRefreshOperationFinishedForCity(CityEntity city, Collection<WeatherDataType> typesToRefresh) {
        WeatherRefreshOperationResultMessage message = new WeatherRefreshOperationResultMessage();
        message.setCityId(city.getId());
        message.setRefreshedTypes(typesToRefresh);

        amqpTemplate.convertAndSend(weatherRefreshOperationResultExchange.getName(),
                city.getId(),
                message);
    }
}
