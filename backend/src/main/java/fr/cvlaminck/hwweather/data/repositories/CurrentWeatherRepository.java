package fr.cvlaminck.hwweather.data.repositories;

import fr.cvlaminck.hwweather.data.model.weather.CurrentWeatherEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CurrentWeatherRepository
    extends MongoRepository<CurrentWeatherEntity, String> {

}
