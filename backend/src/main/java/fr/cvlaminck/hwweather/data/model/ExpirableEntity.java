package fr.cvlaminck.hwweather.data.model;

import com.sun.scenario.effect.Offset;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.*;
import java.util.Date;

public abstract class ExpirableEntity {

    /**
     * When the entity has been refreshed for the last time.
     */
    private Date refreshTime;

    private int gracePeriodInSeconds;

    private int expiryInSeconds;

    public boolean isInGracePeriod() {
        return getGracePeriodStartTime().isBefore(OffsetDateTime.now(ZoneId.of("UTC")));
    }

    public boolean isExpired() {
        return getExpiryDate().isBefore(OffsetDateTime.now(ZoneId.of("UTC")));
    }

    public OffsetDateTime getRefreshTime() {
        return refreshTime.toInstant().atOffset(ZoneOffset.UTC);
    }

    public OffsetDateTime getGracePeriodStartTime() {
        return getExpiryDate()
                .minusSeconds(gracePeriodInSeconds);
    }

    public OffsetDateTime getExpiryDate() {
        return getRefreshTime()
                .plusSeconds(expiryInSeconds);
    }
}
