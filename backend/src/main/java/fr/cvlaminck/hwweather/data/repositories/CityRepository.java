package fr.cvlaminck.hwweather.data.repositories;

import fr.cvlaminck.hwweather.data.model.CityEntity;
import fr.cvlaminck.hwweather.data.model.CityExternalIdEntity;
import fr.cvlaminck.hwweather.data.repositories.extensions.CityRepositoryExtension;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository
        extends MongoRepository<CityEntity, String>, CityRepositoryExtension {

    @Query("{ externalIds : { $in : ?0 } }")
    public CityEntity findByExternalId(CityExternalIdEntity externalId);

}
