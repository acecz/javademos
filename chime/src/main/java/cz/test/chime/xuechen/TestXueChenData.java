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
		System.out.println("  time    freq   energy");
		sampleEps.stream().filter(e -> e.energy > 2).forEach(e -> {
			System.out.println(String.format("%6.2f\t%7.2f\t%6.2f", e.time, e.freq, e.energy));
		});
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
			cnt++;
			String[] rawArr = rawLine.trim().split("\t");
			sumTime += Double.parseDouble(rawArr[0]);
			sumFreq += Double.parseDouble(rawArr[1]);
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
		return Files.lines(Paths.get("C:/Users/cz", "3dft.dat")).collect(Collectors.toList());
	}
}

class EnergyPoint {
	public final double time;
	public final double freq;
	public final double energy;

	public EnergyPoint(double time, double freq, double energy) {
		super();
		this.time = time;
		this.freq = freq;
		this.energy = energy;
	}

	@Override
	public String toString() {
		return String.format("%16.2f\t%16.2f\t%16.2f\n", time, freq, energy);
	}

}