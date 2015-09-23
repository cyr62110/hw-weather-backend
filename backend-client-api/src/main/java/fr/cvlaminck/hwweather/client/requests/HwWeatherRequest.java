package fr.cvlaminck.hwweather.client.requests;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.cvlaminck.builders.uri.Uri;
import fr.cvlaminck.builders.uri.UriBuilder;
import fr.cvlaminck.hwweather.client.exceptions.HwWeatherClientException;
import fr.cvlaminck.hwweather.client.exceptions.HwWeatherIllegalProtocolException;
import fr.cvlaminck.hwweather.client.exceptions.HwWeatherRequestException;
import fr.cvlaminck.hwweather.client.exceptions.HwWeatherServerException;
import fr.cvlaminck.hwweather.client.reponses.ClientErrorResponse;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public abstract class HwWeatherRequest<T>
    implements Callable<T> {

    private Uri baseUri;
    private Class<T> responseClass;
    private Proxy proxy;

    private ObjectMapper objectMapper;

    protected HwWeatherRequest(Uri baseUri, ObjectMapper objectMapper, Class<T> responseClass) {
        this.baseUri = baseUri;
        this.objectMapper = objectMapper;
        this.responseClass = responseClass;
    }

    public UriBuilder getBaseUriBuilder() {
        return baseUri.buildUpon();
    }

    public abstract Uri build();

    @Override
    public T call() throws HwWeatherRequestException, IOException {
        T response = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = build().toURI().toURL();
            if (proxy == null) {
                urlConnection = (HttpURLConnection) url.openConnection();
            } else {
                urlConnection = (HttpURLConnection) url.openConnection(proxy);
            }

            urlConnection.setRequestMethod(getRequestMethod());
            String requestContent = getRequestContent();
            if (requestContent != null && !requestContent.isEmpty()) {
                urlConnection.setDoOutput(true);
                IOUtils.write(requestContent, urlConnection.getOutputStream());
            }

            int responseCode = urlConnection.getResponseCode();
            Map<String, List<String>> responseHeaders = urlConnection.getHeaderFields();
            String responseContent = getResponseContent(urlConnection, responseCode, responseHeaders);

            if (responseCode == 200) {
                response = getResponse(responseContent, responseHeaders);
            } else {
                switch (responseCode / 100) {
                    case 1:
                    case 2:
                    case 3:
                        //TODO
                        break;
                    case 4:
                        handleClientError(url, requestContent, responseCode, responseContent, responseHeaders);
                        break;
                    case 5:
                        handleServerError(url, requestContent, responseCode, responseContent, responseHeaders);
                        break;
                }
            }

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return response;
    }

    protected String getRequestMethod() {
        return "GET";
    }

    protected String getRequestContent() throws HwWeatherIllegalProtocolException {
        return null;
    }

    private String getResponseContent(HttpURLConnection urlConnection, int responseCode, Map<String, List<String>> responseHeaders) throws IOException {
        InputStream is = null;
        try {
            is = urlConnection.getInputStream();
            //TODO: Handle Gzip compression, etc...
            return IOUtils.toString(is, Charset.forName("UTF-8"));
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    protected void handleServerError(URL requestUrl, String requestContent,
                                     int responseCode, String responseContent, Map<String, List<String>> responseHeaders)
        throws HwWeatherServerException {
        throw new HwWeatherServerException(requestUrl, requestContent, responseCode);
    }

    protected void handleClientError(URL requestUrl, String requestContent,
                                     int responseCode, String responseContent, Map<String, List<String>> responseHeaders)
            throws HwWeatherClientException, IOException {
        if (responseContent == null || responseContent.isEmpty()) {
            throw new HwWeatherClientException(requestUrl, requestContent, responseCode);
        }
        ClientErrorResponse response = objectMapper.readValue(responseContent, ClientErrorResponse.class);
        throw new HwWeatherClientException(requestUrl, requestContent, responseCode, response.getMessage());
    }

    protected T getResponse(String responseContent, Map<String, List<String>> responseHeaders) throws IOException {
        return objectMapper.readValue(responseContent, responseClass);
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }
}
