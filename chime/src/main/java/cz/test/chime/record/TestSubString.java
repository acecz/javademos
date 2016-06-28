package cz.test.chime.record;

public class TestSubString {
	public static void main(String[] args) {
		String s = "abcdfg";
		int p = s.length();
		String path = s.substring(0, p);
		s = s.substring(p + 1);
	}
}
