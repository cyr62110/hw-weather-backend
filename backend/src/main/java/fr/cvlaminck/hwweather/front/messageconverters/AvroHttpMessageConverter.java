package fr.cvlaminck.hwweather.front.messageconverters;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class AvroHttpMessageConverter
        extends AbstractHttpMessageConverter<GenericContainer> {

    public AvroHttpMessageConverter() {
        super(MediaType.parseMediaType("application/avro+binary"));
    }

    @Override
    protected boolean supports(Class clazz) {
        return GenericContainer.class.isAssignableFrom(clazz);
    }

    @Override
    protected GenericContainer readInternal(Class<? extends GenericContainer> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return null;
    }

    @Override
    protected void writeInternal(GenericContainer genericContainer, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        HttpHeaders headers = outputMessage.getHeaders();

        //FIXME: include an X-Schema containing the MD5 of the schema so we can detect if the schema has changed

        Encoder encoder =  getEncoder(outputMessage);

        SpecificDatumWriter datumWriter = new SpecificDatumWriter(genericContainer.getClass());
        datumWriter.write(genericContainer, encoder);

        encoder.flush();
    }

    private Encoder getEncoder(HttpOutputMessage outputMessage) throws IOException {
        OutputStream os = outputMessage.getBody();
        //TODO: Create a pool of encoder to reuse older encoder instead of creating new ones?
        Encoder encoder = EncoderFactory.get().binaryEncoder(os, null);
        return  encoder;
    }
}
