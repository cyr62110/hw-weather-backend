package fr.cvlaminck.hwweather.data.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.*;

public class CityEntity {
    @Id
    private String id;
    @Indexed
    private Set<CityExternalIdEntity> externalIds = new HashSet<>();
    @Indexed
    private Point location;
    private Map<String, InternationalizedInformation> i18nInformation = new HashMap<>(); //ISO 639-2 Language code -> name

    public InternationalizedInformation getInternationalizedInformation(String languageCode) {
        InternationalizedInformation info = i18nInformation.get(languageCode);
        if (info == null) {
            info = i18nInformation.values().iterator().next(); //FIXME: Take US-en if value is missing
        }
        return info;
    }
    public void addInternationalizedInformation(String languageCode, InternationalizedInformation info) {
        i18nInformation.put(languageCode, info);
    }

    public void addExternalId(CityExternalIdEntity externalId) {
        externalIds.add(externalId);
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
    public Point getLocation() {
        return location;
    }

    public String getId() {
        return id;
    }

    public static class InternationalizedInformation {
        private String name;
        private String country;

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getCountry() {
            return country;
        }
        public void setCountry(String country) {
            this.country = country;
        }
    }
}
