package fr.cvlaminck.hwweather.data.repositories.impl;

import com.mongodb.WriteResult;
import fr.cvlaminck.hwweather.data.model.CityEntity;
import fr.cvlaminck.hwweather.data.model.CityExternalIdEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class CityRepositoryImpl
    implements CityRepositoryCustom {

    @Autowired
    private MongoOperations mongoOperations;

    public CityEntity findByExternalIdAndUpdateOrElseCreate(CityExternalIdEntity id, CityEntity city) {
        Query query = new Query();
        query.addCriteria(Criteria.where("externalIds").is(id));

        Update update = new Update();
        update.addToSet("externalIds") //FIXME Field named 'externalIds' has a non-array type Object in the document INVALID-MUTABLE-ELEMENT
                .value(id);
        update.set("location", city.getLocation());

        WriteResult writeResult = mongoOperations.upsert(query, update, CityEntity.class);
        return mongoOperations.findById(writeResult.getUpsertedId(), CityEntity.class);
    }

}
