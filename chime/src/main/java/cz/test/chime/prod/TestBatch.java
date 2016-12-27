package cz.test.chime.prod;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class TestBatch {
	static String dir = "/Users/cz/Desktop/wav";
	static String[] future = { "TIMES3" };

	public static void main(String[] args) throws Exception {
		Map<String, File> files = new TreeMap<>();
		loadWav(new File(dir), files);
		for (File file : files.values()) {
			ChimeCheckUtil.validateWavEh(file, future);
		}
	}

	private static void loadWav(File file, Map<String, File> files) throws IOException {
		if (file.isDirectory()) {
			File[] fs = file.listFiles();
			for (File f : fs) {
				loadWav(f, files);
			}
		} else {
			if (file.getName().toLowerCase().endsWith("wav")) {
				files.put(file.getCanonicalPath(), file);
			}
		}

	}

}
