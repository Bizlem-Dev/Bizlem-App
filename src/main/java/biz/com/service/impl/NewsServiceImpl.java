package biz.com.service.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.jcr.LoginException;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.jcr.api.SlingRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import biz.com.service.NewsService;

/**
 * 
 * @author atul
 */
@Component(configurationFactory = true)
@Service(NewsService.class)
@Properties({ @Property(name = "news", value = "newsService") })
public class NewsServiceImpl implements NewsService {

	/** The repo variable is an object of SlingRepository interface. */

	@Reference
	private SlingRepository repo;

	Session session = null;

	public ArrayList<javax.jcr.Node> getNewsList(String newsType) {
		ArrayList<javax.jcr.Node> pb = null;

		javax.jcr.Node tempPrdNode, childNode;

		if (!newsType.trim().equals("")) {
			try {

				pb = new ArrayList<javax.jcr.Node>();
				session = repo.login(new SimpleCredentials("admin", "admin"
						.toCharArray()));

				String querryStr = "select * from [nt:unstructured] where  ISDESCENDANTNODE('/content/news/') and newsType  like '"
						+ newsType + "%'";
				Workspace workspace = session.getWorkspace();
				Query query = workspace.getQueryManager().createQuery(
						querryStr, Query.JCR_SQL2);
				QueryResult result = query.execute();
				NodeIterator iterator = result.getNodes();

				while (iterator.hasNext()) {

					tempPrdNode = (javax.jcr.Node) iterator.nextNode();

					pb.add(tempPrdNode);

				}

			} catch (LoginException e) {

			} catch (RepositoryException e) {
				// TODO Auto-generated catch block

			} catch (Exception e) {
				// TODO Auto-generated catch block

			}
		}
		return pb;
	}

	public String addtoDb(SlingHttpServletRequest request, String id,int code) {

		String metadeta = request.getParameter("metadeta");
		String[] para = { "process", "id", "metadeta" };
		String[] paravalue = { "store", id, metadeta };

		if(code==0){return this.callPostService(
				"http://34.193.219.25/bsearch/ressfeedtoDB.php", para,
				paravalue);
		}else{
					return this.callPostService(
							"http://34.193.219.25/bsearch/ressfeedtoDBwidget.php", para,
							paravalue);
		}
	}

