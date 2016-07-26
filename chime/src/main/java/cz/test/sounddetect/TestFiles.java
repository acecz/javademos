package cz.test.sounddetect;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.apache.log4j.Logger;

import cz.test.chime.prod.GoertzelChimeAnalysis;
import cz.test.chime.record.VoiceRecorder;

public class TestFiles {
	private static final Logger log = Logger.getLogger(VoiceRecorder.class);
	private static final AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 16000F, 16, 1, 2,
			16000F, false);

	static String testDir = "/Users/cz/workspace/tmp/SoundCheck_log";

	public static void main(String[] args) throws Exception {
		List<File> allWavs = new ArrayList<>();
		loadAudioDatas(allWavs, new File(testDir));
		for (File wav : allWavs) {
			boolean pass = detectSound(wav, 50f, 15000);
			System.out.printf("%6s\t%s\n", pass, wav.getPath());
		}
	}

	private static void loadAudioDatas(List<File> allCsvs, File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				loadAudioDatas(allCsvs, f);
			}
		} else {
			if (file.getName().endsWith(".wav")) {
				allCsvs.add(file);
			}
		}
	}

	private static boolean detectSound(File wav, float thredholdDb, int expiredTime) throws Exception {
		final double energy = dB2Energy(thredholdDb);
		System.out.println(energy);
		int maxTimeSt = expiredTime / 16 + 1;
		AudioInputStream ais = null;
		long st = System.currentTimeMillis();
		try {
			ais = AudioSystem.getAudioInputStream(wav);
			float sampleRate = audioFormat.getSampleRate();
			// every 16 ms as window
			int sampleRateInt = Float.valueOf(sampleRate).intValue();
			final int windowSize = 16 * sampleRateInt / 1000;
			final byte[] audioBytes = new byte[windowSize * 2];
			int maxHz = (int) (sampleRate / 2);
			float freqIntv = sampleRate / windowSize;
			// the divisor of converting from a frame to float [-1,1]
			final float cvtFrameDivisor = 1 << (audioFormat.getFrameSize() * 8 - 1);
			double[] windowData = new double[windowSize];
			int amplCnt = 0;
			for (int read = 0, timest = 0; (read = ais.read(audioBytes)) != -1 && timest < maxTimeSt; timest++) {
				for (int i = 0; i < read / 2; i++) {
					// magic number for PCM SIGNED conversion. hard code
					// 2bytes/frame
					windowData[i % windowSize] = ((audioBytes[2 * i] & 0xff) | (audioBytes[2 * i + 1] << 8))
							/ cvtFrameDivisor;
				}
				for (float hz = freqIntv; hz < maxHz; hz += freqIntv) {
					GoertzelChimeAnalysis ca = new GoertzelChimeAnalysis(sampleRateInt, hz, windowData);
					if (ca.calAmpl() >= energy) {
						System.out.println(hz + " " + ca.calAmpl());
						amplCnt += 1;
						System.out.println(amplCnt);
						if (amplCnt >= 3) {
							System.out.println("TRUE");
							return true;
						}
						break;
					} else if (hz <= maxHz - freqIntv) {
						amplCnt = 0;
					}
				}
			}
		} finally {
			// System.out.println("final:" + (System.currentTimeMillis() - st));
			try {
				if (ais != null) {
					ais.close();
				}
			} catch (Exception e) {
				log.error("release resource error!", e);
				// ignore
			}
		}
		return false;
	}

	private static double dB2Energy(double db) {
		// the 2E-5 value:refer to
		// http://www.sengpielaudio.com/calculator-soundvalues.htm
		// the reference sound pressure p0 = 20 µPa = 2 × 10−5 Pa.
		return Math.pow(10, db / 10) * 2E-5;
	}
}
