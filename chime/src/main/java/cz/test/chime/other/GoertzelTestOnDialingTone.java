package cz.test.chime.other;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class GoertzelTestOnDialingTone {
	static public class Goertzel {
		private float sampling_rate;
		private float target_frequency;
		private int n;
		private double[] testData;
		private double coeff, Q1, Q2;
		private double sine, cosine;
		public boolean debug = false;

		public Goertzel(float sampleRate, float targetFreq, double[] data, boolean inDebug) {
			sampling_rate = sampleRate;
			target_frequency = targetFreq;
			n = data.length;
			debug = inDebug;
			testData = data;

			// In case initGoertzel is not called, initialize the Goertzel
			// parameters with default precomputed values.
			// Below = 21000 Hz
			sine = 0.14904226617617444692935471527722; // = sin(2*pi*200/420)
			cosine = -0.98883082622512854506974288293401; // = cos(2*pi*200/420)
			// Below = 19005 Hz
			// sine = 0.42035722830956549189972281978021; // = sin(2*pi*181/420)
			// cosine = -0.90735869456786483795065221200264;// =
			// cos(2*pi*181/420)
			// Below = 22995 Hz
			// sine = -0.13423326581765547603701864151067; // =
			// sin(2*pi*219/420)
			// cosine = -0.99094976176793475524868671316836;// =
			// cos(2*pi*219/420)
			coeff = 2 * cosine;
		}

		/**
		 * Call this method after every block of N samples has been processed.
		 *
		 * @return void
		 */
		public void resetGoertzel() {
			Q2 = 0;
			Q1 = 0;
		}

		/**
		 * Call this once, to precompute the constants.
		 *
		 * @return void
		 */
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

			/*
			 * System.out.println("For SAMPLING_RATE = " + sampling_rate);
			 * System.out.print(" N = " + n); System.out.println(
			 * " and FREQUENCY = " + target_frequency); System.out.println(
			 * "k = " + k + " and coeff = " + coeff + "\n");
			 */

			resetGoertzel();
		}

		/**
		 * Call this routine for every sample.
		 *
		 * @param sample
		 *            is a double
		 * @return void
		 */
		public void processSample(double sample) {
			double Q0;

			Q0 = coeff * Q1 - Q2 + sample;
			Q2 = Q1;
			Q1 = Q0;
		}

		/**
		 * Basic Goertzel. Call this routine after every block to get the
		 * complex result.
		 *
		 * @param parts
		 *            has length two where the first item is the real part and
		 *            the second item is the complex part.
		 * @return double[] stores the values in the param
		 */
		public double[] getRealImag(double[] parts) {
			parts[0] = (Q1 - Q2 * cosine);
			parts[1] = (Q2 * sine);
			return parts;
		}

		/**
		 * Optimized Goertzel. Call this after every block to get the RELATIVE
		 * magnitude squared.
		 *
		 * @return double is the value of the relative mag squared.
		 */
		public double getMagnitudeSquared() {
			return (Q1 * Q1 + Q2 * Q2 - Q1 * Q2 * coeff);
		}

		/**
		 * End of Goertzel-specific code, the remainder is test code.
		 */
		public double test() {

			int index;

			double magnitudeSquared;
			double magnitude;
			double real;
			double imag;
			double[] parts = new double[2];

			// System.out.println("Freq= " + frequency);
			// generate(frequency);

			/* Process the samples. */
			for (index = 0; index < n; index++) {
				processSample(testData[index]);
			}

			parts = getRealImag(parts);
			real = parts[0];
			imag = parts[1];

			magnitudeSquared = real * real + imag * imag;
			// System.out.println("rel mag^2= " + magnitudeSquared);
			magnitude = java.lang.Math.sqrt(magnitudeSquared);
			// System.out.println("rel mag= " + magnitude);

			resetGoertzel();

			return magnitude;
		}
		// ...... //

	}

	public static void main(String[] args) throws IOException, UnsupportedAudioFileException {

		final File audioFile = new File("/Users/cz/Desktop/testwav/test05", "AudioRecord.wav");
		final AudioInputStream inputStream = AudioSystem.getAudioInputStream(audioFile);
		if (inputStream.getFormat().getFrameSize() != 2)
			throw new IllegalArgumentException("Must be 2 bytes per frame");
		if (inputStream.getFormat().isBigEndian())
			throw new IllegalArgumentException("Must be little endian");
		if (inputStream.getFormat().getEncoding() != AudioFormat.Encoding.PCM_SIGNED)
			throw new IllegalArgumentException("Must be PCM_SIGNED ");

		System.out.println("Sample size in bits   : " + inputStream.getFormat().getSampleSizeInBits());
		System.out.println("Encoding              : " + inputStream.getFormat().getEncoding());
		float sampleRate = (int) inputStream.getFormat().getSampleRate();
		System.out.println("Sample rate           : " + sampleRate);
		System.out.println("Number of channels    : " + inputStream.getFormat().getChannels());
		System.out.println("Frame rate            : " + inputStream.getFormat().getFrameRate());
		System.out.println("Big-endian            : " + inputStream.getFormat().isBigEndian());

		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final byte[] buff = new byte[1024];
		for (int read = 0; (read = inputStream.read(buff)) != -1;) {
			out.write(buff, 0, read);
		}
		out.close();
		final byte[] audioBytes = out.toByteArray();

		double[] data_ = new double[audioBytes.length / 2];
		for (int i = 0, j = 0; j < data_.length;) {
			// Little endian PCM SIGNED conversion
			data_[j++] = ((audioBytes[i++] & 0xff) | (audioBytes[i++] << 8)) / 32768.0;
		}

		BufferedWriter br = new BufferedWriter(new FileWriter("output.txt"));

		double max = -1;
		double maxFreq = -1;

		double maxScanFreq = 5000;

		System.out.println("Scanning from 0 to " + maxScanFreq + " Hz");
		for (int k = 0; k < maxScanFreq; k++) {
			double ampl = -1;

			System.out.println("searching for " + k + "Hz");
			Goertzel test = new Goertzel(sampleRate, (float) k, data_, false);
			test.initGoertzel();

			// Run the Algorithm
			ampl = test.test();

			// Pitches detection
			if (ampl > 800000) {
				br.write("New pitch at " + k + " of ampl " + ampl);
				br.newLine();
			}

			if (ampl > max) {
				max = ampl;
				maxFreq = k;
			}

		}
		System.out.println("Amplitude: " + max + " max at " + maxFreq + "Hz");
		br.close();

	}

}