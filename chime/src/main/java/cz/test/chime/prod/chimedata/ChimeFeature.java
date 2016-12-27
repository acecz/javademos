package cz.test.chime.prod.chimedata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import cz.test.chime.prod.ChimeCheckUtil;

public class ChimeFeature {
	private final static Logger log = Logger.getLogger(ChimeFeature.class);
	private static final String lessKey = "LV";
	private static final String greatKey = "GV";
	private static final String preTimes = "TIMES";
	private static final String preInterval = "INTERVAL";
	private static final String preFrequency = "FREQUENCY";
	private static final String preDB = "DB";
	private final String[] features;
	private boolean checkTimes = false;
	private boolean checkInterval = false;
	private boolean checkFrequency = false;
	private boolean checkDB = false;
	private int[] expTimes = { 0, 0, 0 };
	private double[] expInterval = { 0, 0, 0 };
	private double[] expFrequency = { 0, 0, 0 };
	private double[] expDB = { 0, 0, 0 };
	private ChimeFreqStatistic cfs = ChimeFreqStatistic.EMPTY_CHIME_FREQ_STATISTIC;

	public double baseFrequency() {
		return expFrequency[1];
	}

	public ChimeFeature(String[] features) {
		// log.info("Features:" + Arrays.asList(features));
		this.features = features;
		parseFeature();
		// if (log.isDebugEnabled()) {
		// StringBuilder logSb = new
		// StringBuilder("ChimeFeature:checkTimes=").append(checkTimes)
		// .append(",checkInterval=").append(checkInterval).append(",checkFrequency=").append(checkFrequency)
		// .append(",checkEnergy=").append(checkDB).append(",expTimes=").append(Arrays.toString(expTimes))
		// .append(",expInterval=").append(Arrays.toString(expInterval)).append(",expFrequency=")
		// .append(Arrays.toString(expFrequency)).append(",expEnergy=").append(Arrays.toString(expDB));
		// log.debug(logSb.toString());
		// }
	}

	public Map<String, String> validateData(ChimeFreqStatistic cfs) {
		this.cfs = cfs;
		List<EnergyPoint> filteredEps = this.cfs.getFilteredEps();
		Map<String, String> resultMap = new HashMap<>();
		boolean result = true;
		int times = filteredEps.size();
		resultMap.put(ChimeCheckUtil.KEY_TIMES, times + "");
		if (checkTimes) {
			boolean checkTimes = expTimes[0] <= times && times <= expTimes[2];
			result = result && checkTimes;
			if (!checkTimes) {
				log.warn(new StringBuilder().append("checkTimes failed.expected times range[").append(expTimes[0])
						.append(",").append(expTimes[2]).append("] real times:").append(times));
			}
		}
		double sumDB = 0;
		// ignore the first one interval
		EnergyPoint firstEp = filteredEps.remove(0);
		result = result && checkSigle(firstEp, true);
		sumDB += firstEp.getDB();
		for (EnergyPoint ep : filteredEps) {
			result = result && checkSigle(ep, false);
			sumDB += ep.getDB();
		}
		if (times == 1) {
			resultMap.put(ChimeCheckUtil.KEY_INTERVAL, "---");
		} else {
			resultMap.put(ChimeCheckUtil.KEY_INTERVAL, String.format("%.2f", this.cfs.getAvgInteral()) + " MS");
		}
		resultMap.put(ChimeCheckUtil.KEY_INTERVAL, String.format("%.2f", this.cfs.getAvgInteral()) + " MS");
		resultMap.put(ChimeCheckUtil.KEY_FREQUENCY, this.cfs.freq + "Hz");
		resultMap.put(ChimeCheckUtil.KEY_DB, String.format("%.2f", Double.valueOf(sumDB / times)) + " dB");
		resultMap.put(ChimeCheckUtil.KEY_RESULT, result + "");
		return resultMap;
	}

	public Map<String, String> validateData(List<EnergyPoint> filteredEps) {
		Map<String, String> resultMap = new HashMap<>();
		boolean result = true;
		int times = filteredEps.size();
		resultMap.put(ChimeCheckUtil.KEY_TIMES, times + "");
		if (checkTimes) {
			boolean checkTimes = expTimes[0] <= times && times <= expTimes[2];
			result = result && checkTimes;
			if (!checkTimes) {
				log.warn(new StringBuilder().append("checkTimes failed.expected times range[").append(expTimes[0])
						.append(",").append(expTimes[2]).append("] real times:").append(times));
			}
		}
		double sumFreq = 0;
		double sumDB = 0;
		double sumInterval = 0;
		// ignore the first one interval
		EnergyPoint firstEp = filteredEps.remove(0);
		result = result && checkSigle(firstEp, true);
		sumFreq += firstEp.freq;
		sumDB += firstEp.getDB();
		for (EnergyPoint ep : filteredEps) {
			result = result && checkSigle(ep, false);
			sumFreq += ep.freq;
			sumDB += ep.getDB();
			sumInterval += ep.getInterval();
		}

		String intvVal = "---";
		resultMap.put(ChimeCheckUtil.KEY_FREQUENCY, intvVal);
		resultMap.put(ChimeCheckUtil.KEY_DB, intvVal);
		resultMap.put(ChimeCheckUtil.KEY_INTERVAL, intvVal);
		if (times > 1) {
			resultMap.put(ChimeCheckUtil.KEY_INTERVAL,
					Double.valueOf(sumInterval * 1000 / (times - 1)).intValue() + " MS");
			resultMap.put(ChimeCheckUtil.KEY_FREQUENCY, Double.valueOf(sumFreq / times).intValue() + "Hz");
			resultMap.put(ChimeCheckUtil.KEY_DB, Double.valueOf(sumDB / times).intValue() + " dB");
		}
		resultMap.put(ChimeCheckUtil.KEY_RESULT, result + "");
		return resultMap;
	}

	private boolean checkSigle(EnergyPoint ep, boolean ignoreInterval) {
		if (checkInterval && !ignoreInterval) {
			double v = ep.getInterval();
			boolean b = expInterval[0] <= v && v <= expInterval[2];
			if (!b) {
				log.warn(new StringBuilder().append("checkInterval failed.expected Interval range[")
						.append(expInterval[0]).append(",").append(expInterval[2]).append("] real Interval:")
						.append(v));
				return b;
			}
		}
		if (checkFrequency) {
			double v = ep.freq;
			boolean b = expFrequency[0] <= v && v <= expFrequency[2];
			if (!b) {
				log.warn(new StringBuilder().append("checkFrequency failed.expected Frequency range[")
						.append(expFrequency[0]).append(",").append(expFrequency[2]).append("] real Frequency:")
						.append(v));
				return b;
			}
		}
		if (checkDB) {
			double v = ep.getDB();
			boolean b = expDB[0] <= v && v <= expDB[2];
			if (!b) {
				log.warn(new StringBuilder().append("checkEnergy failed.expected Energy range[").append(expDB[0])
						.append(",").append(expDB[2]).append("] real Energy:").append(v));
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
				expInterval = new double[] { expInterval[0], expInterval[1], expInterval[2] };
			}
			if (f.startsWith(preFrequency)) {
				checkFrequency = true;
				expFrequency = parseDoubleThreshold(f.replace(preFrequency, ""));
			}
			if (f.startsWith(preDB)) {
				checkDB = true;
				expDB = parseDoubleThreshold(f.replace(preDB, ""));
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

	public Map<String, String> validateData(ChimeFreqStatisticEh efs) {
		// TODO Auto-generated method stub
		return null;
	}

}
