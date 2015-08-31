package fr.cvlaminck.hwweather.data.model.weather;

import fr.cvlaminck.hwweather.data.model.ExpirableEntity;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "weather.current")
public class CurrentWeatherEntity
    extends ExpirableEntity {



}
