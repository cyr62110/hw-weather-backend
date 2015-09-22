package fr.cvlaminck.hwweather.client.requests;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.cvlaminck.builders.uri.Uri;
import fr.cvlaminck.builders.uri.UriBuilder;
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

public abstract class HwWeatherRequest<T> {

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

    public T execute() throws IOException {
        T response = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = build().toURI().toURL();
            if (proxy == null) {
                urlConnection = (HttpURLConnection) url.openConnection();
            } else {
                urlConnection = (HttpURLConnection) url.openConnection(proxy);
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
                        handleClientError(responseCode, responseContent, responseHeaders);
                        break;
                    case 5:
                        handleServerError(responseCode, responseContent, responseHeaders);
                        break;
                }
            }

        } catch (MalformedURLException e) {
            //Silent since the UriBuilder ensure the validity of the URL.
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return response;
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

    protected void handleServerError(int responseCode, String responseContent, Map<String, List<String>> responseHeaders) {

    }

    protected void handleClientError(int responseCode, String responseContent, Map<String, List<String>> responseHeaders) {

    }

    protected T getResponse(String responseContent, Map<String, List<String>> responseHeaders) throws IOException {
        return objectMapper.readValue(responseContent, responseClass);
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }
}
