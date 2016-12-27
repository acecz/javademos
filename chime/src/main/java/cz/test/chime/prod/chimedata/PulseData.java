package cz.test.chime.prod.chimedata;

public class PulseData {
	private int freq;
	private int startTime;
	private int endTime;
	private double maxEnergy;
	private int cadence;

	public int getFreq() {
		return freq;
	}

	public void setFreq(int freq) {
		this.freq = freq;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getEndTime() {
		return endTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	public double getMaxEnergy() {
		return maxEnergy;
	}

	public void setMaxEnergy(double maxEnergy) {
		this.maxEnergy = maxEnergy;
	}

	public int getCadence() {
		return cadence;
	}

	public void setCadence(int cadence) {
		this.cadence = cadence;
	}

	public double getDutyCycle() {
		if (cadence > 0) {
			return (endTime - startTime) * 1D / cadence;
		}
		return 0;
	}

}
