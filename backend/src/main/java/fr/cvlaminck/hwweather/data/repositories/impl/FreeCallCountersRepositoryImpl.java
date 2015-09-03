package fr.cvlaminck.hwweather.data.repositories.impl;

import com.mongodb.WriteResult;
import fr.cvlaminck.hwweather.core.external.providers.weather.WeatherDataProvider;
import fr.cvlaminck.hwweather.data.model.FreeCallCountersEntity;
import fr.cvlaminck.hwweather.data.repositories.FreeCallCountersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;

public class FreeCallCountersRepositoryImpl
        implements FreeCallCountersRepositoryCustom {

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private FreeCallCountersRepository freeCallCountersRepository;

    @Autowired
    private Collection<WeatherDataProvider> weatherDataProviders;

    @Override
    public FreeCallCountersEntity findFreeCallsLeftForToday() {
        LocalDate today = LocalDate.now(ZoneId.of("UTC"));

        Query query = Query.query(Criteria.where("day").is(today));

        //If counters does not exists for today, when inserting we wants to initialize the counters
        //to the number of free calls we can do in a day.
        Update update = new Update();
        update.setOnInsert("day", today);
        for (WeatherDataProvider provider : weatherDataProviders) {
            update.setOnInsert("counters." + provider.getProviderName(), provider.getNumberOfFreeOperationPerDay());
        }

        WriteResult writeResult = mongoOperations.upsert(query, update, FreeCallCountersEntity.class);
        return freeCallCountersRepository.findOneByDay(today);
    }
}
