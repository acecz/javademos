package cz.test.chime.myfft;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class MyAnalysisTest {
	// every window has 256 frames( every frame 2 bytes),equals 16ms
	private static final int windowSize = 256;
	// magic number for PCM SIGNED conversion
	private static final double cvsNum = 32768.0;

	public static void main(String[] args) throws Exception {
		Map<Integer, double[]> timeDataMap = readWav();
	}

	private static Map<Integer, double[]> readWav() throws Exception {
		File file = new File("/Users/cz/Desktop/snd2fftw_win/testwav/test05", "AudioRecord.wav");
		AudioInputStream ais = AudioSystem.getAudioInputStream(file);
		AudioFormat format = ais.getFormat();
		System.out.println(format);
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
			windowData[i % windowSize] = ((audioBytes[2 * i] & 0xff) | (audioBytes[2 * i + 1] << 8)) / cvsNum;

		}
		return timeDataMap;
	}
}
