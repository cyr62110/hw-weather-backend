package fr.cvlaminck.hwweather.client.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.cvlaminck.builders.uri.Uri;
import fr.cvlaminck.builders.uri.UriBuilder;
import fr.cvlaminck.hwweather.client.exceptions.HwWeatherClientException;
import fr.cvlaminck.hwweather.client.exceptions.HwWeatherIllegalProtocolException;
import fr.cvlaminck.hwweather.client.exceptions.HwWeatherRequestException;
import fr.cvlaminck.hwweather.client.exceptions.HwWeatherServerException;
import fr.cvlaminck.hwweather.client.reponses.ClientErrorResponse;
import fr.cvlaminck.hwweather.client.utils.HwWeatherAvroMimeTypes;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
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
            byte[] requestContent = getRequestContent();
            if (requestContent != null && requestContent.length > 0) {
                urlConnection.setDoOutput(true);
                writeRequestContent(urlConnection.getOutputStream());
            }

            int responseCode = urlConnection.getResponseCode();
            Map<String, List<String>> responseHeaders = urlConnection.getHeaderFields();
            byte[] responseContent = getResponseContent(urlConnection, responseCode, responseHeaders);

            // FIXME Check the headers: verify that we have received binary avro, etc.

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

    private void appendRequestHeaders(HttpURLConnection urlConnection) {
        // We want to receive only binary avro from the server
        urlConnection.setRequestProperty("Accept", HwWeatherAvroMimeTypes.BINARY_AVRO);

        Map<String, String> additionalRequestHeaders = getAdditionalRequestHeaders();
        if (additionalRequestHeaders != null && !additionalRequestHeaders.isEmpty()) {
            //FIXME Filter headers that are set internally by this Request builder.
            for (Map.Entry<String, String> entry : additionalRequestHeaders.entrySet()) {
                urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
    }

    protected Map<String, String> getAdditionalRequestHeaders() {
        return null;
    }

    protected byte[] getRequestContent() throws HwWeatherIllegalProtocolException, IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        writeRequestContent(bos);
        return bos.toByteArray();
    }

    protected void writeRequestContent(OutputStream outputStream) throws HwWeatherIllegalProtocolException, IOException {

    }

    private byte[] getResponseContent(HttpURLConnection urlConnection, int responseCode, Map<String, List<String>> responseHeaders) throws IOException {
        InputStream is = null;
        try {
            is = urlConnection.getInputStream();
            //TODO: Handle Gzip compression, etc...
            return IOUtils.toByteArray(is);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    protected void handleServerError(URL requestUrl, byte[] requestContent,
                                     int responseCode, byte[] responseContent, Map<String, List<String>> responseHeaders)
            throws HwWeatherServerException {
        throw new HwWeatherServerException(requestUrl, requestContent, responseCode);
    }

    protected void handleClientError(URL requestUrl, byte[] requestContent,
                                     int responseCode, byte[] responseContent, Map<String, List<String>> responseHeaders)
            throws HwWeatherClientException, IOException {
        if (responseContent == null || responseContent.length == 0) {
            throw new HwWeatherClientException(requestUrl, requestContent, responseCode);
        }
        //FIXME ClientErrorResponse response = objectMapper.readValue(responseContent, ClientErrorResponse.class);
        throw new HwWeatherClientException(requestUrl, requestContent, responseCode, /*response.getMessage()*/ null);
    }

    protected T getResponse(byte[] responseContent, Map<String, List<String>> responseHeaders) throws IOException {
        // FIXME Create a poll to reuse decoder
        Decoder decoder = DecoderFactory.get().binaryDecoder(responseContent, null);

        // FIXME Reader and writer schema are the same, should we use a constructor with reader and writer schema and get schema from the response?
        DatumReader<T> reader = new SpecificDatumReader<T>(responseClass);
        return reader.read(null, decoder);
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }
}
