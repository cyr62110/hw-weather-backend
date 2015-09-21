package fr.cvlaminck.hwweather.data.model;

import fr.cvlaminck.hwweather.core.utils.DateUtils;

import java.time.*;

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
        this.refreshTime = DateUtils.now();
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
        return getExpiryDate().isBefore(DateUtils.now());
    }

    public boolean isExpiredOrInGracePeriod() {
        return getGracePeriodStartDate().isBefore(DateUtils.now());
    }

    public LocalDateTime getRefreshTime() {
        return refreshTime;
    }

    public LocalDateTime getGracePeriodStartDate() {
        return getRefreshTime()
                .plusSeconds(expiryInSeconds);
    }

    public LocalDateTime getExpiryDate() {
        return getRefreshTime()
                .plusSeconds(expiryInSeconds)
                .plusSeconds(gracePeriodInSeconds);
    }
}
