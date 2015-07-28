package demo.baidu.lbs.pojo;

import java.io.Serializable;

import demo.baidu.lbs.util.CommonUtil;

public class BasePointInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String address;
	private String region;
	private Double lat;
	private Double lng;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	public String queryStr(){
		StringBuilder querySb = new StringBuilder();
		if (CommonUtil.isNotBlank(name)) {
			querySb.append(name).append("|");
		}
		if (lat != null) {
			querySb.append(lat).append(",").append(lng);
		}
		return querySb.toString();
	}
}
