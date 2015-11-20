package fr.cvlaminck.hwweather.client.utils;

public final class HwWeatherHttpHeaders {
    /**
     * Headers send by the client to the server.
     * Contains a MD5 hash of the response schema.
     * If the header is missing or if client server do not match the server one,
     * the server will provide the schema into the X-Avro-Schema header.
     */
    public final static String CLIENT_AVRO_SCHEMA_HASH = "X-Client-Avro-Schema";

    /**
     * Headers send by the server to the client.
     * Contains the schema of the response.
     * The schema is gzipped and encoded in base64.
     */
    public final static String AVRO_SCHEMA = "X-Avro-Schema";

    private HwWeatherHttpHeaders() {}
}
