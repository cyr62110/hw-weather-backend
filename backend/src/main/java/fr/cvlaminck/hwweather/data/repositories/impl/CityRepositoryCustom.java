package fr.cvlaminck.hwweather.data.repositories.impl;

import fr.cvlaminck.hwweather.data.model.CityEntity;
import fr.cvlaminck.hwweather.data.model.CityExternalIdEntity;

public interface CityRepositoryCustom {
    public CityEntity findByExternalIdAndUpdateOrElseCreate(CityExternalIdEntity id, CityEntity city);
}
