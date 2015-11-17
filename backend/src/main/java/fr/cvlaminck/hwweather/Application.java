package fr.cvlaminck.hwweather;

import fr.cvlaminck.hwweather.config.messaging.MessageBrokerConfig;
import fr.cvlaminck.hwweather.config.data.MongoConfig;
import fr.cvlaminck.hwweather.config.web.WebMVCConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.util.TimeZone;

@EnableAutoConfiguration
@Import({MongoConfig.class, MessageBrokerConfig.class, WebMVCConfig.class})
@ComponentScan(basePackages = "fr.cvlaminck.hwweather")
public class Application {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        SpringApplication.run(Application.class);
    }

}
