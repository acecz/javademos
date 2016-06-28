package cz.test.chime.split;

import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class SplitWav {
	static File wav = null;

	public static void main(String[] args) throws Exception {
		readWav();
	}

	private static void readWav() throws Exception {
		AudioInputStream ais = AudioSystem.getAudioInputStream(wav);
		AudioFormat format = ais.getFormat();
		AudioInputStream out = AudioSystem.getAudioInputStream(wav);

	}

}
