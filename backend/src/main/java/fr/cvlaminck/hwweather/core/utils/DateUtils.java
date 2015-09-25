package fr.cvlaminck.hwweather.core.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public final class DateUtils {

    private DateUtils() {}

    public static ZoneId defaultZone() { return ZoneId.of("UTC"); }

    public static LocalDate today() {
        return LocalDate.now(defaultZone());
    }

    public static LocalDate firstDayOfWeek(LocalDate date) {
        return date.minusDays(date.getDayOfWeek().getValue() - 1);
    }

    public static LocalDate firstDayOfWeek() {
        return firstDayOfWeek(today());
    }

    public static LocalDateTime now() { return LocalDateTime.now(defaultZone()); }

}
