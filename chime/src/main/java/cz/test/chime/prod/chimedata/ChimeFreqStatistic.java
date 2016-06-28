package cz.test.chime.prod.chimedata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

public class ChimeFreqStatistic {
	public static final ChimeFreqStatistic EMPTY_CHIME_FREQ_STATISTIC = new ChimeFreqStatistic();
	private final static Logger log = Logger.getLogger(ChimeFreqStatistic.class);
	public final float freq;
	private final Map<Integer, Double> timeEnergyMap;
	private List<EnergyPoint> filteredEps = new ArrayList<>();
	private List<Double> chimeLenRec = new ArrayList<>();
	private int chimeTimes = 1;
	private double avgInteral = 0;
	private double relativeSumEnergy = 0;
	private double relativeAvgEnegy = 0;

	public ChimeFreqStatistic(float freq, Map<Integer, Double> timeEnergyMap) {
		super();
		this.freq = freq;
		this.timeEnergyMap = timeEnergyMap;
		analyse();
	}

	private ChimeFreqStatistic() {
		this.freq = 0;
		this.timeEnergyMap = null;
	}

	private void analyse() {
		if (timeEnergyMap == null || timeEnergyMap.isEmpty()) {
			log.warn("none data to be analysed,passby.freqency=" + freq);
			return;
		}
		List<EnergyPoint> sampleEps = new ArrayList<>();
		long cnt = 0;
		double tmpMaxEnergy = 0;
		int tmpMaxEnergyTime = 0;
		double allEnergy = 0;
		for (Entry<Integer, Double> te : timeEnergyMap.entrySet()) {
			cnt++;
			double energy = te.getValue();
			allEnergy += energy;
			if (energy > tmpMaxEnergy) {
				tmpMaxEnergyTime = te.getKey();
				tmpMaxEnergy = energy;
			}
			// check the max energy for every 4*16ms section
			if (cnt % 4 == 0 && tmpMaxEnergy > 0) {
				sampleEps.add(new EnergyPoint(tmpMaxEnergyTime, freq, tmpMaxEnergy));
				tmpMaxEnergyTime = 0;
				tmpMaxEnergy = 0;
			}
		}
		// last loop
		if (cnt % 8 > 0 && tmpMaxEnergy > 0) {
			sampleEps.add(new EnergyPoint(tmpMaxEnergyTime, freq, tmpMaxEnergy));
			tmpMaxEnergyTime = 0;
			tmpMaxEnergy = 0;
		}
		// if (Float.valueOf(freq).intValue() == 750 ||
		// Float.valueOf(freq).intValue() == 2000) {
		// ChimeCheckUtil.writeLogFile(ChimeCheckUtil.fileDir + "/" + freq +
		// "-mid.csv", sampleEps);
		// }
		double engThreshold = 1000000; // unreachable threshold for empty list
		if (cnt > 0) {
			// hard code,the energy be saved must high the average energy
			engThreshold = allEnergy / cnt;
		}

		filterData(sampleEps, engThreshold);
		analyseGongLength(engThreshold);
	}

	private void analyseGongLength(double engThreshold) {
		boolean gongStart = false;
		double windowTime = 0;
		for (Entry<Integer, Double> te : timeEnergyMap.entrySet()) {
			boolean meetTh = te.getValue() >= engThreshold;
			windowTime = Double.valueOf(te.getKey());
			if (meetTh && !gongStart) {
				chimeLenRec.add(windowTime);
				gongStart = true;
			}
			if (!meetTh && gongStart) {
				chimeLenRec.add(windowTime);
				gongStart = false;
			}
		}
		// for the last un-ended gong
		if (chimeLenRec.size() > 0 && chimeLenRec.size() % 2 == 1) {
			chimeLenRec.add(windowTime);
		}
	}

	private void filterData(List<EnergyPoint> sampleEps, double engThreshold) {
		List<EnergyPoint> preFilterList = new ArrayList<>();
		int cnt = 0;
		for (EnergyPoint ep : sampleEps) {
			if (ep.energy >= engThreshold) {
				cnt++;
				relativeSumEnergy += ep.energy;
				preFilterList.add(ep);
			} else {
				// technical method : make all low energy to 0 to pass by the
				// follow comparison
				preFilterList.add(new EnergyPoint(ep.time, ep.freq, 0));
			}
		}
		relativeAvgEnegy = relativeSumEnergy / cnt;
		if (!preFilterList.isEmpty()) {
			// adjust,if the first or last point's energy great than
			// 0.75*average energy,see it as a reserved point
			preFilterList.add(0, new EnergyPoint(0, 0, 0));
			preFilterList.add(new EnergyPoint(0, 0, 0));
		}

		for (int i = 1; i < preFilterList.size() - 1; i++) {
			double preEng = preFilterList.get(i - 1).energy;
			double eng = preFilterList.get(i).energy;
			double nextEng = preFilterList.get(i + 1).energy;
			if (eng > preEng && eng > nextEng) {
				filteredEps.add(preFilterList.get(i));
			}
		}
		chimeTimes = filteredEps.size();
		if (filteredEps.size() <= 1) {
			return;
		}
		int allInteral = 0;
		for (int i = 1; i < filteredEps.size(); i++) {
			EnergyPoint e = filteredEps.get(i);
			double interval = e.time - filteredEps.get(i - 1).time;
			allInteral += interval;
			e.setInterval(interval);
		}
		avgInteral = allInteral / (chimeTimes - 1);
	}

	public List<EnergyPoint> getFilteredEps() {
		return filteredEps;
	}

	public void setFilteredEps(List<EnergyPoint> filteredEps) {
		this.filteredEps = filteredEps;
	}

	public int getChimeTimes() {
		return chimeTimes;
	}

	public double getAvgInteral() {
		return avgInteral;
	}

	public double getRelativeSumEnergy() {
		return relativeSumEnergy;
	}

	public double getRelativeAvgEnegy() {
		return relativeAvgEnegy;
	}

	public String showGongLength() {
		double sumLen = 0;
		StringBuilder db = new StringBuilder("Every Gong Length Data:\n");
		if (chimeLenRec.isEmpty()) {
			return db.append("NO DATA!").toString();
		}
		for (int i = 0; i < chimeLenRec.size(); i += 2) {
			double from = chimeLenRec.get(i);
			double to = chimeLenRec.get(i + 1);
			double last = to - from;
			db.append(String.format("%10.2f ~ %10.2f last:%f\n", from, to, last));
			sumLen += last;
		}
		db.append("average length:").append(sumLen / (chimeLenRec.size() / 2));
		return db.toString();
	}
}
