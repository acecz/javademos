package cz.test.chime.prod;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.apache.log4j.Logger;

import cz.test.chime.prod.chimedata.ChimeFeature;
import cz.test.chime.prod.chimedata.ChimeFreqStatistic;
import cz.test.chime.prod.chimedata.EnergyPoint;

public class ChimeCheckUtil {
	private final static Logger log = Logger.getLogger(ChimeCheckUtil.class);

	public static final String fileDir = "/Users/cz/Desktop/ChimeCheck/All_Case_logs/0_ChimeCheck_16_2000/2E6408F0167400A2/0_ChimeCheck_16_2000";

	public static final String AVG_PREFIX = "Average ";
	public static final String KEY_TIMES = "times";
	public static final String KEY_INTERVAL = "interval";
	public static final String KEY_FREQUENCY = "frequency";
	public static final String KEY_DB = "dB";
	public static final String KEY_ENERGY = "energy";
	public static final String KEY_RESULT = "result";

	public static void main(String[] args) throws Exception {
		String[] future = { "FREQUENCY750" };
		removeOldCsv();
		// System.out.println(validate(new File(fileDir, "3dft.dat"), future));
		System.out.println(validateWav(new File(fileDir, "AudioRecord-2.wav"), future));
	}

	private static void removeOldCsv() {
		File[] csvs = new File(fileDir).listFiles(f -> f.getName().endsWith(".csv"));
		for (File file : csvs) {
			file.delete();
		}

	}

	public static Map<String, String> validate(File file, String... features) throws IOException {
		ChimeFeature feature = new ChimeFeature(features);
		Map<Float, Map<Integer, Double>> freqTimeAmpMap = readOrigData(file);
		writeLogFile(file.getAbsolutePath(), freqTimeAmpMap);
		ChimeFreqStatistic efs = parseData(freqTimeAmpMap);
		writeLogFile(file.getAbsolutePath(), efs.getFilteredEps());
		return feature.validateData(efs);
	}

	public static Map<String, String> validateWav(File file, String... features) throws Exception {
		ChimeFeature feature = new ChimeFeature(features);
		Map<Float, Map<Integer, Double>> freqTimeAmpMap = analysisWav(file);
		writeLogFile(file.getAbsolutePath(), freqTimeAmpMap);
		ChimeFreqStatistic efs = parseData(freqTimeAmpMap);
		writeLogFile(file.getAbsolutePath(), efs.getFilteredEps());
		return feature.validateData(efs);
	}

	public static void writeLogFile(String filePath, List<EnergyPoint> filteredEps) {
		String flie = filePath.substring(0, filePath.lastIndexOf(".")) + "-result.csv";
		FileWriter fw = null;
		try {
			fw = new FileWriter(new File(flie));
			fw.write("frequency(Hz),time,interval(ms),energy,volume(dB)\n");
			for (EnergyPoint ep : filteredEps) {
				fw.write(String.format("%f,%f,%d,%f,%f\n", ep.freq, ep.time,
						Double.valueOf(ep.getInterval()).intValue(), ep.energy, ep.getDB()));
			}
		} catch (IOException e) {
			log.error("write log error!", e);
			// ignore
		} finally {
			try {
				fw.close();
			} catch (Exception e) {
				log.error("close resource error!", e);
			}
		}
	}

