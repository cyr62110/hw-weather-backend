package fr.cvlaminck.hwweather.client.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.cvlaminck.builders.uri.Uri;
import fr.cvlaminck.builders.uri.UriBuilder;
import fr.cvlaminck.hwweather.client.exceptions.HwWeatherClientException;
import fr.cvlaminck.hwweather.client.exceptions.HwWeatherIllegalProtocolException;
import fr.cvlaminck.hwweather.client.exceptions.HwWeatherRequestException;
import fr.cvlaminck.hwweather.client.exceptions.HwWeatherServerException;
import fr.cvlaminck.hwweather.client.protocol.ClientErrorResponse;
import fr.cvlaminck.hwweather.client.schema.HwWeatherAvroSchemaStore;
import fr.cvlaminck.hwweather.client.utils.HwWeatherAvroMimeTypes;
import fr.cvlaminck.hwweather.client.utils.HwWeatherAvroSchemaHelper;
import fr.cvlaminck.hwweather.client.utils.HwWeatherHttpHeaders;
import org.apache.avro.Schema;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.zip.GZIPInputStream;

public abstract class HwWeatherRequest<T>
        implements Callable<T> {

    private Uri baseUri;
    private Class<T> responseClass;
    private Proxy proxy;

    private HwWeatherAvroSchemaStore schemaStore;

    protected HwWeatherRequest(Uri baseUri, HwWeatherAvroSchemaStore schemaStore, Class<T> responseClass) {
        this.baseUri = baseUri;
        this.schemaStore = schemaStore;
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
            Schema expectedResponseSchema = getExpectedResponseSchema();

            if (proxy == null) {
                urlConnection = (HttpURLConnection) url.openConnection();
            } else {
                urlConnection = (HttpURLConnection) url.openConnection(proxy);
            }

            urlConnection.setRequestMethod(getRequestMethod());
            appendRequestHeaders(urlConnection, expectedResponseSchema);
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
                Schema serverSchema = getResponseSchema(expectedResponseSchema, responseHeaders);
                response = getResponse(responseClass, serverSchema, responseContent, responseHeaders);
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

    private Schema getExpectedResponseSchema() {
        return schemaStore.getSchemaForClass(responseClass);
    }

    private void appendRequestHeaders(HttpURLConnection urlConnection, Schema expectedResponseSchema) {
        // We want to receive only binary avro compressed with Gzip from the server
        urlConnection.setRequestProperty("Accept", HwWeatherAvroMimeTypes.BINARY_AVRO);
        urlConnection.setRequestProperty("Accept-Encoding", "gzip");

        // We append the hash of the schema we are using, the server will not have to send us the schema if it not has been updated
        String expectedResponseSchemaHash = HwWeatherAvroSchemaHelper.getHash(expectedResponseSchema);
        urlConnection.setRequestProperty(HwWeatherHttpHeaders.CLIENT_AVRO_SCHEMA_HASH, expectedResponseSchemaHash);

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
            if (isResponseContentGzipped(responseHeaders)) {
                is = new GZIPInputStream(is);
            }
            return IOUtils.toByteArray(is);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    private boolean isResponseContentGzipped(Map<String, List<String>> responseHeaders) {
        List<String> contentEncodingHeaders = responseHeaders.get("Content-Encoding");
        if (contentEncodingHeaders == null || contentEncodingHeaders.isEmpty()) {
            return false;
        }
        return "gzip".equals(contentEncodingHeaders.get(0));
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

        Schema serverSchema = getResponseSchema(null, responseHeaders);
        ClientErrorResponse response = getResponse(ClientErrorResponse.class, serverSchema, responseContent, responseHeaders);
        throw new HwWeatherClientException(requestUrl, requestContent, responseCode, response.getMessage());
    }

    private Schema getResponseSchema(Schema expectedResponseSchema, Map<String, List<String>> responseHeaders) {
        List<String> xSchemaHeaders = responseHeaders.get(HwWeatherHttpHeaders.AVRO_SCHEMA);
        if ((xSchemaHeaders == null || xSchemaHeaders.isEmpty()) && expectedResponseSchema != null) {
            return expectedResponseSchema;
        } else {
            Schema schema = HwWeatherAvroSchemaHelper.decodeSchema(xSchemaHeaders.get(0));
            return schema;
        }
    }

    protected <T> T getResponse(Class<T> responseClass, Schema serverSchema, byte[] responseContent, Map<String, List<String>> responseHeaders) throws IOException {
        // FIXME Create a poll to reuse decoder
        Decoder decoder = DecoderFactory.get().binaryDecoder(responseContent, null);

        Schema readerSchema = HwWeatherAvroSchemaHelper.getSchemaForClass(responseClass);
        DatumReader<T> reader = new SpecificDatumReader<T>(readerSchema, serverSchema);
        return reader.read(null, decoder);
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }
}
