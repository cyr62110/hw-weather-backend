package fr.cvlaminck.hwweather.client.utils;

import org.apache.avro.Schema;
import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class HwWeatherAvroSchemaHelper {

    public static <T> Schema getSchemaForClass(Class<T> clazz) {
        try {
            Method getClassSchemaMethod = clazz.getMethod("getClassSchema");
            return (Schema) getClassSchemaMethod.invoke(null);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getHash(Schema schema) {
        Charset charset = Charset.forName("UTF-8");
        if (charset == null) {
            throw new IllegalStateException("UTF-8 encoding is not available on the platform.");
        }

        try {
            byte[] binarySchema = schema.toString(false).getBytes(charset);
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] binaryHash = messageDigest.digest(binarySchema);
            return Hex.encodeHexString(binaryHash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 digest is not available on the platform.", e);
        }
    }

    public static String encodeSchema(Schema schema) {
        Charset charset = Charset.forName("UTF-8");
        if (charset == null) {
            throw new IllegalStateException("UTF-8 encoding is not available on the platform.");
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Base64OutputStream base64OutputStream = null;
        GZIPOutputStream gzipOutputStream = null;
        try {
            base64OutputStream = new Base64OutputStream(bos);
            gzipOutputStream = new GZIPOutputStream(base64OutputStream);

            IOUtils.write(schema.toString(false), gzipOutputStream);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        } finally {
            IOUtils.closeQuietly(gzipOutputStream);
        }

        return new String(bos.toByteArray(), charset);
    }

    public static Schema decodeSchema(String encodedSchema) {
        Charset charset = Charset.forName("UTF-8");
        if (charset == null) {
            throw new IllegalStateException("UTF-8 encoding is not available on the platform.");
        }

        InputStream is = new ByteArrayInputStream(encodedSchema.getBytes(charset));
        GZIPInputStream gzipInputStream = null;
        Base64InputStream base64InputStream = null;
        try {
            base64InputStream = new Base64InputStream(is);
            gzipInputStream = new GZIPInputStream(base64InputStream);

            return new Schema.Parser().parse(gzipInputStream);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            IOUtils.closeQuietly(gzipInputStream);
        }
    }

    private HwWeatherAvroSchemaHelper() {}
}
