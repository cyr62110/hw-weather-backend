package fr.cvlaminck.hwweather.core.listeners;

import fr.cvlaminck.hwweather.core.exceptions.DataProviderException;
import fr.cvlaminck.hwweather.core.exceptions.NoProviderAvailableForRefreshOperationException;
import fr.cvlaminck.hwweather.core.managers.WeatherRefreshManager;
import fr.cvlaminck.hwweather.core.managers.WeatherRefreshQueuesManager;
import fr.cvlaminck.hwweather.core.messages.WeatherRefreshOperationMessage;
import fr.cvlaminck.hwweather.core.model.RefreshOperationSummary;
import fr.cvlaminck.hwweather.data.model.WeatherDataType;
import fr.cvlaminck.hwweather.data.model.city.CityEntity;
import fr.cvlaminck.hwweather.data.repositories.CityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Component
public class WeatherRefreshOperationListener
    implements MessageListener {

    @Autowired
    private MessageConverter messageConverter;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private WeatherRefreshManager weatherRefreshManager;

    @Autowired
    private WeatherRefreshQueuesManager weatherRefreshQueuesManager;

    private Logger log = LoggerFactory.getLogger(WeatherRefreshOperationListener.class);

    @Override
    public void onMessage(Message message) {
        onWeatherRefreshOperationReceived((WeatherRefreshOperationMessage) messageConverter.fromMessage(message));
    }

    private void onWeatherRefreshOperationReceived(WeatherRefreshOperationMessage message) {
        log.info("Weather refresh operation received for city '{}'. Types to refresh: {}", message.getCityId(), message.getTypesToRefresh());
        CityEntity city = cityRepository.findOne(message.getCityId());
        try {
            RefreshOperationSummary summary = weatherRefreshManager.refresh(city, message.getTypesToRefresh());

            log.info("Weather refresh operation finished for city '{}'. Refreshed types: {}", message.getCityId(), summary.getRefreshedTypes());
            weatherRefreshQueuesManager.postRefreshOperationFinishedForCity(city, summary);
        } catch (NoProviderAvailableForRefreshOperationException | DataProviderException e) {
            weatherRefreshQueuesManager.postRefreshOperationFailedForCity(city, message.getTypesToRefresh(), e);
            e.printStackTrace(); //TODO Better handling of the error.
        }
    }
}
