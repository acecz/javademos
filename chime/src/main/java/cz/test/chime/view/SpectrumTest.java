package cz.test.chime.view;

import javax.swing.JFrame;

public class SpectrumTest {
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		final Spectrum spec = new Spectrum();
		frame.getContentPane().add(spec);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Audio Spectrum");
		frame.setResizable(false);
		frame.pack();
		// frame.setAlwaysOnTop(true);
		frame.setVisible(true);
		// com.sun.awt.AWTUtilities.setWindowOpacity(frame, 0.8f);
		new Thread(spec).start();
	}

}
