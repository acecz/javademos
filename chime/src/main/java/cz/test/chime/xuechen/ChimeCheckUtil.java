package cz.test.chime.xuechen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChimeCheckUtil {
	public static boolean validate(String filePath, String... features) throws IOException {
		ChimeFeature feature = new ChimeFeature(features);
		List<String> origRawLines = readOrigData(filePath);
		List<EnergyPoint> sampleEps = parseData(origRawLines, feature.baseFrequency());
		testPrintData(sampleEps);
		List<EnergyPoint> filteredEps = filterData(sampleEps);
		testPrintData(filteredEps);
		return feature.validateData(filteredEps);
	}

	private static void testPrintData(List<EnergyPoint> sampleEps) {
		System.out.println("  time    freq   energy   db   intval");
		for (int i = 1; i < sampleEps.size(); i++) {
			EnergyPoint e = sampleEps.get(i);
			System.out.printf("%6.2f\t%7.2f\t%10.7f\t%10.7f\t%8.5f\n", e.time, e.freq, e.energy, e.db,
					e.time - sampleEps.get(i - 1).time);
		}

	}

	private static List<EnergyPoint> filterData(List<EnergyPoint> sampleEps) {
		List<EnergyPoint> filteredEps = new ArrayList<>();
		for (int i = 1; i < sampleEps.size() - 1; i++) {
			double preEng = sampleEps.get(i - 1).energy;
			double eng = sampleEps.get(i).energy;
			double nextEng = sampleEps.get(i + 1).energy;
			if (eng > preEng && eng > nextEng) {
				filteredEps.add(sampleEps.get(i));
			}
		}
		if (filteredEps.size() <= 1) {
			return filteredEps;
		}
		for (int i = 1; i < filteredEps.size(); i++) {
			EnergyPoint e = filteredEps.get(i);
			e.setInterval(e.time - filteredEps.get(i - 1).time);
		}
		return filteredEps;
	}

	/**
	 * just check the give frenquency EnergyPoint,TODO FIX later
	 */
	private static List<EnergyPoint> parseData(List<String> origRawLines, int baseFreq) {
		if (origRawLines == null || origRawLines.isEmpty()) {
			throw new RuntimeException("no data to parse!");
		}
		List<EnergyPoint> sampleEps = new ArrayList<>();
		long cnt = 0;
		double sumTime = 0;
		double sumFreq = 0;
		double sumEnergy = 0;
		double allEnergy = 0;
		for (String rawLine : origRawLines) {
			if (rawLine == null || rawLine.trim().length() == 0) {
				continue;
			}
			// hard code, source data split by tab
			String[] rawArr = rawLine.trim().split("\t");
			Double freq = Double.parseDouble(rawArr[1]);
			if (freq.intValue() != baseFreq) {
				continue;
			}
			cnt++;
			sumTime += Double.parseDouble(rawArr[0]);
			sumFreq += freq;
			double energy = Double.parseDouble(rawArr[2]);
			sumEnergy += Double.parseDouble(rawArr[2]);
			allEnergy += energy;
			if (cnt % 10 == 0) {
				sampleEps.add(new EnergyPoint(sumTime / 10, sumFreq / 10, sumEnergy / 10));
				sumTime = 0;
				sumFreq = 0;
				sumEnergy = 0;
			}
		}
		double halfEnergy = 0;
		if (cnt > 0) {
			halfEnergy = allEnergy / (2 * cnt);
		}
		List<EnergyPoint> rtns = new ArrayList<>();
		for (EnergyPoint ep : sampleEps) {
			if (ep.energy >= halfEnergy) {
				rtns.add(ep);
			}
		}
		return rtns;
	}

	private static List<String> readOrigData(String filePath) throws IOException {
		return Files.lines(Paths.get("/Users/cz/dev/tmp", "3dft.dat")).collect(Collectors.toList());
	}

	public static void main(String[] args) throws IOException {
		String[] features = { "INTERVAL400LV81GV81", "FREQUENCY750" };
		System.out.println(ChimeCheckUtil.validate("/Users/cz/dev/tmp/3dft.dat", features));
	}
}

class ChimeFeature {
	private static final String lessKey = "LV";
	private static final String greatKey = "GV";
	private static final String preTimes = "TIMES";
	private static final String preInterval = "INTERVAL";
	private static final String preFrequency = "FREQUENCY";
	private static final String preEnergy = "ENERGY";
	private final String[] features;
	private boolean checkTimes = false;
	private boolean checkInterval = false;
	private boolean checkFrequency = false;
	private boolean checkEnergy = false;
	private int[] expTimes = { 0, 0, 0 };
	private double[] expInterval = { 0, 0, 0 };
	private int[] expFrequency = { 0, 0, 0 };
	private double[] expEnergy = { 0, 0, 0 };

