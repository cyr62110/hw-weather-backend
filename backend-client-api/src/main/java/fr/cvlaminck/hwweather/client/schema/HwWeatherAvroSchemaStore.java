package fr.cvlaminck.hwweather.client.schema;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;

public interface HwWeatherAvroSchemaStore {

    /**
     * Returns the latest version of the record schema that has been used
     * to generate the provided class.
     */
    <T> Schema getSchemaForClass(Class<T> clazz);

    /**
     * Store the latest version of the record schema that is used by the server.
     */
    <T> void storeSchemaForClass(Class<T> clazz, Schema schema);

}
