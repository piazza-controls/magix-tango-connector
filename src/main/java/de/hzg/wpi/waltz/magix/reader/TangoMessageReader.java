package de.hzg.wpi.waltz.magix.reader;

import de.hzg.wpi.waltz.magix.client.Message;
import de.hzg.wpi.waltz.magix.connector.TangoPayload;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 07.07.2020
 */
@Provider
public class TangoMessageReader implements MessageBodyReader<Message<TangoPayload>> {


    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        throw new UnsupportedOperationException("This method is not supported in " + this.getClass());
    }

    @Override
    public Message<TangoPayload> readFrom(Class<Message<TangoPayload>> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        throw new UnsupportedOperationException("This method is not supported in " + this.getClass());
    }
}
