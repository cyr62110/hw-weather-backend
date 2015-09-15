package fr.cvlaminck.hwweather.config;

import fr.cvlaminck.hwweather.core.listeners.WeatherRefreshOperationListener;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageBrokerConfig {

    @Bean
    public ConnectionFactory messageBrokerConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("owl.rmq.cloudamqp.com");
        connectionFactory.setUsername("sihbhphn");
        connectionFactory.setPassword("qPRcVb1gwcXmaEuwYmT1FPvTQIsb2J1l");
        connectionFactory.setVirtualHost("sihbhphn");
        return connectionFactory;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    @Autowired
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    @Autowired
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    @Bean
    public TopicExchange weatherRefreshOperationExchange() {
        return new TopicExchange("weather-refresh-operation");
    }

    @Bean
    public TopicExchange weatherRefreshOperationResultExchange() { return new TopicExchange("weather-refresh-operation-result"); }

    @Bean
    public Queue weatherRefreshOperationQueue() {
        return new Queue("weather-refresh-operation", true);
    }

    @Bean
    @Autowired
    public Binding weatherRefreshOperationBinding(Queue weatherRefreshOperationQueue, TopicExchange weatherRefreshOperationExchange) {
        return BindingBuilder.bind(weatherRefreshOperationQueue).to(weatherRefreshOperationExchange).with("weather-refresh-operation");
    }

    @Bean
    @Autowired
    public SimpleMessageListenerContainer weatherRefreshOperationListenerContainer(ConnectionFactory connectionFactory,
                                                                                   Queue weatherRefreshOperationQueue,
                                                                                   WeatherRefreshOperationListener listener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addQueues(weatherRefreshOperationQueue);
        container.setMessageListener(listener);
        return container;
    }
}
