package cz.test.chime.prod;

public class GoertzelChimeAnalysis {

	private float sampling_rate;
	private float target_frequency;
	private int n;
	private double[] testData;
	private double coeff, Q1, Q2;
	private double sine, cosine;

	public GoertzelChimeAnalysis(int sampleRate, float targetFreq, double[] data) {
		sampling_rate = sampleRate;
		target_frequency = targetFreq;
		n = data.length;
		testData = data;
		initGoertzel();
	}

	public void resetGoertzel() {
		Q2 = 0;
		Q1 = 0;
	}

	public void initGoertzel() {
		int k;
		float floatN;
		double omega;
		floatN = (float) n;
		k = (int) (0.5 + ((floatN * target_frequency) / sampling_rate));
		omega = (2.0 * Math.PI * k) / floatN;
		sine = Math.sin(omega);
		cosine = Math.cos(omega);
		coeff = 2.0 * cosine;
		resetGoertzel();
	}

	public void processSample(double sample) {
		double Q0;
		Q0 = coeff * Q1 - Q2 + sample;
		Q2 = Q1;
		Q1 = Q0;
	}

	public double[] getRealImag(double[] parts) {
		parts[0] = (Q1 - Q2 * cosine);
		parts[1] = (Q2 * sine);
		return parts;
	}

	// public double getMagnitudeSquared() {
	// return (Q1 * Q1 + Q2 * Q2 - Q1 * Q2 * coeff);
	// }

	public double calAmpl() {
		int index;
		double magnitudeSquared;
		double magnitude;
		double real;
		double imag;
		double[] parts = new double[2];

		for (index = 0; index < n; index++) {
			processSample(testData[index]);
		}
		parts = getRealImag(parts);
		real = parts[0];
		imag = parts[1];
		magnitudeSquared = real * real + imag * imag;
		magnitude = java.lang.Math.sqrt(magnitudeSquared) / 2;
		resetGoertzel();
		return magnitude;
	}

}
