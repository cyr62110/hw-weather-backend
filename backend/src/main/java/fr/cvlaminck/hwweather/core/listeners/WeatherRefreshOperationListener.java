package fr.cvlaminck.hwweather.core.listeners;

import fr.cvlaminck.hwweather.core.messages.WeatherRefreshOperationMessage;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WeatherRefreshOperationListener
    implements MessageListener {

    @Autowired
    private MessageConverter messageConverter;

    @Override
    public void onMessage(Message message) {
        onWeatherRefreshOperationReceived((WeatherRefreshOperationMessage) messageConverter.fromMessage(message));
    }

    private void onWeatherRefreshOperationReceived(WeatherRefreshOperationMessage message) {
        System.out.println(message.getCityId());
    }
}
