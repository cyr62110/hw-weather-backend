package fr.cvlaminck.hwweather.data.repositories.impl;

import fr.cvlaminck.hwweather.data.model.FreeCallCountersEntity;

import java.util.Collection;

public interface FreeCallCountersRepositoryCustom {
    FreeCallCountersEntity findFreeCallsLeftForToday();

    FreeCallCountersEntity decrement(Collection<String> counters);
}