	private static void writeLogFile(String filePath, Map<Float, Map<Integer, Double>> freqTimeAmpMap) {
		String flie = filePath.substring(0, filePath.lastIndexOf(".")) + "-orig.csv";
		FileWriter fw = null;
		try {
			fw = new FileWriter(new File(flie));
			for (Float freq : freqTimeAmpMap.keySet()) {
				StringBuilder sb = new StringBuilder();
				sb.append(freq).append(",");
				for (Entry<Integer, Double> kv : freqTimeAmpMap.get(freq).entrySet()) {
					sb.append(kv.getValue()).append(",");
				}
				sb.append("\n");
				fw.write(sb.toString());
			}
			fw.flush();
		} catch (IOException e) {
			log.error("write log error!", e);
			// ignore
		} finally {
			try {
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static Map<Float, Map<Integer, Double>> readOrigData(File file) throws IOException {
		FileReader reader = null;
		BufferedReader br = null;
		Map<Float, Map<Integer, Double>> freqTimeAmpMap = new TreeMap<>();
		try {
			reader = new FileReader(file);
			br = new BufferedReader(reader);
			String str = null;
			while ((str = br.readLine()) != null) {
				if (str == null || str.trim().length() == 0) {
					continue;
				}
				String[] rawArr = str.trim().split("\t");
				double time = Double.parseDouble(rawArr[0]);
				float freq = Float.parseFloat(rawArr[1]);
				double energy = Double.parseDouble(rawArr[2]);
				int timeinms = Double.valueOf(time * 1000).intValue();
				Map<Integer, Double> timeAmpMap = freqTimeAmpMap.get(freq);
				if (timeAmpMap == null) {
					timeAmpMap = new TreeMap<>();
					freqTimeAmpMap.put(freq, timeAmpMap);
				}
				timeAmpMap.put(timeinms, energy);
			}
		} catch (Exception e) {
			log.error("read resource data error!", e);
			throw e;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
					log.error("close resource error!", e);
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
					log.error("close resource error!", e);
				}
			}
		}
		return freqTimeAmpMap;
	}

	private static ChimeFreqStatistic parseData(Map<Float, Map<Integer, Double>> freqTimeAmpMap) {
		double maxRelativeSumEnergy = 0;
		ChimeFreqStatistic maxRelativeSumEnergyStatistics = null;
		for (Float freq : freqTimeAmpMap.keySet()) {
			ChimeFreqStatistic cfs = new ChimeFreqStatistic(freq, freqTimeAmpMap.get(freq));
			if (cfs.getRelativeSumEnergy() > maxRelativeSumEnergy) {
				maxRelativeSumEnergy = cfs.getRelativeSumEnergy();
				maxRelativeSumEnergyStatistics = cfs;
			}
		}
		return maxRelativeSumEnergyStatistics;
	}

	public static Map<Float, Map<Integer, Double>> analysisWav(File wav) throws Exception {
		AudioInputStream ais = AudioSystem.getAudioInputStream(wav);
		Map<Float, Map<Integer, Double>> freqTimeAmpMap = new TreeMap<>();
		try {
			AudioFormat format = ais.getFormat();
			// the divisor of converting from a frame to float [-1,1]
			final float cvtFrameDivisor = 1 << (format.getFrameSize() * 8 - 1);
			// every 16 ms as window
			final int windowSize = 16 * Float.valueOf(format.getSampleRate()).intValue() / 1000;
			Map<Integer, double[]> windowDataMAp = readWav(ais, cvtFrameDivisor, windowSize);
			int sampleRate = Float.valueOf(format.getSampleRate()).intValue();
			int maxHz = sampleRate / 2;
			float freqIntv = format.getSampleRate() / windowSize;

			for (int t = 0; t < windowDataMAp.size(); t++) {
				int time = 16 * t;
				for (float hz = 0; hz < maxHz; hz += freqIntv) {
					Map<Integer, Double> timeAmpMap = freqTimeAmpMap.get(hz);
					if (timeAmpMap == null) {
						timeAmpMap = new TreeMap<>();
						freqTimeAmpMap.put(hz, timeAmpMap);
					}
					GoertzelChimeAnalysis ca = new GoertzelChimeAnalysis(sampleRate, hz, windowDataMAp.get(t));
					timeAmpMap.put(time, ca.calAmpl());
				}
			}
		} finally {
			try {
				if (ais != null) {
					ais.close();
				}
			} catch (Exception e) {
				log.error("close resource error!", e);
			}
		}
		return freqTimeAmpMap;
	}

	private static Map<Integer, double[]> readWav(final AudioInputStream ais, final float cvtFrameDivisor,
			final int windowSize) throws Exception {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final byte[] buff = new byte[windowSize * 2];
		for (int read = 0; (read = ais.read(buff)) != -1;) {
			out.write(buff, 0, read);
		}
		out.close();
		ais.close();
		final byte[] audioBytes = out.toByteArray();
		Map<Integer, double[]> timeDataMap = new TreeMap<>();
		for (int i = 0; i < audioBytes.length / 2; i++) {
			int key = i / windowSize;
			double[] windowData = timeDataMap.get(key);
			if (windowData == null) {
				windowData = new double[windowSize];
				timeDataMap.put(key, windowData);
			}
			// magic number for PCM SIGNED conversion. hard code 2bytes/frame
			windowData[i % windowSize] = ((audioBytes[2 * i] & 0xff) | (audioBytes[2 * i + 1] << 8)) / cvtFrameDivisor;
		}
		return timeDataMap;
	}

	private static class GoertzelChimeAnalysis {
		private float sampling_rate;
		private float target_frequency;
		private int n;
		private double[] testData;
		private double coeff, Q1, Q2;
		private double sine, cosine;

		public GoertzelChimeAnalysis(int sampleRate, float targetFreq, double[] data) {
			sampling_rate = sampleRate;
			target_frequency = targetFreq;
			n = data.length;
			testData = data;
			initGoertzel();
		}

		public void resetGoertzel() {
			Q2 = 0;
			Q1 = 0;
		}

		public void initGoertzel() {
			int k;
			float floatN;
			double omega;
			floatN = (float) n;
			k = (int) (0.5 + ((floatN * target_frequency) / sampling_rate));
			omega = (2.0 * Math.PI * k) / floatN;
			sine = Math.sin(omega);
			cosine = Math.cos(omega);
			coeff = 2.0 * cosine;
			resetGoertzel();
		}

		public void processSample(double sample) {
			double Q0;
			Q0 = coeff * Q1 - Q2 + sample;
			Q2 = Q1;
			Q1 = Q0;
		}

		public double[] getRealImag(double[] parts) {
			parts[0] = (Q1 - Q2 * cosine);
			parts[1] = (Q2 * sine);
			return parts;
		}

		// public double getMagnitudeSquared() {
		// return (Q1 * Q1 + Q2 * Q2 - Q1 * Q2 * coeff);
		// }

		public double calAmpl() {
			int index;
			double magnitudeSquared;
			double magnitude;
			double real;
			double imag;
			double[] parts = new double[2];

			for (index = 0; index < n; index++) {
				processSample(testData[index]);
			}
			parts = getRealImag(parts);
			real = parts[0];
			imag = parts[1];
			magnitudeSquared = real * real + imag * imag;
			magnitude = java.lang.Math.sqrt(magnitudeSquared) / 2;
			resetGoertzel();
			return magnitude;
		}
	}
}
