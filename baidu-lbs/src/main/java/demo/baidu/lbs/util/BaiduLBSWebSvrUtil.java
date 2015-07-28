package demo.baidu.lbs.util;

import java.util.Map;

import demo.baidu.lbs.pojo.DrivingSpecQueryReq;
import demo.baidu.lbs.pojo.JsonReq;
import demo.baidu.lbs.pojo.JsonReq.ReqMethod;
import demo.baidu.lbs.pojo.PathInfo;

public class BaiduLBSWebSvrUtil {
	private static final String lbsApiDirecUrl = "http://api.map.baidu.com/direction/v1";
	public static PathInfo findPathDrivingSpec(DrivingSpecQueryReq queryReq) {
		JsonReq<String> req = new JsonReq<String>(lbsApiDirecUrl, "", ReqMethod.GET);
		fillQueryParams(req, queryReq.queryParams());
		return Jersey1ClientUtil.sendJsonReq(req, PathInfo.class);
	}

	private static void fillQueryParams(JsonReq<String> req, Map<String, String> map) {
		if (map == null) {
			return;
		}
		for (String key : map.keySet()) {
			req.addPathParamMap(key, map.get(key));
		}
		// map.forEach((k, v) -> {
		// req.addPathParamMap(k, v);
		// });
	}

}
