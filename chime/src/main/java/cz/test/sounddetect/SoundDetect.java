package cz.test.sounddetect;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

import org.apache.log4j.Logger;

import cz.test.chime.prod.GoertzelChimeAnalysis;
import cz.test.chime.record.VoiceRecorder;

public class SoundDetect {

	private static final Logger log = Logger.getLogger(VoiceRecorder.class);
	private static final AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 16000F, 16, 1, 2,
			16000F, false);
	private static final DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);

	public static void main(String[] args) throws Exception {
		System.out.println(detectSound(40f, 15000));
	}

	private static boolean detectSound(float thredholdDb, int expiredTime) throws Exception {
		final double energy = dB2Energy(thredholdDb);
		int maxTimeSt = expiredTime / 16 + 1;
		TargetDataLine targetDataLine = null;
		AudioInputStream ais = null;
		byte[] allAudioBytes = null;
		long st = System.currentTimeMillis();
		Map<Integer, String> ampls = new TreeMap<>();
		try {
			targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
			targetDataLine.open(audioFormat);
			targetDataLine.start();
			ais = new AudioInputStream(targetDataLine);
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
			allAudioBytes = new byte[windowSize * 2 * maxTimeSt];
			for (int read = 0, timest = 0; (read = ais.read(audioBytes)) != -1 && timest < maxTimeSt; timest++) {
				System.arraycopy(audioBytes, 0, allAudioBytes, timest * windowSize * 2, audioBytes.length);
				for (int i = 0; i < read / 2; i++) {
					// magic number for PCM SIGNED conversion. hard code
					// 2bytes/frame
					windowData[i % windowSize] = ((audioBytes[2 * i] & 0xff) | (audioBytes[2 * i + 1] << 8))
							/ cvtFrameDivisor;
				}
				int timePoint = 16 * timest;
				ampls.put(timePoint, String.format("%20d", timePoint));
				boolean timeHasAmpl = false;
				for (float hz = freqIntv; hz < maxHz; hz += freqIntv) {
					GoertzelChimeAnalysis ca = new GoertzelChimeAnalysis(sampleRateInt, hz, windowData);
					double fqEnergy = ca.calAmpl();
					if (fqEnergy >= energy) {
						timeHasAmpl = true;
						amplCnt += 1;
						ampls.put(timePoint, String.format("%20d%20f%20f", timePoint, hz, calcDB(fqEnergy)));
						System.out.println(ampls.get(timePoint));
						if (amplCnt >= 3) {
							System.out.println("TRUE");
							return true;
						}
						break;
					}
				}
				if (!timeHasAmpl) {
					amplCnt = 0;
				}
			}
		} finally {
			System.out.println("final:" + (System.currentTimeMillis() - st));
			AudioInputStream stream = null;
			try {
				if (allAudioBytes != null) {
					stream = new AudioInputStream(new ByteArrayInputStream(allAudioBytes), audioFormat,
							allAudioBytes.length);
					File file = new File("/Users/cz/Desktop/test.wav");
					file.delete();
					AudioSystem.write(stream, AudioFileFormat.Type.WAVE, file);
					PrintWriter amplWriter = null;
					try {
						if (!ampls.isEmpty()) {
							File log = new File(file.getAbsolutePath().replace(".wav", "SoundDetectReordAmpls.txt"));
							log.delete();
							amplWriter = new PrintWriter(log);
							amplWriter.write(String.format("%20s%20s%20s", "TIME", "FREQUENCY", "DB"));
							amplWriter.write("\n");
							for (String rec : ampls.values()) {
								amplWriter.write(rec);
								amplWriter.write("\n");
							}
						}
					} catch (Exception e) {
						log.error("release resource error!", e);
						// ignore
					} finally {
						try {
							if (amplWriter != null) {
								amplWriter.close();
							}
						} catch (Exception e) {
							log.error("release resource error!", e);
							// ignore
						}
					}
				}
			} catch (Exception e) {
				log.error("release resource error!", e);
			} finally {
				try {
					if (stream != null) {
						stream.close();
					}
				} catch (Exception e) {
					log.error("release resource error!", e);
					// ignore
				}
			}
			try {
				if (ais != null) {
					ais.close();
				}
			} catch (Exception e) {
				log.error("release resource error!", e);
				// ignore
			}
			if (targetDataLine != null) {
				targetDataLine.stop();
				targetDataLine.close();
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

	private static double calcDB(double energy) {
		// the 5E4 value:refer to
		// http://www.sengpielaudio.com/calculator-soundvalues.htm
		// the reference sound pressure p0 = 20 µPa = 2 × 10−5 Pa.
		return 10 * Math.log10(energy * 5E4);
	}

}
