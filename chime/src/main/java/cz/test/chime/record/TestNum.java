package cz.test.chime.record;

import java.util.HashMap;
import java.util.Map;

public class TestNum {
	public static void main(String[] args) {
		Map<String, Object> map = new HashMap<>();
		map.put("aa", "11");
		Long aa = (Long) map.get("aa");
		System.out.println(aa);

	}
}
