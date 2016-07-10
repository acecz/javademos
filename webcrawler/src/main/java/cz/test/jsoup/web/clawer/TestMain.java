package cz.test.jsoup.web.clawer;

import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.text.html.HTML.Tag;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TestMain {
	public static void main(String[] args) throws Exception {
		getWowSpells();
	}

	private static void getWowSpells() throws Exception {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter("wowSpells80000.html");
			Document doc = new Document("");
			Element html = doc.appendElement(Tag.HTML.toString());
			html.appendElement(Tag.HEAD.toString()).appendElement(Tag.META.toString())
					.attr("http-equiv", "Content-Type").attr("content", "text/html; charset=utf8");
			Element body = html.appendElement(Tag.BODY.toString());
			Element table = body.appendElement(Tag.TABLE.toString());
			table.attr("border", "1");
			Element thead = table.appendElement(Tag.TR.toString());
			thead.appendElement(Tag.TH.toString()).appendText("SpellId");
			thead.appendElement(Tag.TH.toString()).appendText("SpellIcon");
			thead.appendElement(Tag.TH.toString()).appendText("SpellName");
			thead.appendElement(Tag.TH.toString()).appendText("SpellDesc");
			appendContent(table);
			pw.write(doc.html());
			pw.flush();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	private static void appendContent(Element table) {
		String utlFmt = "http://db.duowan.com/wow/spell-%d.html";
		for (int i = 1; i < 80000; i++) {
			String url = String.format(utlFmt, i);
			Document docSpl = null;
			try {
				docSpl = loadUrl(url);
			} catch (Exception e) {
				System.out.println(e.getMessage());
				try {
					docSpl = loadUrl(url);
				} catch (Exception e1) {
					System.out.println(e.getMessage());
					try {
						docSpl = loadUrl(url);
					} catch (Exception e2) {
						System.out.println(e.getMessage());
						continue;
					}
				}
			}
			Elements contents = docSpl.getElementsByClass("skill-intro-content");
			if (contents.size() < 1) {
				continue;
			}
			Element content = contents.get(0);
			String iconSrc = docSpl.getElementsByClass("pic").get(0).getElementsByTag(Tag.IMG.toString()).get(0)
					.attr("src");
			String iconUrl = "http://db.duowan.com/wow/" + iconSrc.replace("\\", "/");
			String[] nameId = content.getElementsByTag(Tag.H4.toString()).text().split("ID:");
			String desc = content.getElementsByClass("skill-desc").text();
			Element tr = table.appendElement(Tag.TR.toString());
			tr.appendElement(Tag.TD.toString()).appendText(nameId[1]);
			tr.appendElement(Tag.TD.toString()).appendElement(Tag.IMG.toString()).attr("src", iconUrl);
			tr.appendElement(Tag.TD.toString()).appendText(nameId[0]);
			tr.appendElement(Tag.TD.toString()).appendText(desc);
		}
	}

	private static Document loadUrl(String url) throws IOException {
		Document doc = Jsoup.connect(url).execute().parse();
		return doc;
	}
}
