package fr.cvlaminck.hwweather.data.repositories.impl;

import com.mongodb.WriteResult;
import fr.cvlaminck.hwweather.data.model.city.CityEntity;
import fr.cvlaminck.hwweather.data.model.city.CityExternalIdEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Map;

public class CityRepositoryImpl
    implements CityRepositoryCustom {

    @Autowired
    private MongoOperations mongoOperations;

    public CityEntity findByExternalIdAndUpdateOrElseCreate(CityExternalIdEntity id, CityEntity city) {
        Query query = new Query();
        Criteria elemMatchCriteria = Criteria
                .where("externalId").is(id.getExternalId())
                .and("dataProvider").is(id.getDataProvider());
        query.addCriteria(Criteria.where("externalIds").elemMatch(elemMatchCriteria));

        Update update = new Update();
        update.addToSet("externalIds").value(id);
        update.set("location", city.getLocation());
        for (Map.Entry<String, CityEntity.InternationalizedInformation> entry : city.getInternationalizedInformation().entrySet()) {
            update.set("i18nInformation." + entry.getKey(), entry.getValue());
        }

        WriteResult writeResult = mongoOperations.upsert(query, update, CityEntity.class);
        return mongoOperations.findOne(query, CityEntity.class);
    }

}
