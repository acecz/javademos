package org.apache.log4j;

public class Logger {

	private static Logger logger = new Logger();

	public static <T> Logger getLogger(Class<T> clz) {
		return logger;
	}

	public void info(String string) {
		System.out.println(string);

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
		e.printStackTrace();
	}

	public void warn(String string) {
		System.out.println(string);
	}

	public void error(String string) {
		System.err.println(string);

	}

}
