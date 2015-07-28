package demo.baidu.lbs.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

import demo.baidu.lbs.pojo.JsonReq;

public class Jersey1ClientUtil {
	private static final Logger log = Logger.getLogger(Jersey1ClientUtil.class);

	private static String jsonMt = "application/json";

	private static Client client = null;

	private static Map<String, WebResource> webResourceMap = new ConcurrentHashMap<String, WebResource>();

	public static <S, P> P sendJsonReq(JsonReq<S> req, Class<P> clz) {
		String reqDbgStr = null;
		if (log.isDebugEnabled()) {
			reqDbgStr = JsonUtil.obj2str(req);
			log.debug("REQUEST=" + reqDbgStr);
		}
		String url = req.getAppUrl() + req.getUrlPath();
		P resp = null;
		try {
			WebResource rsc = webResourceMap.get(url);
			if (rsc == null) {
				synchronized (Jersey1ClientUtil.class) {
					if (rsc == null) {
						rsc = getClient().resource(url);
						webResourceMap.put(url, rsc);
					}
				}
			}
			Map<String, String> pathParamMap = req.getPathParamMap();
			for (String key : pathParamMap.keySet()) {
				rsc = rsc.queryParam(key, pathParamMap.get(key));
			}
			Builder builder = rsc.accept(jsonMt).type(jsonMt);
			Map<String, Object> headerMap = req.getHeaderMap();
			for (String key : headerMap.keySet()) {
				builder = builder.header(key, headerMap.get(key));
			}
			boolean hasRsp = req.isHaveRsp();
			switch (req.getReqMethod()) {
			case GET:
				if (hasRsp) {
					resp = builder.get(clz);
				} else {
					builder.get(clz);
				}
				break;
			case PUT:
				if (hasRsp) {
					resp = builder.put(clz, req.getJsonObj());
				} else {
					builder.put(req.getJsonObj());
				}
				break;
			case POST:
				if (hasRsp) {
					resp = builder.post(clz, req.getJsonObj());
				} else {
					builder.post(req.getJsonObj());
				}
				break;
			case DELETE:
				if (hasRsp) {
					resp = builder.delete(clz);
				} else {
					builder.delete();
				}
				break;
			}
			if (log.isDebugEnabled()) {
				log.debug("RESPONSE OF REQUEST" + reqDbgStr + "IS " + JsonUtil.obj2str(resp));
			}
			return resp;
		} catch (Exception e) {
			log.error("sendJsonReq error!" + e.getMessage());
			throw new RuntimeException("sendJsonReq error!", e);
		}
	}

	private static synchronized Client getClient() {
		if (client == null) {
			synchronized (Jersey1ClientUtil.class) {
				if (client == null) {
					ClientConfig cc = new DefaultClientConfig();
					cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
					cc.getClasses().add(JavascriptReaderPrivoder.class);
					client = Client.create(cc);
					int connTimeout = 10000;
					client.setConnectTimeout(connTimeout / 5);
					client.setReadTimeout(connTimeout + connTimeout / 10);
				}
			}
		}
		return client;
	}

}
