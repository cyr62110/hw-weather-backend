package fr.cvlaminck.hwweather.data.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CityEntity {
    @Id
    private String id;
    @Indexed
    private Collection<CityExternalIdEntity> externalIds = Collections.emptyList();
    @Indexed
    private Point location;
    private Map<String, String> i18nNames = new HashMap<>(); //ISO 639-2 Language code -> name

    public String getName(String languageCode) {
        String name = i18nNames.get(languageCode);
        if (name == null) {
            name = i18nNames.values().iterator().next(); //FIXME: Take US-en if value is missing
        }
        return name;
    }
    public void appendName(String languageCode, String name) {
        i18nNames.put(languageCode, name);
    }

    public double getLongitude() {
        return location.getX();
    }
    public double getLatitude() {
        return location.getY();
    }
    public void setLocation(double longitude, double latitude) {
        this.location = new Point(longitude, latitude);
    }

    public String getId() {
        return id;
    }
}
