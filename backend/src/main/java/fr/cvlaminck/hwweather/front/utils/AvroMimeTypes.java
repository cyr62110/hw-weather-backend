package fr.cvlaminck.hwweather.front.utils;

import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;

public final class AvroMimeTypes {
    public final static String BINARY_AVRO_MIME = "application/avro+binary";
    public final static String JSON_AVRO_MIME = "application/avro+json";

    public final static MediaType BINARY_AVRO = MediaType.parseMediaType(BINARY_AVRO_MIME);
    public final static MediaType JSON_AVRO = MediaType.parseMediaType(JSON_AVRO_MIME);

    public static final String[] PRODUCES = {
            JSON_AVRO_MIME,
            BINARY_AVRO_MIME
    };

    private AvroMimeTypes() {}
}
