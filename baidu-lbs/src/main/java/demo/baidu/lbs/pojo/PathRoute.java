package demo.baidu.lbs.pojo;

import java.util.List;

public class PathRoute {
	private Integer distance;
	private Integer duration;
	private Integer toll;
	private List<PathRouteStep> steps;

	public Integer getDistance() {
		return distance;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}



	public Integer getToll() {
		return toll;
	}

	public void setToll(Integer toll) {
		this.toll = toll;
	}

	public List<PathRouteStep> getSteps() {
		return steps;
	}

	public void setSteps(List<PathRouteStep> steps) {
		this.steps = steps;
	}

}
