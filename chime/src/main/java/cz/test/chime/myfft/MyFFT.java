package cz.test.chime.myfft;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.jtransforms.fft.DoubleFFT_1D;

public class MyFFT {
	private static final int seclen = 512;
	private static final int rate = 2;

	public static void main(String[] args) throws Exception {
		readWav();
	}

	private static void readWav() throws Exception {
		File file = new File("/Users/cz/Desktop/snd2fftw_win/testwav/test05", "AudioRecord.wav");
		AudioInputStream ais = AudioSystem.getAudioInputStream(file);
		AudioFormat format = ais.getFormat();
		System.out.println(format);
		byte[] tmpBts = new byte[seclen];
		int readLen = 0;
		while ((readLen = ais.read(tmpBts)) != -1) {
			if (readLen < seclen) {
				break;
			}
			testDoubleFFT_1D1(tmpBts);
		}
		ais.close();
	}

	private static Map<Integer, Double> testDoubleFFT_1D1(byte[] tmpBts) {
		int len = tmpBts.length;
		double[] sampleDate = new double[len];
		for (int i = 0; i < len / 2; i++) {
			sampleDate[i * 2] = ((tmpBts[i] & 0xFF) << 8) | (tmpBts[i + 1] & 0xFF);
		}
		// System.out.println(Arrays.toString(sampleDate));
		DoubleFFT_1D transfer = new DoubleFFT_1D(len / 2);
		transfer.complexForward(sampleDate);
		System.out.println(Arrays.toString(sampleDate));
		return null;
	}

}
