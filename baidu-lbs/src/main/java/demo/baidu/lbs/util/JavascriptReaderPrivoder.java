package demo.baidu.lbs.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import demo.baidu.lbs.pojo.PathInfo;

@Provider
@Consumes({ "application/javascript", "text/javascript", "text/javascript; charset=utf-8" })
public class JavascriptReaderPrivoder implements MessageBodyReader<Object> {
	private static final Set<String> jsTypes = new HashSet<>(
Arrays.asList("javascript"));

	private static final Set<String> asJsonClzs = new HashSet<>(Arrays.asList(PathInfo.class.getName()));

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		if (jsTypes.contains(mediaType.getSubtype())) {
			return true;
		}
		return false;
	}

	@Override
	public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
					throws IOException, WebApplicationException {
		try {
			String clzName = type.getName();
			if (asJsonClzs.contains(clzName)) {
				ObjectMapper mapper = JsonUtil.getMapper();
				JsonParser jp = mapper.getJsonFactory().createJsonParser(entityStream);
				jp.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
				return mapper.readValue(jp, mapper.constructType(genericType));
			}
		} finally {
			if (entityStream != null) {
				try {
					entityStream.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
		String msg = "just handle these class for baidu api:" + asJsonClzs;
		throw new IOException(msg);
	}

}
