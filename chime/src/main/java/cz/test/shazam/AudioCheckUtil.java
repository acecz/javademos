package cz.test.shazam;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.apache.log4j.Logger;

import cz.test.shazam.data.AudioData;
import cz.test.shazam.data.AudioMatchResult;

public class AudioCheckUtil {
	private final static Logger log = Logger.getLogger(AudioCheckUtil.class);
	static String fileDir = "/Users/cz/Desktop/Chime_06_29/ChimeCheck_81_750_1/2E6408F0102E00A2/0_ChimeCheck_81_750";

	public static void main(String[] args) throws Exception {
		Map<Integer, String> audioFpMap = hashAudio(new File(fileDir, "AudioRecord-2.wav"));
		System.out.println("*************************");
		System.out.println(audioFpMap);
	}

	public static Map<Integer, String> hashAudio(File file) throws Exception {
		AudioData audioData = analysisWav(file);
		Map<Integer, String> audioFpMap = audioData.makeFingerprintOfAudio();
		return audioFpMap;
	}

	public static AudioData audioFingerprint(File file) throws Exception {
		// log.debug("FP SONG:" + file.getName());
		AudioData audioData = analysisWav(file);
		audioData.makeFingerprintOfAudio();
		return audioData;
	}

	private static AudioData analysisWav(File wav) throws Exception {
		AudioInputStream ais = AudioSystem.getAudioInputStream(wav);
		try {
			AudioFormat format = ais.getFormat();
			// the divisor of converting from a frame to float [-1,1]
			final float cvtFrameDivisor = 1 << (format.getFrameSize() * 8 - 1);
			// every 16 ms as window
			int timeIntv = 16;
			final int windowSize = timeIntv * Float.valueOf(format.getSampleRate()).intValue() / 1000;
			Map<Integer, double[]> windowDataMAp = readWav(ais, cvtFrameDivisor, windowSize);
			int sampleRate = Float.valueOf(format.getSampleRate()).intValue();
			int maxHz = sampleRate / 5;
			float freqIntv = format.getSampleRate() / windowSize;
			int maxHzStep = Float.valueOf(maxHz / freqIntv).intValue();
			AudioData ad = new AudioData(freqIntv, timeIntv, new double[windowDataMAp.size()][maxHzStep]);
			for (int t = 0; t < windowDataMAp.size(); t++) {
				for (int hzstep = 0; hzstep < maxHzStep; hzstep += 1) {
					GoertzelChimeAnalysis ca = new GoertzelChimeAnalysis(sampleRate, hzstep * freqIntv,
							windowDataMAp.get(t));
					ad.ampls[t][hzstep] = ca.calAmpl();
				}
			}
			return ad;
		} finally {
			try {
				if (ais != null) {
					ais.close();
				}
			} catch (Exception e) {
				log.error("close resource error!", e);
			}
		}
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

	public static AudioMatchResult recognizeAudio(File song, Map<String, AudioData> songFps) throws Exception {
		AudioData fp = audioFingerprint(song);
		Map<String, AudioMatchResult> matchRecs = new HashMap<>();
		for (String key : songFps.keySet()) {
			AudioData tfp = songFps.get(key);
			matchRecs.put(key, doMatchCheck(fp, tfp, key));
		}
		AudioMatchResult max = new AudioMatchResult("");
		for (AudioMatchResult amr : matchRecs.values()) {
			// System.out.printf("song:%s,offset:%d,matchs:%d\n", amr.songName,
			// amr.getOffset(), amr.getTimes());
			if (max.getMatchTimes() < amr.getMatchTimes()) {
				max = amr;
			} else if (max.getMatchTimes() == amr.getMatchTimes()) {
				// TODO
			}
		}
		return max;
	}

	private static AudioMatchResult doMatchCheck(AudioData fp, AudioData tfp, String key) {
		AudioMatchResult rst = new AudioMatchResult(key);
		rst.setSampleHashs(fp.hashs.length);
		rst.setTargetHashs(tfp.hashs.length);
		String[] hashs = fp.hashs;
		String[] thashs = tfp.hashs;
		for (int i = 0; i < thashs.length; i++) {
			int matchCnt = 0;
			for (int j = 0; j < hashs.length && i + j < thashs.length; j++) {
				if (hashs[j].equals(thashs[i + j])) {
					matchCnt++;
				}
			}
			if (rst.getMatchTimes() < matchCnt) {
				rst.setOffset(i);
				rst.setMatchTimes(matchCnt);
			}
		}
		return rst;
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
