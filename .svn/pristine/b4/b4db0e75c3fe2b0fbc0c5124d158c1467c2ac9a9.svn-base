package biz.com.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import biz.com.service.RSSReader;

public class RSSReaderImpl implements RSSReader {

	private static RSSReader instance = null;

	private URL rssURL;
	private String headerstyle = "";
	private String titlestyle = "";
	private String descstyle = "";
	private String dateStyle = "";
	private String textorthum = "";

	public String getTextorthum() {
		return textorthum;
	}

	public void setTextorthum(String textorthum) {
		this.textorthum = textorthum;
	}

	public String getDateStyle() {
		return dateStyle;
	}

	public void setDateStyle(String dateStyle) {
		this.dateStyle = dateStyle;
	}

	public URL getRssURL() {
		return rssURL;
	}

	public void setRssURL(URL rssURL) {
		this.rssURL = rssURL;
	}

	public String getHeaderstyle() {
		return headerstyle;
	}

	public void setHeaderstyle(String headerstyle) {
		this.headerstyle = headerstyle;
	}

	public String getTitlestyle() {
		return titlestyle;
	}

	public void setTitlestyle(String titlestyle) {
		this.titlestyle = titlestyle;
	}

	public String getDescstyle() {
		return descstyle;
	}

	public void setDescstyle(String descstyle) {
		this.descstyle = descstyle;
	}

	public void setURL(URL url) {
		rssURL = url;
	}

	public ArrayList writeFeed1(HttpServletRequest request,
			HttpServletResponse response, ArrayList al) throws IOException,
			ParserConfigurationException, SAXException {

		try {
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			// out.print(rssURL+"");
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document doc = builder.parse(rssURL.openStream());

			NodeList items = doc.getElementsByTagName("item");

			for (int i = 0; i < items.getLength(); i++) {
				Element item = (Element) items.item(i);
				StringBuilder br = new StringBuilder();
				if (item != null) {

					br.append("<li>");
					if (this.getTextorthum().equals("textandimage")) {
						if (!getValue(item, "img").equals("")) {
							br.append("<img src='"
									+ getValue(item, "img")
									+ "' style='float:left;width:90px:height:51px'/>");
						} else if (getValueMedia(i, "media:thumbnail", doc) != null) {
							br.append("<img src='"
									+ getValueMedia(i, "media:thumbnail", doc)
									+ "' style='float:left;width:90px:height:51px'/>");
						}
					}
					br.append("<div class=\"text\">\n" + "");
					br.append("<a href='" + getValue(item, "link")
							+ "' target='_blank' style='"
							+ this.getTitlestyle() + "'>"
							+ getValue(item, "title") + "</a>");
					br.append("</div>");
					br.append("<p style='white-space: nowrap;overflow: hidden; text-overflow: ellipsis;"
							+ this.getDescstyle()
							+ "'><small>"
							+ removeTags(getValue(item, "description"))
							+ "</small></p>");

					br.append("<p style='" + this.getDateStyle() + "'><small>"
							+ getValue(item, "pubDate") + "</small></p>");
					br.append("</li>");

				
					al.add(br.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}

	public String getValue(Element parent, String nodeName) {
		try {
			
			return parent.getElementsByTagName(nodeName).item(0)
					.getFirstChild().getNodeValue();
		} catch (NullPointerException e) {
			return "";
		}
	}

	public String getValueMedia(int i, String nodeName, Document doc) {
		try {
			NodeList thumbList = doc.getDocumentElement().getElementsByTagName(
					"media:thumbnail");
			Element thumbElement = (Element) thumbList.item(i);
			String thumbURL = thumbElement.getAttribute("url");

			return thumbURL;
		} catch (Exception e) {
			return null;
		}
	}

	
	private static final Pattern REMOVE_TAGS = Pattern.compile("<.+?>");

	public String removeTags(String string) {
		if (string == null || string.length() == 0) {
			return string;
		}

		Matcher m = REMOVE_TAGS.matcher(string);
		return m.replaceAll("");
	}


}
