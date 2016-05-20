package cz.com.jdbc.demo;

import java.sql.Connection;
import java.sql.DriverManager;

public class JdbcUtil {
	static String jdbcUrl = "jdbc:mysql://127.0.0.1:3306/skykite?characterSetResults=UTF-8";
	static String dbUser = "xxx", dbPwd = "xxx";
	private static Connection conn = null;

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Can not find driver!");
			e.printStackTrace();
		}
	}

	public static synchronized Connection getConnection() {
		try {
			if (conn == null) {
				conn = DriverManager.getConnection(jdbcUrl, dbUser, dbPwd);
			}
		} catch (Exception e) {
			System.out.println("OPEN db connection Error!");
			e.printStackTrace();
		}
		return conn;
	}

	public static void closeRsc(AutoCloseable... dbRscs) {
		if (dbRscs == null || dbRscs.length == 0) {
			return;
		}
		for (AutoCloseable rsc : dbRscs) {
			if (rsc != null) {
				try {
					rsc.close();
					rsc = null;
				} catch (Exception e) {
					System.out.println("CLOSE DB resource Error!");
					e.printStackTrace();
				}
			}
		}

	}

}
