package fr.cvlaminck.hwweather.data.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Map;

@Document(collection = "freeCallCounters")
public class FreeCallCountersEntity {

    private String id;
    private LocalDate day;
    private Map<String, Integer> counters;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getDay() {
        return day;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }

    public Map<String, Integer> getCounters() {
        return counters;
    }

    public void setCounters(Map<String, Integer> counters) {
        this.counters = counters;
    }
}
