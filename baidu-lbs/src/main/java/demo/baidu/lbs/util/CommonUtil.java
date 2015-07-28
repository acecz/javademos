package demo.baidu.lbs.util;

import java.security.MessageDigest;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.sun.jersey.core.util.Base64;

public class CommonUtil {
	private static final Logger log = Logger.getLogger(CommonUtil.class);

	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static boolean isNotBlank(String src) {
		return !(src == null || src.trim().length() == 0);
	}

	public static byte[] hexToBytes(String hexStr) {
		int len = hexStr.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(hexStr.charAt(i), 16) << 4)
					+ Character.digit(hexStr.charAt(i + 1), 16));
		}
		return data;
	}

	public static String SHA1Encode(String sourceString) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			return CommonUtil.bytesToHex(md.digest(sourceString.getBytes()));
		} catch (Exception e) {
			log.error("SHA1Encode Error!", e);
		}
		return null;
	}

	/** to yyyyMMddhhmmss */
	public static Long time2long(Long time) {
		if (time == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		long l = 10000000000L;
		l = l * cal.get(Calendar.YEAR);
		l += (cal.get(Calendar.MONTH) + 1) * 100000000L;
		l += cal.get(Calendar.DAY_OF_MONTH) * 1000000L;
		l += cal.get(Calendar.HOUR_OF_DAY) * 10000L;
		l += cal.get(Calendar.MINUTE) * 100L + cal.get(Calendar.SECOND);
		return l;
	}

	/** to millis */
	public static Long long2time(Long y2s) {
		if (y2s == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		int second = (int) (y2s % 100);
		int minute = (int) (y2s % 10000 / 100);
		int hourOfDay = (int) (y2s % 1000000 / 10000);
		int date = (int) (y2s % 100000000 / 1000000);
		int month = (int) (y2s % 10000000000L / 100000000L - 1);
		int year = (int) (y2s / 10000000000L);
		cal.set(year, month, date, hourOfDay, minute, second);
		return cal.getTimeInMillis();
	}

	public static String MD5Encode(String sourceString) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			return byte2hexString(md.digest(sourceString.getBytes()));
		} catch (Exception e) {
			log.error("MD5Encode Error!", e);
		}
		return null;
	}

	private static String byte2hexString(byte[] bytes) {
		StringBuffer buf = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			if (((int) bytes[i] & 0xff) < 0x10) {
				buf.append("0");
			}
			buf.append(Long.toString((int) bytes[i] & 0xff, 16));
		}
		return buf.toString().toUpperCase();
	}

	public static String encode2base64(String src) {
		return new String(Base64.encode(src));
	}

	public static String decodeFromBase64(String src) {
		return new String(Base64.decode(src));
	}

	public static boolean regCheck(String reg, String string) {
		boolean tem = false;
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(string);
		tem = matcher.matches();
		return tem;
	}
}