	public String callPostService(String urlStr, String[] paramName,
			String[] paramValue) {

		StringBuilder response = new StringBuilder();
		URL url;
		try {
			System.out.println("caalign callPostService");
			url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setAllowUserInteraction(false);
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			OutputStream out = conn.getOutputStream();
			Writer writer = new OutputStreamWriter(out, "UTF-8");
			for (int i = 0; i < paramName.length; i++) {
				writer.write(paramName[i]);
				writer.write("=");
				writer.write(URLEncoder.encode(paramValue[i], "UTF-8"));
				writer.write("&");
			}
			writer.close();
			out.close();
			if (conn.getResponseCode() != 200) {
				System.out.println("indeside caalign responce"
						+ conn.getResponseCode());
			}
			System.out.println("caalign responce");
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			response = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				response.append(line);
			}
			rd.close();
			System.out.println("closinng conneciotn" + conn.getResponseCode());
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();

		}
		System.out.println("priting responce");
		System.out.println(response.toString() + "~~~~~~~~~~~~~~~~~~~~~~~");
		return response.toString();
	}

	public StringBuilder callGetService(String urlStr, String[] paramName,
			String[] paramValue) {
		URL url;
		StringBuilder requestString = new StringBuilder(urlStr);
		StringBuilder sb = new StringBuilder();
		InputStream stream = null;
		try {
			url = new URL(requestString.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			stream = conn.getInputStream();
			BufferedReader rd = new BufferedReader(
					new InputStreamReader(stream));

			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
			conn.disconnect();
			conn.disconnect();
		} catch (Exception e) {
			return sb.append(e.getMessage());

		}
		return sb;
	}

	public HashMap<String, Object> alibaba(String url) {
		HashMap<String, Object> obj = new HashMap<String, Object>();
		try {

			ArrayList desc = new ArrayList();
			ArrayList pic = new ArrayList();
			StringBuilder sb = this.callGetService(url);
			obj.put("respo", sb.toString());
			Document doc = Jsoup.parse(sb.toString());
			// Document doc = Jsoup.parse(this.readFile().toString());
			// obj.put("respo", sb.toString());
			String title = doc.body().getElementsByClass("title-text").get(0)
					.text();
			obj.put("title", title);
			obj.put("stage", "1");
			Element hrml = doc.body().getElementById("J-quick-detail");
			if (hrml != null) {
				Document doc1 = Jsoup.parse("<html><body><table>" + hrml.html()
						+ "</table></body></html>");
				Element table = doc1.select("table").get(0); // select the first
																// table.
				Elements rows = table.select("tr");

				for (int i = 0; i < rows.size(); i++) { // first row is the col
														// names so skip it.

					Element row = rows.get(i);
					Elements cols = row.select("td");
					int o = 0;
					for (int p = 0; p < cols.size(); p++) {
						// System.out.println(cols.get(p).text() + ":" +
						// cols.get(++p).text());
						desc.add(cols.get(p).text() + ":"
								+ cols.get(++p).text());
					}
				}
			}
			obj.put("desc", desc);
			obj.put("stage", "2");
			Elements ele = doc.body().getElementsByClass("thumb");
			if (ele != null) {
				for (Element e : ele) {
					for (int i = 0; i < e.childNodes().size(); i++) {
						if (e.childNodes().get(i) != null
								&& !e.childNodes().get(i).toString().trim()
										.equals("")) {
							for (int k = 0; k < e.childNodes().get(i)
									.childNodeSize(); k++) {
								if (e.childNodes().get(i).childNodes().get(k) != null
										&& !e.childNodes().get(i).childNodes()
												.get(k).toString().trim()
												.equals("")) {
									// System.out.println(e.childNodes().get(i).childNodes().get(k));
									pic.add(e.childNodes().get(i).childNodes()
											.get(k));
								}
							}
						}
					}
				}
			}
			obj.put("pic", pic);
			obj.put("stage", "3");
			ArrayList breadcum = new ArrayList();
			Elements hj = doc.body().getElementsByClass("category");
			if (hj != null) {

				for (Element k : hj) {

					breadcum.add(k.text());
					// System.out.println(k.text());

				}
			}
			obj.put("breadcum", breadcum);
		} catch (Exception ex) {
			obj.put("error", ex.getMessage());
			return obj;
		}
		return obj;
	}

	public HashMap<String, Object> amazon(String url) {
		HashMap<String, Object> obj = new HashMap<String, Object>();
		try {

			ArrayList desc = new ArrayList();
			ArrayList pic = new ArrayList();

			// Document doc =
			// Jsoup.connect("http://www.amazon.com/Amazon-JK76PL-Dasani-Dash-Button/dp/B013CX0QZK?ie=UTF8&redirect=true&ref_=s9_hps_bw_g570_i3").get();
			StringBuilder sb = this.callGetService(url);
			Document doc = Jsoup.parse(sb.toString());
			// Document doc = Jsoup.parse(this.readFile().toString());
			String title = doc.body().getElementById("productTitle").text();
			obj.put("title", title);
			try {
				Element h = doc.body().getElementById(
						"detailBullets_feature_div");
				if (h != null) {
					List<Node> links2 = doc.body()
							.getElementById("detailBullets_feature_div")
							.childNodes();
					// System.out.println("test"+links2);
					for (int i = 0; i < links2.get(1).childNodes().size(); i++) {
						if (links2.get(1).childNodes().get(i).childNodes()
								.size() > 0) {
							int l = links2.get(1).childNodes().get(i)
									.childNodes().get(0).childNodes().size();
							for (int j = 0; j < l / 2; j++) {
								Node d = links2.get(1).childNodes().get(i)
										.childNodes().get(0).childNodes()
										.get(j);
								Node d1 = links2.get(1).childNodes().get(i)
										.childNodes().get(0).childNodes()
										.get(j + 2);
								if (d.childNodes().size() > 0
										&& d1.childNodes().size() > 0) {
									desc.add(d.childNodes().get(0) + ":"
											+ d1.childNodes().get(0));
								}
							}
						}
					}
				}
			} catch (Exception eX) {

			}
			obj.put("desc", desc);
			try {
				Elements ele = doc.body().getElementsByClass("a-button-text");
				if (ele != null) {
					for (Element e : ele) {
						if (e.childNodes().size() > 1
								&& e.childNodes().get(1).nodeName()
										.equals("img")) {
							pic.add(e.childNodes().get(1));
						}
					}
				}
			} catch (Exception e) {

			}
			Element er = doc.body().getElementById("landingImage");
			pic.add(er);
			obj.put("pic", pic);
			ArrayList breadcum = new ArrayList();
			Elements hj = doc.body().getElementsByClass("a-color-tertiary");
			if (hj != null) {
				for (Element k : hj) {
					if (k.toString().indexOf("a-link-normal") != -1) {
						breadcum.add(k.text());
						// System.out.println(k.text());
					}
				}
			}
			obj.put("breadcum", breadcum);
		} catch (Exception ex) {
			System.out.println("" + ex.getMessage());
			return null;
		}
		return obj;
	}

	public HashMap<String, Object> ebay(String url) {
		HashMap<String, Object> obj = new HashMap<String, Object>();
		try {

			ArrayList desc = new ArrayList();
			ArrayList pic = new ArrayList();
			StringBuilder sb = this.callGetService(url);
			Document doc = Jsoup.parse(sb.toString());
			// Document doc = Jsoup.parse(this.readFile().toString());
			String title = doc.body().getElementById("itemTitle").text();
			// System.out.println(""+title);
			obj.put("title", title);
			Elements links1 = doc.body().getElementsByClass("itemAttr");
			if (links1 != null) {
				for (Node e : links1.get(0).childNodes()) {
					if (e != null && !e.equals("")) {
						for (Node n : e.childNodes()) {
							if (n != null && !n.equals("")
									&& n.hasAttr("cellspacing")) {
								// System.out.println(n.childNode(1));
								Document doc1 = Jsoup
										.parse("<html><body><table>"
												+ n.childNode(1)
												+ "</table></body></html>");
								Element table = doc1.select("table").get(0); // select
																				// the
																				// first
																				// table.
								Elements rows = table.select("tr");
								// System.out.println(rows.size());
								for (int i = 0; i < rows.size(); i++) { // first
																		// row
																		// is
																		// the
																		// col
																		// names
																		// so
																		// skip
																		// it.
									Element row = rows.get(i);
									Elements cols = row.select("td");
									int o = 0;
									// for (int p = 0; p < cols.size() - 1; p++)
									// {
									for (int p = 0; p < cols.size(); p++) {
										// if (o < cols.size() - 2) {
										// desc.add(cols.get(p + o).text() + ":"
										// + cols.get(p + o + 1).text());
										desc.add(cols.get(p).text() + ":"
												+ cols.get(++p).text());
										// System.out.println(cols.get(p +
										// o).text() + ":" + cols.get(p + o +
										// 1).text());
										// o++;
										// }
									}
								}
							}
						}
					}
				}
			}
			obj.put("desc", desc);
			Elements el = doc.body().getElementsByClass("tdThumb");
			if (el != null) {

				for (Element im : el) {
					for (int k = 0; k < im.childNodeSize(); k++) {
						if (im.childNodes().get(k) != null
								&& !im.childNodes().get(k).equals("")) {
							for (int g = 0; g < im.childNodes().get(k)
									.childNodeSize(); g++) {
								if (im.childNodes().get(k).childNodes().get(g) != null
										&& !im.childNodes().get(k).childNodes()
												.get(g).toString().trim()
												.equals("")) {
									// System.out.println(im.childNodes().get(k).childNodes().get(g));
									pic.add(im.childNodes().get(k).childNodes()
											.get(g));
								}
							}
						}
					}
				}
			}
			// icImg
			Element e = doc.body().getElementById("icImg");
			pic.add(e.toString().replace("style=\"display:none;\"", ""));
			obj.put("pic", pic);
			ArrayList breadcum = new ArrayList();
			Elements hj = doc.body().getElementsByClass("bc-w");
			if (hj != null) {
				for (Element k : hj) {
					breadcum.add(k.childNodes().get(0).childNodes().get(0)
							.childNodes().get(0));
					// System.out.println(k.childNodes().get(0).childNodes().get(0).childNodes().get(0));

				}
			}
			obj.put("breadcum", breadcum);
		} catch (Exception ex) {
			System.out.println("" + ex.getMessage());
			return null;
		}
		return obj;
	}

	public HashMap<String, Object> bzlem(String url) {
		HashMap<String, Object> obj = new HashMap<String, Object>();
		try {
			Session session = repo.login(new SimpleCredentials("admin", "admin"
					.toCharArray()));
			String pid = url.substring(url.indexOf("=") + 1, url.length());
			javax.jcr.Node d = session.getRootNode().getNode("content")
					.getNode("product").getNode("products").getNode(pid);
			ArrayList desc = new ArrayList();
			ArrayList pic = new ArrayList();
			desc.add(d.getProperty("shortdescription").getString());

			if (d.hasNode("media") && d.getNode("media").hasNode("images")
					&& d.getNode("media").getNode("images").hasNodes()) {
				NodeIterator itr = d.getNode("media").getNode("images")
						.getNodes();
				// String imurl =
				// "<img style='width:50px;height:50px' src='http://prod.bizlem.io:8082/portal";
				String imurlcot = "";
				while (itr.hasNext()) {
					javax.jcr.Node i = itr.nextNode();
					pic.add("<img style='width:50px;height:50px' src='http://prod.bizlem.io:8082/portal"
							+ i.getProperty("imgpath").getString() + "'>");
				}
			}
			obj.put("title", d.getProperty("name").getString());
			obj.put("desc", desc);
			obj.put("pic", pic);
		} catch (Exception ex) {
			return null;
		}
		return obj;
	}

	public StringBuilder callGetService(String urlStr) {
		URL url;
		StringBuilder requestString = new StringBuilder(urlStr);
		StringBuilder sb = new StringBuilder();
		InputStream stream = null;
		try {
			url = new URL(requestString.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			stream = conn.getInputStream();
			BufferedReader rd = new BufferedReader(
					new InputStreamReader(stream));

			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
			conn.disconnect();
			// conn.disconnect();
		} catch (Exception e) {
			sb.append(e.getMessage());

		}
		return sb;
	}

	public String addSource(SlingHttpServletRequest request,
			SlingHttpServletResponse response) {
		String feedurl = request.getParameter("feedurl");
		String metadeta = request.getParameter("metadeta");
		String revivitetime = request.getParameter("revisitetime");
		String id = request.getParameter("sourceid");
		String dbresponce="";
		try {
			Session session = repo.login(new SimpleCredentials("admin", "admin"
					.toCharArray()));
			javax.jcr.Node content = session.getRootNode().getNode("content");
			if (content.hasNode("newsfeed")) {
				content = content.getNode("newsfeed");
			} else {
				content = content.addNode("newsfeed");
			}

			if (content.hasNode("sourcemaster")) {
				content = content.getNode("sourcemaster");
			} else {
				content = content.addNode("sourcemaster");
				content.setProperty("count", Long.valueOf("0"));
			}

			long l = content.getProperty("count").getLong();
			content.setProperty("count", l + 1);
			content = content.addNode(String.valueOf(l));
			content.setProperty("feedurl", feedurl);
			content.setProperty("metadeta", metadeta);
			content.setProperty("revivitetime", revivitetime);
			content.setProperty("id", l);
			content.setProperty("title", request.getParameter("sourcename"));
			content.setProperty("bizurl", "/content/newsfeed/sourcemaster/" + l
					+ "/" + l);
			response.getWriter().print("Stage 1 clear");

			InputStream streams = new ByteArrayInputStream(callGetService(feedurl, null, null).toString().getBytes());

			javax.jcr.Node fileNode = content.addNode(String.valueOf(l),
					"nt:file");

			javax.jcr.Node jcrNode = fileNode.addNode("jcr:content",
					"nt:resource");
			response.getWriter().print("Stage 2 clear");
			jcrNode.setProperty("jcr:data", streams);
			//
			response.getWriter().print("Stage 3 clear");
			jcrNode.setProperty("jcr:mimeType", "text/plain");
			response.getWriter().print("Stage 4 clear");
			dbresponce = addtoDb(request, String.valueOf(l),0);

			if (dbresponce.trim().equals("success")) {
				SimpleTriggerExample b = new SimpleTriggerExample();

				b.fireJob(String.valueOf(l), b.calculateTimeInSec(revivitetime));
				return dbresponce+"##"+l;
			} else {
				return "failed";
			}

		} catch (Exception e) {
			return "failed"+e.getMessage();
		}
		
	}
	
}