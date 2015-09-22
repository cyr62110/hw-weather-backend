package fr.cvlaminck.hwweather.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.cvlaminck.builders.uri.Uri;
import fr.cvlaminck.hwweather.client.requests.HwWeatherRequest;

import java.util.concurrent.ExecutorService;

public class HwWeatherClient {

    private Uri baseUri;
    private ObjectMapper objectMapper;
    private ExecutorService executorService;

    private HwWeatherCityRequests cityRequests = new HwWeatherCityRequests(this);

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
}
