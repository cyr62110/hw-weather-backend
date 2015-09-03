package fr.cvlaminck.hwweather.data.repositories.impl;

import fr.cvlaminck.hwweather.data.model.FreeCallCountersEntity;

public interface FreeCallCountersRepositoryCustom {
    FreeCallCountersEntity findFreeCallsLeftForToday();
}
