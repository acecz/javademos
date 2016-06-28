package cz.test.chime.prod;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class TestBatch {
	static String dir = "/Users/cz/Desktop/chimes";
	static String[] future = { "FREQUENCY1500GV1000LV1400" };

	public static void main(String[] args) throws Exception {
		Set<File> files = new HashSet<>();
		loadWav(new File(dir), files);
		for (File file : files) {
			ChimeCheckUtil.validateWav(file, future);
		}
	}

	private static void loadWav(File file, Set<File> files) {
		if (file.isDirectory()) {
			File[] fs = file.listFiles();
			for (File f : fs) {
				loadWav(f, files);
			}
		} else {
			if (file.getName().toLowerCase().endsWith("wav")) {
				files.add(file);
			}
		}

	}

}
