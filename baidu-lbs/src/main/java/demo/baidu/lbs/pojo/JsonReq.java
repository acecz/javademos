package demo.baidu.lbs.pojo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class JsonReq<S> implements Serializable {

	private static final long serialVersionUID = 1L;

	public static enum ReqMethod {
		GET, PUT, POST, DELETE
	}

	private String appUrl = "";

	private String urlPath = "";

	private ReqMethod reqMethod = ReqMethod.GET;

	private S jsonObj = null;

	private Map<String, Object> headerMap = new HashMap<String, Object>();

	private Map<String, String> pathParamMap = new HashMap<String, String>();

	private boolean haveRsp = true;

	public JsonReq(String appUrl, String urlPath, ReqMethod reqMethod) {
		super();
		this.appUrl = appUrl;
		this.urlPath = urlPath;
		this.reqMethod = reqMethod;
	}

	public JsonReq(String urlPath, ReqMethod reqMethod) {
		super();
		this.urlPath = urlPath;
		this.reqMethod = reqMethod;
	}

	public JsonReq() {
		// TODO Auto-generated constructor stub
	}

	public String getAppUrl() {
		return appUrl;
	}

	public void setAppUrl(String appUrl) {
		this.appUrl = appUrl;
	}

	public String getUrlPath() {
		return urlPath;
	}

	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}

	public ReqMethod getReqMethod() {
		return reqMethod;
	}

	public void setReqMethod(ReqMethod reqMethod) {
		this.reqMethod = reqMethod;
	}

	public S getJsonObj() {
		return jsonObj;
	}

	public void setJsonObj(S jsonObj) {
		this.jsonObj = jsonObj;
	}

	public Map<String, Object> getHeaderMap() {
		return headerMap;
	}

	public void addHeaderMap(String key, Object val) {
		this.headerMap.put(key, val);
	}

	public Map<String, String> getPathParamMap() {
		return pathParamMap;
	}

	public void addPathParamMap(String key, String val) {
		this.pathParamMap.put(key, val);
	}

	public boolean isHaveRsp() {
		return haveRsp;
	}

	public void setHaveRsp(boolean haveRsp) {
		this.haveRsp = haveRsp;
	}

}
