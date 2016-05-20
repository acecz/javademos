package cz.com.jdbc.demo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {
	private static final String ADCONTENT_REGEX = "<img\\b*[^<>]*?\\bsrc=[\"\']*([^>\"\']*)[\"\']*\\b";
	private static final Pattern ADCONTENT_PATTERN = Pattern.compile(ADCONTENT_REGEX, Pattern.CASE_INSENSITIVE);
	private static final String querySql = "select adContent from kts_good";

	public static void main(String[] args) throws SQLException {
		List<String> contentList = getContent();
		for (String content : contentList) {
			System.out.println("Content parse Start ****");
			parseContent(content);
			System.out.println("Content parse Finish ****\n\n\n");
		}

	}

	private static void parseContent(String content) {
		System.out.println(content);
		Map<String, String> adContentImgSrcReplaceMap = new HashMap<>();
		Matcher matcher = ADCONTENT_PATTERN.matcher(content);
		while (matcher.find()) {
			String imgSrc = matcher.group(1);
			String picName = parImgName(imgSrc);
			adContentImgSrcReplaceMap.put(imgSrc, picName);

			for (Entry<String, String> ent : adContentImgSrcReplaceMap.entrySet()) {
				content = content.replace(ent.getKey(), ent.getValue());
				System.out.printf("key: %s\n\tval: %s\n\n", ent.getKey(), ent.getValue());
			}
		}
		System.out.println(content);

	}

	private static String parImgName(String imgSrc) {
		int qidx = imgSrc.indexOf("?");
		if (qidx > 0) {
			imgSrc = imgSrc.substring(0, qidx);
		}
		int lastSlashIdx = imgSrc.lastIndexOf("/");
		if (lastSlashIdx >= 0) {
			imgSrc = imgSrc.substring(lastSlashIdx + 1);
		}
		return imgSrc;
	}

	private static List<String> getContent() throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		List<String> contens = new LinkedList<>();
		try {
			conn = JdbcUtil.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(querySql);
			while (rs.next()) {
				contens.add(rs.getString("adContent"));
			}
		} catch (Exception e) {
			System.out.println("SEARCH result error!");
			e.printStackTrace();
		} finally {
			JdbcUtil.closeRsc(rs, stmt, conn);
		}
		return contens;
	}
}
