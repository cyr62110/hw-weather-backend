package fr.cvlaminck.hwweather.front.messageconverters;

import fr.cvlaminck.hwweather.client.utils.HwWeatherAvroSchemaHelper;
import fr.cvlaminck.hwweather.client.utils.HwWeatherHttpHeaders;
import fr.cvlaminck.hwweather.data.model.city.CityEntity;
import fr.cvlaminck.hwweather.front.utils.AvroMimeTypes;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.*;
import org.springframework.util.Base64Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class AvroHttpMessageConverter
        implements HttpMessageConverter<GenericContainer> {
    private final static Logger log = Logger.getLogger(AvroHttpMessageConverter.class);

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return false; //FIXME: Implements reading of avro
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        if (mediaType == null || MediaType.ALL.equals(mediaType)) {
            return true;
        }
        for (MediaType supportedMediaType : getSupportedMediaTypes()) {
            if (supportedMediaType.isCompatibleWith(mediaType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Arrays.asList(
                AvroMimeTypes.JSON_AVRO,
                AvroMimeTypes.BINARY_AVRO);
    }

    @Override
    public GenericContainer read(Class<? extends GenericContainer> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return null;
    }

    @Override
    public void write(GenericContainer genericContainer, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        HttpHeaders headers = outputMessage.getHeaders();

        if (headers.getContentType() == null) {
            if (contentType == null || contentType.isWildcardType() || contentType.isWildcardSubtype()) {
                contentType = getDefaultContentType();
            }
            if (contentType != null) {
                headers.setContentType(contentType);
            }
        }

        // First, we get the schema of the data we are going to write in the response
        Schema schema = genericContainer.getSchema();

        if (shouldIncludeAvroSchemaInResponse(schema, headers)) {
            writeAvroSchemaInResponse(schema, headers);
        }

        // Write to a byte array so we can get the size and put it in the Content-Length
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Encoder encoder =  getEncoder(schema, contentType, bos);

        SpecificDatumWriter datumWriter = new SpecificDatumWriter(schema);
        datumWriter.write(genericContainer, encoder);

        encoder.flush();
        bos.close();

        if (headers.getContentLength() == -1) {
            headers.setContentLength(bos.size());
        }

        // Recopy the content into the HTTP output stream.
        IOUtils.copy(new ByteArrayInputStream(bos.toByteArray()), outputMessage.getBody());
    }

    private MediaType getDefaultContentType() {
        return AvroMimeTypes.JSON_AVRO;
    }

    private boolean shouldIncludeAvroSchemaInResponse(Schema schema, HttpHeaders headers) {
        // We use the ClientAvroSchemaDetectorFilter to copy the request header in the response
        List<String> clientSchemaHeaderValues = headers.get(HwWeatherHttpHeaders.CLIENT_AVRO_SCHEMA_HASH);
        if (clientSchemaHeaderValues == null || clientSchemaHeaderValues.isEmpty()) {
            return true;
        }

        try {
            String clientSchemaHash = clientSchemaHeaderValues.get(0);
            String serverSchemaHash = HwWeatherAvroSchemaHelper.getHash(schema);
            return !serverSchemaHash.equals(clientSchemaHash);
        } catch (NumberFormatException ex) {
            // If the header is malformed, we react as if it was not send
            return true;
        }
    }

    private void writeAvroSchemaInResponse(Schema schema, HttpHeaders headers) throws IOException {
        String encodedSchema = HwWeatherAvroSchemaHelper.encodeSchema(schema);
        headers.set(HwWeatherHttpHeaders.AVRO_SCHEMA, encodedSchema);
    }

    private Encoder getEncoder(Schema schema, MediaType contentType, OutputStream outputStream) throws IOException {
        Encoder encoder = null;
        if (AvroMimeTypes.BINARY_AVRO.equals(contentType)) {
            //TODO: Create a pool of encoder to reuse older encoder instead of creating new ones?
            encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
        } else if (AvroMimeTypes.JSON_AVRO.equals(contentType)) {
            encoder = EncoderFactory.get().jsonEncoder(schema, outputStream, false);
        }
        if (encoder == null) {
            throw new IllegalStateException("Cannot create encode for mime type '"+contentType+"'");
        }
        return  encoder;
    }
}
