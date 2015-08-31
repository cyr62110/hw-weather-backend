package fr.cvlaminck.hwweather.core.managers;

import fr.cvlaminck.hwweather.core.messages.WeatherRefreshOperationMessage;
import fr.cvlaminck.hwweather.data.model.city.CityEntity;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WeatherManager {

    @Autowired
    WeatherDataProviderManager weatherDataProviderManager;

    @Autowired
    AmqpTemplate amqpTemplate;

    @Autowired
    private Queue weatherRefreshOperationQueue;

    @Autowired
    private Exchange hwWeatherExchange;

    /**
     * City are not refresh immediately by the front. To avoid useless simultaneous call to external weather API,
     * a message is posted in a queue of the message broker representing a refresh operation for a given city.
     */
    public void postUpdate(CityEntity city) {
        WeatherRefreshOperationMessage message = new WeatherRefreshOperationMessage();
        message.setCityId(city.getId());

        amqpTemplate.convertAndSend(hwWeatherExchange.getName(),
                weatherRefreshOperationQueue.getName(),
                message);
    }

    public void refresh(CityEntity city) {

    }

    public void forceUpdate(CityEntity city) {
    }

}
