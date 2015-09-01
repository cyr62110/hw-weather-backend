package fr.cvlaminck.hwweather.data.repositories;

import fr.cvlaminck.hwweather.data.model.weather.DailyForecastEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DailyForecastRepository
    extends MongoRepository<DailyForecastEntity, String>{
}
