package cz.test.chime.record;

public class TestNull {
	public static void main(String[] args) {
		String s = ";a;b;;c; ;";
		for (String ss : s.split(";")) {
			System.out.println(ss.toUpperCase());
		}

	}
}
