package demo.baidu.lbs.util;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

public class JsonUtil {
	private static final Logger log = Logger.getLogger(JsonUtil.class);

	private static ObjectMapper mapper = null;

	public static String obj2str(Object obj) {
		if (obj == null) {
			return null;
		}
		try {
			return getMapper().writeValueAsString(obj);
		} catch (Exception e) {
			log.error(" ObjectMapper.writeValueAsString Error! ", e);
			// ignore
		}
		return null;
	}

	public static <T> T str2obj(String json, Class<T> clz) {
		if (json == null) {
			return null;
		}
		try {
			return getMapper().readValue(json, clz);
		} catch (Exception e) {
			log.error(" ObjectMapper.readValue Error! json=" + json + ",clazz=" + clz.getName(), e);
			// ignore
		}
		return null;
	}

	private static ObjectMapper getMapper() {
		if (mapper == null) {
			synchronized (JsonUtil.class) {
				if (mapper == null) {
					mapper = new ObjectMapper();
					mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					// TODO other configurations
				}
			}
		}
		return mapper;
	}
}
