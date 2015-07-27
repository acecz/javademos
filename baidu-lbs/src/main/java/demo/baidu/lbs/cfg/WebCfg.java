package demo.baidu.lbs.cfg;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import demo.baidu.lbs.web.filter.AuthFilter;

public class WebCfg implements AppConst {
	private static final Logger log = Logger.getLogger(AuthFilter.class);
	Properties prop = null;
	private static WebCfg instance;
	private WebCfg() {
	}

	private static WebCfg getInstance() {
		if (instance == null) {
			synchronized (WebCfg.class) {
				Properties prop = null;
				if (instance == null) {
					instance = new WebCfg();
					InputStream inputStream = null;
					String propFileName = "webapp.properties";
					try {
						prop = new Properties();
						inputStream = WebCfg.class.getClassLoader().getResourceAsStream(propFileName);
						prop.load(inputStream);
						inputStream.close();
					} catch (Exception e) {
						throw new RuntimeException("read property file '" + propFileName + "'error!", e);
					}
				}
				log.info("WEB-APP-CFG:" + prop);
			}
		}
		return instance;
	}

	public static Map<String, String> webCfg() {
		final Map<String, String> rtnMap = new HashMap<>();
		Properties prop = getInstance().prop;
		if (prop == null) {
			return rtnMap;
		}
		prop.keySet().forEach(k -> {
			rtnMap.put(k.toString(), prop.getOrDefault(k, "").toString());
		});
		return rtnMap;
	}

}
