package fr.cvlaminck.hwweather.data.repositories.extensions;

import com.mongodb.WriteResult;
import fr.cvlaminck.hwweather.data.model.CityEntity;
import fr.cvlaminck.hwweather.data.model.CityExternalIdEntity;
import fr.cvlaminck.hwweather.data.repositories.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class CityRepositoryExtensionImpl
    implements CityRepositoryExtension {

    @Autowired
    private MongoOperations mongoOperations;

    public CityEntity findByExternalIdAndModifyOrCreate(CityExternalIdEntity id, CityEntity city) {
        Query query = new Query();
        query.addCriteria(Criteria.where("externalIds").is(id));

        Update update = new Update();
        update.addToSet("externalIds", id);
        update.set("location", city.getLocation());

        WriteResult writeResult = mongoOperations.upsert(query, update, CityEntity.class);
        return mongoOperations.findById(writeResult.getUpsertedId(), CityEntity.class);
    }

}
