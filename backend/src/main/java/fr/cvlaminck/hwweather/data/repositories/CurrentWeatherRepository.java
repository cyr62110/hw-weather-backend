package fr.cvlaminck.hwweather.data.repositories;

import fr.cvlaminck.hwweather.data.model.weather.CurrentWeatherEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;

public interface CurrentWeatherRepository
        extends MongoRepository<CurrentWeatherEntity, String> {

    @Query("{cityId: ?0, day: ?1}")
    public CurrentWeatherEntity findByCityIdAndDay(String cityId, LocalDate day);

}
