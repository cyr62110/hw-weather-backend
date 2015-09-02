package fr.cvlaminck.hwweather.data.repositories;

import com.mongodb.WriteResult;
import fr.cvlaminck.hwweather.core.external.providers.weather.WeatherDataProvider;
import fr.cvlaminck.hwweather.data.model.FreeCallCountersEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Map;

@Repository
public class FreeCallCounterRepository {

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private Collection<WeatherDataProvider> weatherDataProviders;

    public FreeCallCountersEntity findByDay(LocalDate day) {

        mongoOperations.executeCommand("")
    }

    public Map<String, Integer> getFreeCallsLeftForProviders() {
        LocalDate today = LocalDate.now(ZoneId.of("UTC"));

        Query query = Query.query(Criteria.where("day").is(today));

        //If counters does not exists for today, when inserting we wants to initialize the counters
        //to the number of free calls we can do in a day.
        Update update = new Update();
        for (WeatherDataProvider provider : weatherDataProviders) {
            update.setOnInsert(provider.getProviderName(), provider.getNumberOfFreeOperationPerDay());
        }

        WriteResult writeResult = mongoOperations.upsert(query, update, FreeCallCountersEntity.class);
        //FIXME get from db after upsert
        return null;
    }

}
