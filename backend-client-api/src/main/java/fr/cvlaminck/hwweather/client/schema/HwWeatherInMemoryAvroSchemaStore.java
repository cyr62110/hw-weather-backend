package fr.cvlaminck.hwweather.client.schema;

import fr.cvlaminck.hwweather.client.utils.HwWeatherAvroSchemaHelper;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of the {@link HwWeatherAvroSchemaStore}.
 * This implementation stores the schema retrieved from the server in-memory and
 * has no persistence.
 */
public class HwWeatherInMemoryAvroSchemaStore
    implements HwWeatherAvroSchemaStore {

    private Map<Class, Schema> storedSchemas = new HashMap<>();

    @Override
    public <T> Schema getSchemaForClass(Class<T> clazz) {
        Schema storedSchema = storedSchemas.get(clazz);
        if (storedSchema == null) {
            storedSchema = HwWeatherAvroSchemaHelper.getSchemaForClass(clazz);
            if (storedSchema == null) {
                throw new IllegalStateException("Provided class has not been generated using an avro schema.");
            }
            storedSchemas.put(clazz, storedSchema);
        }
        return storedSchema;
    }

    @Override
    public <T> void storeSchemaForClass(Class<T> clazz, Schema schema) {
        storedSchemas.put(clazz, schema);
    }
}
