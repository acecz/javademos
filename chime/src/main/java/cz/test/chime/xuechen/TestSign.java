package cz.test.chime.xuechen;

public class TestSign {
	public static void main(String[] args) {
		for (float i = -10; i < 11; i += 0.01) {
			System.out.printf("%5.2f %8.5f %f\n", i, Math.sin(i), Math.floor(1.1 + Math.sin(i)));
		}
	}
}
