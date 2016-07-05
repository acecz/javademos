package cz.test.shazam.data;

public interface AudioCheckCfg {
	// energy->dB:0.001->16.990, 0.002->20.000, 0.004->23.010, 0.008->26.021,
	// 0.016->29.031, 0.032->32.041, 0.064->35.051, 0.128->38.062,
	// 0.256->41.072, 0.512->44.082, 1.024->47.093, 2.048->50.103,
	// 4.096->53.113, 8.192->56.124, 16.384->59.134, 32.768->62.144,
	// 65.536->65.154, 131.072->68.165
	public static final double ENERGY_THRESHOLD_MIN = 1;
	public static final int HASH_FREQ_ENERGY_PAIR_CNT = 4;
	public static final int ENERGY_RATIO_MAGNIFICATION = 18;
}
