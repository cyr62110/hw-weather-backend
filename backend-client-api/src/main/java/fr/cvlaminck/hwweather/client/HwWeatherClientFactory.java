package fr.cvlaminck.hwweather.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.cvlaminck.builders.Builder;
import fr.cvlaminck.builders.exception.MalformedUriException;
import fr.cvlaminck.builders.uri.Uri;
import fr.cvlaminck.hwweather.client.schema.HwWeatherAvroSchemaStore;
import fr.cvlaminck.hwweather.client.schema.HwWeatherInMemoryAvroSchemaStore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HwWeatherClientFactory
        implements Builder<HwWeatherClient> {

    private String baseUrl;

    private ObjectMapper objectMapper;

    private ExecutorService executorService;

    private HwWeatherAvroSchemaStore schemaStore;

    public HwWeatherClientFactory baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public HwWeatherClientFactory objectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        return this;
    }

    public HwWeatherClientFactory executorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    public HwWeatherClientFactory schemaStore(HwWeatherAvroSchemaStore schemaStore) {
        this.schemaStore = schemaStore;
        return this;
    }

    @Override
    public HwWeatherClient build() {
        HwWeatherClient client = new HwWeatherClient();
        client.setObjectMapper(getObjectMapper());
        client.setExecutorService(getExecutorService());
        client.setSchemaStore(getSchemaStore());

        try {
            client.setBaseUri(Uri.parse(baseUrl));
        } catch (MalformedUriException ex) {
            throw new IllegalArgumentException("baseUrl is not a valid url.", ex);
        }

        return client;
    }

    private ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = this.objectMapper;
        if (objectMapper == null) {
            objectMapper = new ObjectMapper(); //TODO make default configuration
        }
        return objectMapper;
    }

    public ExecutorService getExecutorService() {
        ExecutorService executorService = this.executorService;
        if (executorService == null) {
            executorService = Executors.newFixedThreadPool(10); //TODO make this number configurable
        }
        return executorService;
    }

    public HwWeatherAvroSchemaStore getSchemaStore() {
        HwWeatherAvroSchemaStore schemaStore = this.schemaStore;
        if (schemaStore == null) {
            schemaStore = new HwWeatherInMemoryAvroSchemaStore();
        }
        return schemaStore;
    }
}