	public int baseFrequency() {
		return expFrequency[1];
	}

	public ChimeFeature(String[] features) {
		this.features = features;
		parseFeature();
	}

	public boolean validateData(List<EnergyPoint> filteredEps) {
		if (checkTimes) {
			int times = filteredEps.size();
			if (expTimes[0] <= times && times <= expTimes[1])
				return expTimes[0] <= filteredEps.size() && filteredEps.size() <= expTimes[2];
		}
		// ignore the first one interval
		if (!checkSigle(filteredEps.remove(0), true)) {
			return false;
		}
		for (EnergyPoint ep : filteredEps) {
			if (!checkSigle(ep, false)) {
				return false;
			}
		}
		return true;
	}

	private boolean checkSigle(EnergyPoint ep, boolean ignoreInterval) {
		if (checkInterval && !ignoreInterval) {
			double v = ep.getInterval();
			boolean b = expInterval[0] <= v && v <= expInterval[2];
			if (!b) {
				return b;
			}
		}
		if (checkFrequency) {
			double v = ep.freq;
			boolean b = expFrequency[0] <= v && v <= expFrequency[2];
			if (!b) {
				return b;
			}
		}
		if (checkEnergy) {
			double v = ep.energy;
			boolean b = expEnergy[0] <= v && v <= expEnergy[2];
			if (!b) {
				return b;
			}
		}
		return true;
	}

	private void parseFeature() {
		if (features == null || features.length == 0) {
			return;
		}
		for (String f : features) {
			if (f == null || f.trim().isEmpty()) {
				continue;
			}
			f = f.trim().toUpperCase();
			if (f.startsWith(preTimes)) {
				checkTimes = true;
				expTimes = parseIntThreshold(f.replace(preTimes, ""));
			}
			if (f.startsWith(preInterval)) {
				checkInterval = true;
				expInterval = parseDoubleThreshold(f.replace(preInterval, ""));
				// ms to second
				expInterval = new double[] { expInterval[0] / 1000d, expInterval[1] / 1000d, expInterval[2] / 1000d };
			}
			if (f.startsWith(preFrequency)) {
				checkFrequency = true;
				expFrequency = parseIntThreshold(f.replace(preFrequency, ""));
			}
			if (f.startsWith(preEnergy)) {
				checkEnergy = true;
				expEnergy = parseDoubleThreshold(f.replace(preEnergy, ""));
			}
		}
	}

	private double[] parseDoubleThreshold(String feature) {
		boolean hasGv = feature.contains(greatKey);
		boolean hasLv = feature.contains(lessKey);
		String[] fvs = feature.split(greatKey + "|" + lessKey);
		double baseval = Integer.parseInt(fvs[0]);
		if (!hasGv && !hasLv) {
			return new double[] { baseval, baseval, baseval };
		} else if (hasGv && hasLv) {
			double vv1 = Double.parseDouble(fvs[1]);
			double vv2 = Double.parseDouble(fvs[2]);
			if (feature.indexOf(lessKey) < feature.indexOf(greatKey)) {
				return new double[] { baseval - vv1, baseval, baseval + vv2 };
			} else {
				return new double[] { baseval - vv2, baseval, baseval + vv1 };
			}
		} else if (hasGv && !hasLv) {
			double vv1 = Double.parseDouble(fvs[1]);
			return new double[] { baseval, baseval, baseval + vv1 };
		} else if (!hasGv && hasLv) {
			double vv1 = Double.parseDouble(fvs[1]);
			return new double[] { baseval - vv1, baseval, baseval };
		}
		return null;
	}

	private int[] parseIntThreshold(String feature) {
		boolean hasGv = feature.contains(greatKey);
		boolean hasLv = feature.contains(lessKey);
		String[] fvs = feature.split(greatKey + "|" + lessKey);
		int baseval = Integer.parseInt(fvs[0]);
		if (!hasGv && !hasLv) {
			return new int[] { baseval, baseval, baseval };
		} else if (hasGv && hasLv) {
			int vv1 = Integer.parseInt(fvs[1]);
			int vv2 = Integer.parseInt(fvs[2]);
			if (feature.indexOf(lessKey) < feature.indexOf(greatKey)) {
				return new int[] { baseval - vv1, baseval, baseval + vv2 };
			} else {
				return new int[] { baseval - vv2, baseval, baseval + vv1 };
			}
		} else if (hasGv && !hasLv) {
			int vv1 = Integer.parseInt(fvs[1]);
			return new int[] { baseval, baseval, baseval + vv1 };
		} else if (!hasGv && hasLv) {
			int vv1 = Integer.parseInt(fvs[1]);
			return new int[] { baseval - vv1, baseval, baseval };
		}
		return null;
	}
}