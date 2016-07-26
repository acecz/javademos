package cz.test.shazam;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import cz.test.shazam.data.AudioData;
import cz.test.shazam.data.AudioMatchResult;

public class TestFolder {
	private final static Logger log = Logger.getLogger(TestFolder.class);
	static final String dir = "/Users/cz/Desktop/sample";

	public static void main(String[] args) throws Exception {
		List<File> songs = new ArrayList<>();
		loadSongs(songs, new File(dir + "/train"));
		Map<String, AudioData> songFps = new HashMap<>();
		for (File file : songs) {
			songFps.put(file.getAbsolutePath(), AudioCheckUtil.audioFingerprint(file));
		}
		songs.clear();
		loadSongs(songs, new File(dir, "/recs"));
		log.debug("song count=" + songs.size());
		for (File file : songs) {
			// log.debug("REC SONG:" + file.getName());
			AudioMatchResult amr = AudioCheckUtil.recognizeAudio(file, songFps);
			System.out.println(file.getName() + " ---> " + amr.dbgStr());
		}
	}

	private static void loadSongs(List<File> songs, File file) {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				loadSongs(songs, f);
			}
		} else {
			if (file.getName().toLowerCase().endsWith(".wav")) {
				songs.add(file);
			}
		}
	}
}
