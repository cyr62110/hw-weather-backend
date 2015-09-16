package fr.cvlaminck.hwweather.data.repositories;

import fr.cvlaminck.hwweather.data.model.weather.DailyForecastEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.Collection;

public interface DailyForecastRepository
    extends MongoRepository<DailyForecastEntity, String> {

    public DailyForecastEntity findByCityIdAndWeek(String cityId, LocalDate week);

    public Collection<DailyForecastEntity> findByCityIdAndWeekBetween(String cityId, LocalDate startDate, LocalDate endDate);

}
