package cz.test.chime.prod.chimedata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

public class ChimeFreqStatisticEh {
	public static final ChimeFreqStatisticEh EMPTY_CHIME_FREQ_STATISTIC = new ChimeFreqStatisticEh();
	private final static Logger log = Logger.getLogger(ChimeFreqStatisticEh.class);
	public final float freq;
	private final Map<Integer, Double> timeEnergyMap;
	private List<PulseData> pulseList = new ArrayList<>();

	private int pulseCnt = 1;
	private double avgCadence = 0;
	private double avgDutyCycle = 0;
	private double sumEnergy = 0;

	public ChimeFreqStatisticEh(float freq, Map<Integer, Double> timeEnergyMap) {
		super();
		this.freq = freq;
		this.timeEnergyMap = timeEnergyMap;
		analyse();
	}

	private ChimeFreqStatisticEh() {
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
		double allEnergy = 0;
		for (Entry<Integer, Double> te : timeEnergyMap.entrySet()) {
			cnt++;
			double energy = te.getValue();
			allEnergy += energy;
			sampleEps.add(new EnergyPoint(te.getKey(), freq, te.getValue()));
		}
		double engThreshold = 1000000; // unreachable threshold for empty list
		if (cnt > 0) {
			// hard code,the energy be saved must high the average energy
			engThreshold = allEnergy / cnt;
		}
		groupData(sampleEps, engThreshold);
	}

	private void groupData(List<EnergyPoint> sampleEps, double engThreshold) {
		List<List<EnergyPoint>> groupedEps = new ArrayList<>();
		List<EnergyPoint> epGroup = new ArrayList<>();
		for (EnergyPoint ep : sampleEps) {
			if (ep.energy > engThreshold) {
				sumEnergy += ep.energy;
				epGroup.add(ep);
			} else {
				if (!epGroup.isEmpty()) {
					groupedEps.add(epGroup);
					epGroup = new ArrayList<>();
				}
			}
		}
		if (!epGroup.isEmpty()) {
			groupedEps.add(epGroup);
		}

		pulseCnt = groupedEps.size();
		int prestartTime = 0;
		double allCadence = 0;
		double allDutyCycle = 0;
		for (List<EnergyPoint> epg : groupedEps) {
			PulseData pd = new PulseData();
			double maxEnergy = 0;
			for (EnergyPoint ep : epg) {
				maxEnergy = maxEnergy < ep.energy ? ep.energy : maxEnergy;
			}
			pd.setMaxEnergy(maxEnergy);
			pd.setStartTime((int) epg.get(0).time);
			pd.setEndTime((int) epg.get(epg.size() - 1).time);
			if (prestartTime > 0) {
				pd.setCadence(pd.getStartTime() - prestartTime);
			}
			allDutyCycle += (pd.getEndTime() - pd.getStartTime());
			prestartTime = pd.getStartTime();
			pd.setFreq((int) epg.get(0).freq);
			allCadence += pd.getCadence();
			pulseList.add(pd);
		}
		if (pulseCnt > 1) {
			avgCadence = allCadence / (pulseCnt - 1);
			avgDutyCycle = allDutyCycle / (allCadence + avgCadence);
		} else {
			PulseData siglePd = pulseList.get(0);
			avgCadence = siglePd.getEndTime() - siglePd.getStartTime();
		}
	}

	public int getPulseCnt() {
		return pulseCnt;
	}

	public double getAvgInteral() {
		return avgCadence;
	}

	public List<PulseData> getPulseList() {
		return pulseList;
	}

	public double getSumEnergy() {
		return sumEnergy;
	}

	public double getAvgDutyCycle() {
		return avgDutyCycle;
	}

	public String dbgStr() {
		return String.format("AVG-DATA: pulseCnt=%d,avgCadence=%f,avgDutyCycle=%f", pulseCnt, avgCadence, avgDutyCycle);
	}

}
