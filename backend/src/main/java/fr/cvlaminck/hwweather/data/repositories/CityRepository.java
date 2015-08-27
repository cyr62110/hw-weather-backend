package fr.cvlaminck.hwweather.data.repositories;

import fr.cvlaminck.hwweather.data.model.CityEntity;
import fr.cvlaminck.hwweather.data.model.CityExternalIdEntity;
import fr.cvlaminck.hwweather.data.repositories.impl.CityRepositoryCustom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

//Do not use @Repository since they are generated and put in context, otherwise weird exception
public interface CityRepository
        extends MongoRepository<CityEntity, String>, CityRepositoryCustom {

    @Query("{ externalIds : { $elemMatch : ?0 } }")
    public CityEntity findByExternalId(CityExternalIdEntity externalId);

}
