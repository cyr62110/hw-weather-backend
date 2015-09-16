package fr.cvlaminck.hwweather.data.repositories;

import fr.cvlaminck.hwweather.data.model.weather.HourlyForecastEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface HourlyForecastRepository
    extends MongoRepository<HourlyForecastEntity, String> {

    public HourlyForecastEntity findByCityIdAndDay(String cityId, LocalDate day);

    public Collection<HourlyForecastEntity> findByCityIdAndDayBetween(String cityId, LocalDate startDate, LocalDate endDate);

}
