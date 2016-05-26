package cz.test.chime.myfft;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class ChimeAnalysisUtil {
	// magic number for PCM SIGNED conversion
	private static final double cvsNum = 32768.0;
	// every window has 256 frames( every frame 2 bytes),equals 16ms
	private static final int windowSize = 256;

	public static void analysis(String filePath) throws Exception {
		File file = new File(filePath);
		AudioInputStream ais = AudioSystem.getAudioInputStream(file);
		AudioFormat format = ais.getFormat();
		System.out.println(format);
		Map<Integer, double[]> windowDataMAp = readWav(ais);
		int sampleRate = Float.valueOf(format.getSampleRate()).intValue();
		int maxHz = sampleRate / 2;
		float freqIntv = format.getSampleRate() / windowSize;
		Map<Float, Map<Integer, Double>> freqTimeAmpMap = new TreeMap<>();
		for (int t = 0; t < windowDataMAp.size(); t++) {
			int time = 16 * t;
			for (float hz = 0; hz < maxHz; hz += freqIntv) {
				Map<Integer, Double> timeAmpMap = freqTimeAmpMap.get(hz);
				if (timeAmpMap == null) {
					timeAmpMap = new TreeMap<>();
					freqTimeAmpMap.put(hz, timeAmpMap);
				}
				ChimeAnalysis ca = new ChimeAnalysis(sampleRate, hz, windowDataMAp.get(t));
				timeAmpMap.put(time, ca.calAmpl());
			}
		}
		writeLogFile(filePath, freqTimeAmpMap);
	}

	private static void writeLogFile(String filePath, Map<Float, Map<Integer, Double>> freqTimeAmpMap)
			throws IOException {
		System.out.println(freqTimeAmpMap.size());
		String flie = filePath.substring(0, filePath.lastIndexOf(".")) + ".csv";
		FileWriter fw = new FileWriter(new File(flie));
		try {
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
		} finally {
			try {
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static Map<Integer, double[]> readWav(AudioInputStream ais) throws Exception {
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
			// magic number for PCM SIGNED conversion
			windowData[i % windowSize] = ((audioBytes[2 * i] & 0xff) | (audioBytes[2 * i + 1] << 8)) / cvsNum;
		}
		return timeDataMap;
	}
}

class ChimeAnalysis {
	private float sampling_rate;
	private float target_frequency;
	private int n;
	private double[] testData;
	private double coeff, Q1, Q2;
	private double sine, cosine;

	public ChimeAnalysis(int sampleRate, float targetFreq, double[] data) {
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

	public double getMagnitudeSquared() {
		return (Q1 * Q1 + Q2 * Q2 - Q1 * Q2 * coeff);
	}

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
