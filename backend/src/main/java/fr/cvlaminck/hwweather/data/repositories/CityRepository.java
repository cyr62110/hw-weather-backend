package fr.cvlaminck.hwweather.data.repositories;

import fr.cvlaminck.hwweather.data.model.CityEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository
        extends MongoRepository<CityEntity, String> {

}
