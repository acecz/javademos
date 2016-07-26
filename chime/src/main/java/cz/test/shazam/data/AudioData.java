package cz.test.shazam.data;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class AudioData implements AudioCheckCfg {
	// private final static Logger log = Logger.getLogger(AudioData.class);
	public final float freqIntv;
	public final int timeIntv;
	public final double[][] ampls;
	public final long[][] energys;
	public final long[] avgEnergys;
	public final String[] hashs;

	public AudioData(float freqIntv, int timeIntv, double[][] ampls) {
		super();
		this.freqIntv = freqIntv;
		this.timeIntv = timeIntv;
		this.ampls = ampls;
		this.energys = new long[ampls.length][ampls[0].length];
		this.hashs = new String[ampls.length];
		this.avgEnergys = new long[ampls.length];
	}

	public Map<Integer, String> makeFingerprintOfAudio() {
		Map<Integer, String> audioHashs = new TreeMap<>();
		// make a hash for every time window
		for (int i = 0; i < ampls.length; i++) {
			String hash = makeTimeHash(ampls[i]);
			hashs[i] = hash;
			audioHashs.put(i, hash);
		}
		return audioHashs;
	}

	/**
	 * the logic of making hash: use a few biggest {frequency,energy}
	 * pairs,combine them to a byte array:frequencyStep-(int value of
	 * energy/nextPairEnergy)-...,then use Base64 to encode this byte array
	 */
	private String makeTimeHash(double[] ds) {
		TreeMap<Double, Integer> map = new TreeMap<>();
		for (int i = 0; i < ds.length; i++) {
			map.put(ds[i], i);
		}
		ByteBuffer bb = ByteBuffer.allocate(HASH_FREQ_ENERGY_PAIR_CNT * 8);
		Entry<Double, Integer> bigger = map.pollLastEntry();
		Entry<Double, Integer> smaller;
		for (int i = 0; i < HASH_FREQ_ENERGY_PAIR_CNT; i++) {
			if (bigger.getKey() < ENERGY_THRESHOLD_MIN) {
				break;
			}
			smaller = map.pollLastEntry();
			// since dB is a logarithm value,use an ENERGY_RATIO_MAGNIFICATION
			// to increase sensitivity //TODO or use square root ?
			int engRate = Double.valueOf(Math.sqrt(bigger.getKey() / smaller.getKey())).intValue();
			bb.putInt(engRate);
			bb.putInt(engRate);
			bigger = smaller;
		}
		return Base64.getEncoder().encodeToString(bb.array());
	}

	public static double calcDB(double energy) {
		// the 5E4 value:refer to
		// http://www.sengpielaudio.com/calculator-soundvalues.htm
		// the reference sound pressure p0 = 20 µPa = 2 × 10−5 Pa.
		return 10 * Math.log10(energy * 5E4);
	}
}
