package demo.baidu.lbs.pojo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import demo.baidu.lbs.cfg.WebCfg;

public class DrivingSpecQueryReq implements Serializable {
	private static final long serialVersionUID = 1L;
	private BasePointInfo origin;
	private BasePointInfo destination;
	private String mode = "driving"; // driving,walking,transit
	private String output = "json"; // xml or json
	// 10 no express way,11 minimum time,12 shortest distance
	private String tactics = "11";

	public DrivingSpecQueryReq(BasePointInfo origin, BasePointInfo destination) {
		super();
		this.origin = origin;
		this.destination = destination;
	}

	public BasePointInfo getOrigin() {
		return origin;
	}

	public void setOrigin(BasePointInfo origin) {
		this.origin = origin;
	}

	public BasePointInfo getDestination() {
		return destination;
	}

	public void setDestination(BasePointInfo destination) {
		this.destination = destination;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getTactics() {
		return tactics;
	}

	public void setTactics(String tactics) {
		this.tactics = tactics;
	}

	public Map<String, String> queryParams() {
		Map<String, String> map = new HashMap<>();
		// according to baidu api doc
		map.put("origin", origin.queryStr());
		map.put("origin_region", origin.getRegion());
		map.put("destination", destination.queryStr());
		map.put("destination_region", origin.getRegion());
		map.put("mode", mode);
		map.put("output", output);
		map.put("tactics", tactics);
		map.put("ak", WebCfg.baiduAk());
		return map;
	}
}
