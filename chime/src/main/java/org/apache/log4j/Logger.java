package org.apache.log4j;

import java.io.IOException;

public class Logger {

	private static Logger logger = new Logger();

	public static <T> Logger getLogger(Class<T> clz) {
		return logger;
	}

	public void info(String string) {
		System.out.println(string);

	}

	public void error(String string, IOException e) {
		System.err.println(string);
		System.out.println(e.toString());

	}

	public boolean isDebugEnabled() {
		return true;
	}

	public void debug(String string) {
		System.out.println(string);

	}

	public void warn(StringBuilder append) {
		System.out.println(append.toString());

	}

	public void error(String string, Exception e) {
		System.err.println(string);
		System.out.println(e.toString());
	}

	public void warn(String string) {
		System.out.println(string);
	}

}
