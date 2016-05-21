package cz.test.chime.xuechen;

public class EnergyPoint {
	public final double time;
	public final double freq;
	public final double energy;
	public final double db;
	private double interval;

	public EnergyPoint(double time, double freq, double energy) {
		super();
		this.time = time;
		this.freq = freq;
		this.energy = energy;
		this.db = eng2db(this.energy);
	}

	private double eng2db(double eng) {
		return 10 * Math.log10(eng * 5E4);
	}

	public double getInterval() {
		return interval;
	}

	public void setInterval(double interval) {
		this.interval = interval;
	}
}