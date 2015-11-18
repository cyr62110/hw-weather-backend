package fr.cvlaminck.hwweather.front.messageconverters;

import fr.cvlaminck.hwweather.front.utils.AvroMimeTypes;
import org.apache.avro.generic.GenericContainer;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public class AvroHttpMessageConverter
        implements HttpMessageConverter<GenericContainer> {

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

        //FIXME: include an X-Schema containing the MD5 of the schema so we can detect if the schema has changed

        // Write to a byte array so we can get the size and put it in the Content-Length
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Encoder encoder =  getEncoder(genericContainer, contentType, bos);

        SpecificDatumWriter datumWriter = new SpecificDatumWriter(genericContainer.getClass());
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

    private Encoder getEncoder(GenericContainer genericContainer, MediaType contentType, OutputStream outputStream) throws IOException {
        Encoder encoder = null;
        if (AvroMimeTypes.BINARY_AVRO.equals(contentType)) {
            //TODO: Create a pool of encoder to reuse older encoder instead of creating new ones?
            encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
        } else if (AvroMimeTypes.JSON_AVRO.equals(contentType)) {
            encoder = EncoderFactory.get().jsonEncoder(genericContainer.getSchema(), outputStream, false);
        }
        if (encoder == null) {
            throw new IllegalStateException("Cannot create encode for mime type '"+contentType+"'");
        }
        return  encoder;
    }
}
