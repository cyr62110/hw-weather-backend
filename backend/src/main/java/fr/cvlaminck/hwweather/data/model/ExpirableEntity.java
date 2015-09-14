package fr.cvlaminck.hwweather.data.model;

import com.sun.scenario.effect.Offset;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.*;
import java.util.Date;

public abstract class ExpirableEntity {

    /**
     * When the entity has been refreshed for the last time.
     */
    private LocalDateTime refreshTime;

    private int gracePeriodInSeconds;

    private int expiryInSeconds;

    protected ExpirableEntity() {
    }

    protected ExpirableEntity(int expiryInSeconds, int gracePeriodInSeconds) {
        this.refreshTime = LocalDateTime.now(ZoneId.of("UTC"));
        this.gracePeriodInSeconds = gracePeriodInSeconds;
        this.expiryInSeconds = expiryInSeconds;
    }

    public boolean isInGracePeriod() {
        if (isExpired()) {
            return false;
        }
        return isExpiredOrInGracePeriod();
    }

    public boolean isExpired() {
        return getExpiryDate().isBefore(OffsetDateTime.now(ZoneId.of("UTC")));
    }

    public boolean isExpiredOrInGracePeriod() {
        return getGracePeriodStartTime().isBefore(OffsetDateTime.now(ZoneId.of("UTC")));
    }

    public OffsetDateTime getRefreshTime() {
        return refreshTime.atOffset(ZoneOffset.UTC);
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
