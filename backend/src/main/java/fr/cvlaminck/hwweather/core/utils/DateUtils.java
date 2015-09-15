package fr.cvlaminck.hwweather.core.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public final class DateUtils {

    private DateUtils() {}

    public static LocalDate today() {
        return LocalDate.now(ZoneId.of("UTC"));
    }

}
