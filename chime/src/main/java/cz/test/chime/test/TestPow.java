package cz.test.chime.test;

public class TestPow {
	public static void main(String[] args) {
		double k = 1000;
		double b = Math.log10(k);
		double rk = Math.pow(10, b);
		System.out.printf("%f\t%f\t%f", k, b, rk);
	}
}
