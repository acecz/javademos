package cz.test.chime.prod.chimedata;

public class EnergyPoint {

	public final double time;
	public final double freq;
	public final double energy;
	private double interval;

	public EnergyPoint(double time, double freq, double energy) {
		super();
		this.time = time;
		this.freq = freq;
		this.energy = energy;
	}

	public double getInterval() {
		return interval;
	}

	public double getDB() {
		if (energy <= 0) {
			return 0;
		}
		return calcDB(energy);
	}

	public static double calcDB(double energy) {
		// the 5E4 value:refer to
		// http://www.sengpielaudio.com/calculator-soundvalues.htm
		// the reference sound pressure p0 = 20 µPa = 2 × 10−5 Pa.
		return 10 * Math.log10(energy * 5E4);
	}

	public void setInterval(double interval) {
		this.interval = interval;
	}

	public static void main(String[] args) {
		int exp = 1;
		for (double i = 0; i < 200; i = 0.001 * (1 << exp++)) {
			System.out.printf("%8.3f->%.3f,", i, calcDB(i));
		}
	}

}
