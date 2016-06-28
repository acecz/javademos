package cz.test.chime.record;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

import org.apache.log4j.Logger;

public class VoiceRecorder {
	private static final Logger log = Logger.getLogger(VoiceRecorder.class);
	private static final AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 16000F, 16, 1, 2,
			16000F, false);
	private static final AudioFileFormat.Type audioFileFormat = AudioFileFormat.Type.WAVE;
	private static final DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
	private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
	private static final BlockingQueue<File> recQueue = new ArrayBlockingQueue<>(1);
	private static Recorder recorder;

	public static void startRecorder(long maxTime) throws Exception {
		String fileName = Thread.currentThread().getName() + ".wav";
		log.info(String.format("%s %s %d", "pre queue", fileName, System.currentTimeMillis()));
		recQueue.put(new File(fileName));
		log.info(String.format("%s %s %d", "after queue", fileName, System.currentTimeMillis()));
		recorder = new Recorder();
		executor.execute(recorder);
		executor.schedule(new Runnable() {
			@Override
			public void run() {
				recorder.releaseResource();
				File f = recQueue.poll();
				if (f != null) {
					log.warn("expired ended! " + f.getName());
				}
			}
		}, maxTime, TimeUnit.MILLISECONDS);
	}

	public static File getRecorderFile(long recordTime) throws Exception {
		ScheduledFuture<File> future = executor.schedule(new Callable<File>() {
			@Override
			public File call() throws Exception {
				File f = recQueue.poll();
				String name = f == null ? "NULL" : f.getName();
				log.info(String.format("%s %s %d", "fetch result", name, System.currentTimeMillis()));
				recorder.releaseResource();
				return f;
			}
		}, recordTime, TimeUnit.MILLISECONDS);
		return future.get();
	}

	private static class Recorder implements Runnable {
		private TargetDataLine targetDataLine;

		@Override
		public void run() {
			try {
				log.info(String.format("%s %s %d", "start record", recQueue.peek().getName(),
						System.currentTimeMillis()));
				targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
				targetDataLine.open(audioFormat);
				targetDataLine.start();
				AudioSystem.write(new AudioInputStream(targetDataLine), audioFileFormat, recQueue.peek());
			} catch (Exception e) {
				log.error("fail to open voice resource!", e);
			}
		}

		public void releaseResource() {
			log.debug("release thread!" + Thread.currentThread().getName());
			if (targetDataLine != null && targetDataLine.isOpen()) {
				targetDataLine.stop();
				targetDataLine.close();
				targetDataLine = null;
			}
		}
	}

	public static void main(String[] args) throws Exception {
		for (int i = 0; i < 5; i++) {
			final int t = i;
			new Thread() {
				public void run() {
					try {
						String tn = "t-" + t;
						this.setName(tn);
						log.info(String.format("%s %s %d", "Start loop", tn, System.currentTimeMillis()));
						VoiceRecorder.startRecorder(10000);
						File f = VoiceRecorder.getRecorderFile((5 + 2 * t) * 1000);
						VoiceRecorder.getRecorderFile(2000);
						String name = f == null ? "NULL" : f.getName();
						log.info("****************** GET " + name);
					} catch (Exception e) {
						e.printStackTrace();
					}
				};
			}.start();

		}
		// executor.shutdown();
	}

}
