package cz.test.sounddetect;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DBTest {
	static String testDir = "/Users/cz/workspace/tmp/SoundCheck_log";
	static double dbth = 45.0;

	// static String testDir = "/Users/cz/Desktop/soundaudiosample";
	public static void main(String[] args) throws Exception {
		List<File> allCsvs = new ArrayList<>();
		loadAudioDatas(allCsvs, new File(testDir));
		Map<String, TreeMap<Integer, TreeMap<String, Double>>> powerMap = checkPower(allCsvs);
		writeLogFile(testDir + "/simple.csv", powerMap);
	}

	private static void writeLogFile(String flie, Map<String, TreeMap<Integer, TreeMap<String, Double>>> powerMap) {
		System.out.println(powerMap.size());
		FileWriter fw = null;
		try {
			fw = new FileWriter(new File(flie));
			StringBuilder timeSb = new StringBuilder("time(s),");
			for (int i = 0; i < 1500; i++) {
				timeSb.append(i * 16 / 1000d).append(",");
			}
			timeSb.append("\n");
			fw.write(timeSb.toString());
			for (String key : powerMap.keySet()) {
				TreeMap<Integer, TreeMap<String, Double>> val = powerMap.get(key);
				StringBuilder lineSb = new StringBuilder();
				lineSb.append(key.replace("-orig.csv", ".wav")).append(",");
				int maxCol = val.lastKey();
				for (int i = 1; i < maxCol; i++) {
					TreeMap<String, Double> freqDb = val.get(i);
					double db = 0;
					String freq = "";
					for (String fq : freqDb.keySet()) {
						if (freqDb.get(fq) > db) {
							db = freqDb.get(fq);
							freq = fq;
						}
					}
					if (db == 0) {
						lineSb.append(" ,");
					} else {
						lineSb.append(db).append("(").append(freq).append(")").append(",");
					}

				}
				if (lineSb.lastIndexOf(",") > 0) {
					lineSb.append("\n");
					fw.write(lineSb.toString());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			// ignore
		} finally {
			try {
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static TreeMap<String, TreeMap<Integer, TreeMap<String, Double>>> checkPower(List<File> allCsvs)
			throws Exception {
		System.out.println("allCsvs Size:" + allCsvs.size());
		final TreeMap<String, TreeMap<Integer, TreeMap<String, Double>>> powerMap = new TreeMap<>();
		for (File file : allCsvs) {
			String path = file.getAbsolutePath();
			String rn = path.replace(testDir, "").replaceFirst("/", "");
			final TreeMap<Integer, TreeMap<String, Double>> caseDataMap = new TreeMap<>();
			powerMap.put(rn, caseDataMap);
			Files.lines(file.toPath()).forEach(line -> {
				if (line != null && line.trim().length() > 0) {
					String[] lineRecs = line.split(",");
					String freq = lineRecs[0];
					for (int i = 1; i < lineRecs.length; i++) {
						double db = eng2db(Double.valueOf(lineRecs[i]));
						TreeMap<String, Double> freqDBMap = caseDataMap.get(i);
						if (freqDBMap == null) {
							freqDBMap = new TreeMap<>();
							caseDataMap.put(i, freqDBMap);
						}
						if (db > dbth) {
							freqDBMap.put(freq, db);
						}
					}
				}
			});
		}
		return powerMap;
	}

	private static void loadAudioDatas(List<File> allCsvs, File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				loadAudioDatas(allCsvs, f);
			}
		} else {
			if (file.getName().endsWith("orig.csv")) {
				allCsvs.add(file);
			}
		}
	}

	private static double eng2db(double eng) {
		return 10 * Math.log10(eng * 5E4);
	}

}
