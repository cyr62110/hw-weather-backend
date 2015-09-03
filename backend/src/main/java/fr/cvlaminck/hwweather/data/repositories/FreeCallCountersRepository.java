package fr.cvlaminck.hwweather.data.repositories;

import fr.cvlaminck.hwweather.data.model.FreeCallCountersEntity;
import fr.cvlaminck.hwweather.data.repositories.impl.FreeCallCountersRepositoryCustom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;

public interface FreeCallCountersRepository
    extends MongoRepository<FreeCallCountersEntity, String>, FreeCallCountersRepositoryCustom {

    @Query("{day: ?0}")
    public FreeCallCountersEntity findOneByDay(LocalDate day);

}
