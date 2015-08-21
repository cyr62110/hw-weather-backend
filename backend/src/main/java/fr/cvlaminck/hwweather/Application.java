package fr.cvlaminck.hwweather;

import fr.cvlaminck.hwweather.config.MongoConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableAutoConfiguration
@Import(MongoConfig.class)
@ComponentScan(basePackages = "fr.cvlaminck.hwweather")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

}
