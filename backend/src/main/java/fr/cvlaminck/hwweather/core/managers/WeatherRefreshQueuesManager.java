package fr.cvlaminck.hwweather.core.managers;

import fr.cvlaminck.hwweather.core.exceptions.NoResultForWeatherRefreshOperationException;
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

    public void postRefreshOperationForCityAndWaitIfNecessary(CityEntity city, List<AbstractWeatherDataEntity> weatherData, Collection<WeatherDataType> wantedTypes) throws NoResultForWeatherRefreshOperationException {
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
                //TODO: reload result for database
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

    public void waitUntilCityWeatherIsRefreshed(CityEntity city, Collection<WeatherDataType> typesToRefresh) throws NoResultForWeatherRefreshOperationException {
        //We declare the queue for receiving weather update and bind it to the right exchange
        Queue resultQueue = getQueueForCity(city);

        //TODO get timeout from configuration
        //TODO Find a better way than polling
        WeatherRefreshOperationResultMessage message = null;
        long startTime = System.currentTimeMillis();
        long timeout = 60000l; //For now, 5s
        long timeBetweenPoll = 500; //For now, 500ms

        try {
            while (message == null && (System.currentTimeMillis() - startTime) < timeout) {
                message = (WeatherRefreshOperationResultMessage) amqpTemplate.receiveAndConvert(resultQueue.getName());
                if (message == null) {
                    Thread.sleep(timeBetweenPoll);
                }
            }
        } catch (InterruptedException e) {}

        //We delete the queue since it is no more necessary
        amqpAdmin.deleteQueue(resultQueue.getName());

        if (message == null) {
            throw new NoResultForWeatherRefreshOperationException();
        }

        log.debug("{} {}", message.getCityId(), message.getRefreshedTypes());
        if (!message.getRefreshedTypes().containsAll(typesToRefresh)) {
            //TODO wait for another message
            throw new NoResultForWeatherRefreshOperationException();
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
