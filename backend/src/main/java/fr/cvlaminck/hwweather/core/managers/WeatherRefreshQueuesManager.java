package fr.cvlaminck.hwweather.core.managers;

import fr.cvlaminck.hwweather.core.exceptions.NoResultForWeatherRefreshOperationException;
import fr.cvlaminck.hwweather.core.exceptions.RefreshOperationFailedException;
import fr.cvlaminck.hwweather.core.messages.WeatherRefreshOperationMessage;
import fr.cvlaminck.hwweather.core.messages.WeatherRefreshOperationResultMessage;
import fr.cvlaminck.hwweather.core.model.RefreshOperationSummary;
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

    public RefreshOperationSummary postRefreshOperationForCityAndWaitForResult(CityEntity city, Collection<WeatherDataType> typesToRefresh) throws NoResultForWeatherRefreshOperationException, RefreshOperationFailedException {
        if (!typesToRefresh.isEmpty()) {
            postRefreshForCity(city, typesToRefresh);
            return waitUntilCityWeatherIsRefreshed(city, typesToRefresh);
        }
        return null;
    }

    /**
     * City are not refresh immediately by the front. To avoid useless simultaneous call to external weather API,
     * a message is posted in a queue of the message broker representing a refresh operation for a given city.
     */
    public void postRefreshForCity(CityEntity city, Collection<WeatherDataType> typesToRefresh) {
        if (!typesToRefresh.isEmpty()) {
            log.info("Posting refresh operation for city '{}'. Types to refresh: {}", city.getId(), typesToRefresh);

            WeatherRefreshOperationMessage message = new WeatherRefreshOperationMessage();
            message.setCityId(city.getId());
            message.setTypesToRefresh(typesToRefresh);

            amqpTemplate.convertAndSend(weatherRefreshOperationQueue.getName(),
                    message);
        }
    }

    public RefreshOperationSummary waitUntilCityWeatherIsRefreshed(CityEntity city, Collection<WeatherDataType> typesToRefresh) throws NoResultForWeatherRefreshOperationException, RefreshOperationFailedException {
        RefreshOperationSummary summary = null;

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
                } else {
                    summary = new RefreshOperationSummary();
                    summary.setCity(city);
                    summary.setTypesToRefresh(typesToRefresh);
                    summary.setRefreshedTypes(message.getRefreshedTypes());
                    summary.setNumberOfProviderCalled(message.getNumberOfProviderCalled());
                    summary.setNumberOfFreeCallUsed(message.getNumberOfFreeCallUsed());
                    summary.setOperationCost(message.getOperationCost());
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
        if (!message.isSuccess()) {
            throw new RefreshOperationFailedException(city, typesToRefresh);
        }
        return summary;
    }

    public void postRefreshOperationFinishedForCity(CityEntity city, RefreshOperationSummary summary) {
        WeatherRefreshOperationResultMessage message = new WeatherRefreshOperationResultMessage();
        message.setCityId(city.getId());
        message.setRefreshedTypes(summary.getRefreshedTypes());
        message.setSuccess(true);

        message.setNumberOfProviderCalled(summary.getNumberOfProviderCalled());
        message.setNumberOfFreeCallUsed(summary.getNumberOfFreeCallUsed());
        message.setOperationCost(summary.getOperationCost());

        amqpTemplate.convertAndSend(weatherRefreshOperationResultExchange.getName(),
                city.getId(),
                message);
    }

    public void postRefreshOperationFailedForCity(CityEntity city, Collection<WeatherDataType> typesToRefresh, Throwable t) {
        //TODO find something to send the error through the message broker.
        WeatherRefreshOperationResultMessage message = new WeatherRefreshOperationResultMessage();
        message.setCityId(city.getId());
        message.setRefreshedTypes(typesToRefresh);
        message.setSuccess(false);

        amqpTemplate.convertAndSend(weatherRefreshOperationResultExchange.getName(),
                city.getId(),
                message);
    }
}
