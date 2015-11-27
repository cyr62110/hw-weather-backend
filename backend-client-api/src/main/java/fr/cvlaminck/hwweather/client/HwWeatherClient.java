package fr.cvlaminck.hwweather.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.cvlaminck.builders.uri.Uri;
import fr.cvlaminck.hwweather.client.schema.HwWeatherAvroSchemaStore;

import java.util.concurrent.ExecutorService;

public class HwWeatherClient {

    private Uri baseUri;
    private ObjectMapper objectMapper;
    private ExecutorService executorService;

    private HwWeatherAvroSchemaStore schemaStore;

    private HwWeatherCityRequests cityRequests = new HwWeatherCityRequests(this);
    private HwWeatherWeatherRequests weatherRequests = new HwWeatherWeatherRequests(this);

    public HwWeatherCityRequests cities() {
        return cityRequests;
    }

    public HwWeatherWeatherRequests weather() {
        return weatherRequests;
    }

    /* package */ Uri getBaseUri() {
        return baseUri;
    }

    /* package */ void setBaseUri(Uri baseUri) {
        this.baseUri = baseUri;
    }

    /* package */ ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /* package */ void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /* package */ ExecutorService getExecutorService() {
        return executorService;
    }

    /* package */ void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    /* package */ HwWeatherAvroSchemaStore getSchemaStore() {
        return schemaStore;
    }

    /* package */ void setSchemaStore(HwWeatherAvroSchemaStore schemaStore) {
        this.schemaStore = schemaStore;
    }
}
