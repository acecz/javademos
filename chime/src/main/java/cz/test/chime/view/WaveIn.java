package cz.test.chime.view;

/*
 * WaveIn.java
 * 捕获音频输出
 */
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

public class WaveIn {
	private AudioFormat af;
	private DataLine.Info dli;
	private TargetDataLine tdl;

	/**
	 * 打开音频目标数据行。从中读取音频数据格式为：采样率32kHz，每个样本16位，单声道，有符号的，little-endian。
	 * 
	 * @return 成功打开返回true，否则false。
	 */
	public boolean open() {
		af = new AudioFormat(32000, 16, 1, true, false);
		dli = new DataLine.Info(TargetDataLine.class, af);
		try {
			tdl = (TargetDataLine) AudioSystem.getLine(dli);
			tdl.open(af, FFT.FFT_N << 1);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public void close() {
		tdl.close();
	}

	public void start() {
		tdl.start();
	}

	public void stop() {
		tdl.stop();
	}

	public int read(byte[] b, int len) {
		return tdl.read(b, 0, len);
	}

	private double phase0 = 0;

	/**
	 * 产生频率264Hz，采样率为44.1kHz，幅值为0x7fff，每个样本16位的PCM。
	 * 
	 * @param b
	 *            接收PCM样本。
	 * @param len
	 *            PCM样本字节数。
	 */
	public void getWave264(byte[] b, int len) {
		double dt = 2 * 3.14159265358979323846 * 264 / 44100;
		int i, pcmi;
		len >>= 1;
		for (i = 0; i < len; i++) {
			pcmi = (short) (0x7fff * Math.sin(i * dt + phase0));
			b[2 * i] = (byte) pcmi;
			b[2 * i + 1] = (byte) (pcmi >>> 8);
		}
		phase0 += i * dt;
	}
}
