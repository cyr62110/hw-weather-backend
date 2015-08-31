package fr.cvlaminck.hwweather.data.repositories.impl;

import fr.cvlaminck.hwweather.data.model.city.CityEntity;
import fr.cvlaminck.hwweather.data.model.city.CityExternalIdEntity;

public interface CityRepositoryCustom {
    public CityEntity findByExternalIdAndUpdateOrElseCreate(CityExternalIdEntity id, CityEntity city);
}
