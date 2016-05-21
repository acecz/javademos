package cz.test.chime.xuechen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TestXueChenData {
	public static void main(String[] args) throws Exception {
		List<String> origRawLines = readOrigData();
		List<EnergyPoint> sampleEps = parseData(origRawLines);
		// System.out.println(sampleEps.stream().filter(e -> e.energy >
		// 2).collect(Collectors.toList()));
		List<EnergyPoint> filteredEps = new ArrayList<>();
		sampleEps = sampleEps.stream().filter(e -> e.energy > 2).collect(Collectors.toList());
		for (int i = 1; i < sampleEps.size() - 1; i++) {
			double preEng = sampleEps.get(i - 1).energy;
			double eng = sampleEps.get(i).energy;
			double nextEng = sampleEps.get(i + 1).energy;
			if (eng >= preEng && eng >= nextEng) {
				filteredEps.add(sampleEps.get(i));
			}
		}
		System.out.println("\n\n\n***************");
		System.out.println("  time    freq   energy   db   intval");
		for (int i = 1; i < filteredEps.size(); i++) {
			EnergyPoint e = filteredEps.get(i);
			System.out.printf("%6.2f\t%7.2f\t%6.2f\t%3.5f\n", e.time, e.freq, e.energy, e.db,
					e.time - filteredEps.get(i - 1).time);
		}
		System.out.println(filteredEps.size());
	}

	private static List<EnergyPoint> parseData(List<String> origRawLines) {
		if (origRawLines == null || origRawLines.isEmpty()) {
			throw new RuntimeException("none data to parse!");
		}
		List<EnergyPoint> sampleEps = new ArrayList<>();
		long cnt = 0;
		double sumTime = 0;
		double sumFreq = 0;
		double sumEnergy = 0;
		for (String rawLine : origRawLines) {
			if (rawLine == null || rawLine.trim().length() == 0) {
				continue;
			}
			String[] rawArr = rawLine.trim().split("\t");
			Double freq = Double.parseDouble(rawArr[1]);
			if (freq.intValue() != 750) {
				continue;
			}
			cnt++;
			sumTime += Double.parseDouble(rawArr[0]);
			sumFreq += freq;
			sumEnergy += Double.parseDouble(rawArr[2]);
			if (cnt % 10 == 0) {
				sampleEps.add(new EnergyPoint(sumTime / 10, sumFreq / 10, sumEnergy / 10));
				sumTime = 0;
				sumFreq = 0;
				sumEnergy = 0;
			}
		}
		return sampleEps;
	}

	private static List<String> readOrigData() throws IOException {
		return Files.lines(Paths.get("/Users/cz/dev/tmp", "3dft.dat")).collect(Collectors.toList());
	}
}
