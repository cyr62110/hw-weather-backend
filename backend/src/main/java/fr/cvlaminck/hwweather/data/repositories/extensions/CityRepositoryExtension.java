package fr.cvlaminck.hwweather.data.repositories.extensions;

import fr.cvlaminck.hwweather.data.model.CityEntity;
import fr.cvlaminck.hwweather.data.model.CityExternalIdEntity;

public interface CityRepositoryExtension {
    public CityEntity findByExternalIdAndModifyOrCreate(CityExternalIdEntity id, CityEntity city);
}
