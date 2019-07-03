package biz.com.app.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import jxl.*;
import jxl.write.*;
import jxl.write.biff.RowsExceededException;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.http.HttpService;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import biz.com.service.EventService;
import biz.com.service.ProductService;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

@Component(immediate = true, metatype = false)
@Service(value = javax.servlet.Servlet.class)
@Properties({ @Property(name = "service.description", value = "Save product Servlet"),
		@Property(name = "service.vendor", value = "VISL Company"),
		@Property(name = "sling.servlet.paths", value = { "/servlet/service/productselection" }),
		@Property(name = "sling.servlet.resourceTypes", value = "sling/servlet/default"),
		@Property(name = "sling.servlet.extensions", value = { "hotproducts", "cat", "latestproducts", "brief",
				"prodlist", "catalog", "viewcart", "productslist", "addcart", "createproduct", "checkmodelno",
				"productEdit" }) })
@SuppressWarnings("serial")
public class ProductsServlet extends SlingAllMethodsServlet {

	@Reference
	private SlingRepository repos;

	final int NUMBEROFRESULTSPERPAGE = 10;

	@Reference
	private ProductService product;

	@Reference
	private EventService eventservice;

	ArrayList prodID = new ArrayList();

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {

		if (request.getRequestPathInfo().getExtension().equals("hotproducts")) {
			Node rootNode = null;
			Session session;
			PrintWriter o = response.getWriter();
			try {
				// String k=request.getParameter("idp");
				session = repos.login(new SimpleCredentials("admin", "admin".toCharArray()));

				// rootNode = session.getRootNode().getNode("content");
				rootNode = session.getRootNode().getNode("content");
				// PropertyIterator rootNodes =
				// session.getRootNode().getNode("content").getProperties();
				// javax.jcr.Property p=rootNodes.nextProperty();

				request.getRequestDispatcher("/content/products/.hotproducts").forward(request, response);
			} catch (Exception e) {
				o.print("Error" + e.getMessage());
			}

			// response.getOutputStream().println(request.getParameter("companyName"));

		} else if (request.getRequestPathInfo().getExtension().equals("verifyprod")) {
			String status = "";
			String producttnc;
			String pid = request.getParameter("pid");
			Session session;
			PrintWriter out = response.getWriter();

			Node rootnode;
			Node prod;
			try {
				session = repos.login(new SimpleCredentials("admin", "admin".toCharArray()));
				rootnode = session.getRootNode().getNode("content").getNode("product").getNode("products");
				if (rootnode.hasNode(pid) && rootnode.getNode(pid).hasProperty("producttnc")) {
					producttnc = rootnode.getNode(pid).getProperty("producttnc").getString();
					if (producttnc.equals("accept")) {
						status = "true";
					} else {
						prod = rootnode.getNode(pid);
						prod.setProperty("producttnc", "accept");
						status = "true";
					}

				} else {
					rootnode = session.getRootNode().getNode("content").getNode("product").getNode("products");
					prod = rootnode.getNode(pid);
					prod.setProperty("producttnc", "accept");
					status = "true";
				}
				session.save();
				out.print(status);
			} catch (Exception e) {
			}

		} else if (request.getRequestPathInfo().getExtension().equals("brief")) {
			Node rootNode = null;
			Session session;
			String productId;
			PrintWriter p = response.getWriter();

			try {
				productId = request.getParameter("pid");
				session = repos.login(new SimpleCredentials("admin", "admin".toCharArray()));

				boolean order = session.getRootNode().getNode("content").getNode("product").getNode("products")
						.hasNode(productId);
				if (order) {
					request.setAttribute("hotproductsearch", eventservice.getHotProductSearchText(request));
					request.setAttribute("hotsellersearch", eventservice.getHotSellerSearchText(request));
					// request.setAttribute("news",
					// eventservice.getnewsList(request));
					String url = "";
					// request.getRequestDispatcher("/content/products/.productbrief").forward(request,
					// response);
					// String product = request.getParameter("pid");
					// InputStream inputStream1 =
					// Thread.currentThread().getContextClassLoader().getResourceAsStream("ehcache.xml");
					// CacheManager manager =
					// CacheManager.newInstance(inputStream1);
					// Cache cache = manager.getCache("product");
					// Element element = cache.get(request.getParameter("pid"));
					// String url = "";
					// if(element == null)
					// {
					// System.out.println("step------- 2");
					// // out.print("cache miss");
					// url = "/content/products/.productbrief";
					// }
					// else
					// {
					// System.out.println("step------- 3");
					// String pageData = (String)element.getObjectValue();
					// request.setAttribute("page",pageData);
					// if(!response.isCommitted()){
					// url = "/content/products/.productbriefCache";
					// }else{
					// url = "/content/products/.productbriefCache";
					// }
					// }
					url = "/content/products/.productbrief";
					request.getRequestDispatcher(url).forward(request, response);
				} else {
					p.print("No such product found---404pagee");
				}
			} catch (Exception e) {
				p.print(e.getMessage());
				p.print("From add exception" + e.getCause());

				// request.getRequestDispatcher("/content/products/*.error")
				// /.forward(request, response);

			}
		} else if (request.getRequestPathInfo().getExtension().equals("catalog")) {
			Node rootNode = null;
			Session session;
			String productId;
			PrintWriter p = response.getWriter();

			try {
				productId = request.getParameter("pid");
				session = repos.login(new SimpleCredentials("admin", "admin".toCharArray()));

				rootNode = session.getRootNode().getNode("content");

				request.getRequestDispatcher("/content/products/.productcatalog").forward(request, response);

			} catch (Exception e) {
				p.print(e.getMessage());
				p.print("From add exception" + e.getCause());

				// request.getRequestDispatcher("/content/products/*.error")
				// /.forward(request, response);

			}
		} else if (request.getRequestPathInfo().getExtension().equals("productlist")) {

			String from = null;
			String to = null;
			int t, f;
			String search = request.getParameter("query");
			from = request.getParameter("from");
			to = from;
			request.setAttribute("total", product.getProductListByCatId(search).size());
			if (to != null && from != null) {
				try {
					t = Integer.parseInt(to);
					f = Integer.parseInt(from);
					f = (f - 1) * NUMBEROFRESULTSPERPAGE;
					t = t * NUMBEROFRESULTSPERPAGE;
					ArrayList<Node> m = product.getProductListByCatId(search);
					ArrayList<Node> alist = new ArrayList<Node>();
					if (m.size() > t) {
						for (int i = f; i < t; i++) {
							alist.add(m.get(i));
						}
					} else {
						for (int i = f; i < m.size(); i++) {
							alist.add(m.get(i));
						}
					}
					request.setAttribute("product", alist);
					request.getRequestDispatcher("/content/products/.propagination").forward(request, response);
				} catch (NumberFormatException e) {
					ArrayList<Node> m = product.getProductListByCatId(search);
					ArrayList<Node> alist = new ArrayList<Node>();
					if (m.size() > NUMBEROFRESULTSPERPAGE) {
						for (int i = 0; i < NUMBEROFRESULTSPERPAGE; i++) {
							alist.add(m.get(i));

						}
					} else {
						for (int i = 0; i < m.size(); i++) {
							alist.add(m.get(i));

						}
					}

					request.setAttribute("product", alist);
					request.getRequestDispatcher("/content/products/.propagination").forward(request, response);

				}

			} else {
				ArrayList<Node> m = product.getProductListByCatId(search);
				ArrayList<Node> alist = new ArrayList<Node>();
				if (m.size() > NUMBEROFRESULTSPERPAGE) {
					for (int i = 0; i < NUMBEROFRESULTSPERPAGE; i++) {
						alist.add(m.get(i));

					}
				} else {
					for (int i = 0; i < m.size(); i++) {
						alist.add(m.get(i));

					}
				}
				request.setAttribute("product", alist);
				request.setAttribute("hotproductsearch", eventservice.getHotProductSearchText(request));

				request.getRequestDispatcher("/content/products/.prodlist").forward(request, response);
			}

		} else if (request.getRequestPathInfo().getExtension().endsWith("samplenode")) {

			Node rootNode = null;
			Session session;

			PrintWriter p = response.getWriter();
			try {

				session = repos.login(new SimpleCredentials("admin", "admin".toCharArray()));

				Node f = session.getRootNode().getNode("content").getNode("news");
				for (int i = 0; i < 100; i++) {
					f.addNode("" + i);
				}
				NodeIterator itr = session.getRootNode().getNode("content").getNode("news").getNodes();
				int j = 0;
				while (itr.hasNext()) {
					Node n = itr.nextNode();
					n.setProperty("Title", "This is tilte testoin g new s" + j);
					n.setProperty("Auther", "A.K Mishra" + j);
					n.setProperty("Date", "30-06-2015" + j);
					n.setProperty("Link", "http://google.com/" + j);
					n.setProperty("Description",
							"Repellat quis doloribus pariatur, ducimus quisquam. Accusamus cum tempore, labore. Aut sequi ipsa eum, voluptatum quas, beatae blanditiis esse rem veritatis possimus repellat non harum sapiente quibusdam consequatur expedita recusandae."
									+ j);
					Node img = n.addNode("images");
					Node vid = n.addNode("videos");

					j++;
				}

				session.save();

			} catch (Exception e) {
				p.print(e.getMessage());
				p.print("From add exception" + e.getCause());

				// request.getRequestDispatcher("/content/products/*.error")
				// /.forward(request, response);

			}

		} else if (request.getRequestPathInfo().getExtension().endsWith("samplenode1")) {

			Node rootNode = null;
			Session session;

			PrintWriter p = response.getWriter();
			try {

				session = repos.login(new SimpleCredentials("admin", "admin".toCharArray()));

				NodeIterator itr = session.getRootNode().getNode("content").getNode("news").getNodes();
				int j = 0;
				while (itr.hasNext()) {
					if (j < 25) {
						itr.nextNode().setProperty("newsType", "0");
					} else if (j < 40) {
						itr.nextNode().setProperty("newsType", "1");
					} else if (j < 60) {
						itr.nextNode().setProperty("newsType", "2");
					} else if (j < 80) {
						itr.nextNode().setProperty("newsType", "3");
					} else if (j < 101) {
						itr.nextNode().setProperty("newsType", "4");
					}
					j++;
				}

				session.save();

			} catch (Exception e) {
				p.print(e.getMessage());
				p.print("From add exception" + e.getCause());

				// request.getRequestDispatcher("/content/products/*.error")
				// /.forward(request, response);

			}

		} else if (request.getRequestPathInfo().getExtension().endsWith("compareprod")) {

			request.getRequestDispatcher("/content/products/.compareproduct").forward(request, response);

		} else if (request.getRequestPathInfo().getExtension().endsWith("getQuote")) {
			Node rootNode = null;
			Session session;
			try {
				session = repos.login(new SimpleCredentials("admin", "admin".toCharArray()));
				String arr = request.getParameter("quoteproduct");
				// String[] pid;
				String[] pid = arr.split(",");
				JSONObject objMainJson = new JSONObject();
				JSONArray objArray = new JSONArray();
				JSONObject obj = null;
				for (int i = 0; i < pid.length; i++) {
					rootNode = session.getRootNode().getNode("content").getNode("product");
					if (rootNode.hasNode("products") && rootNode.getNode("products").hasNode(pid[i])) {

						Node productnode = rootNode.getNode("products").getNode(pid[i]);
						String companyid = "";
						String imgpath = "";
						String compname = "";
						String pname = rootNode.getNode("products").getNode(pid[i]).getProperty("name").getString();

						if (productnode.hasNode("sup") && productnode.getNode("sup").hasNodes()) {
							// companyid=productnode.getNode("sup").getNode("1").getProperty("supid").toString();
							NodeIterator itr = productnode.getNode("sup").getNodes();
							// int iCompCOunt = 0;
							while (itr.hasNext()) {
								// if(iCompCOunt == 0){
								companyid = itr.nextNode().getProperty("supid").getString();
								// iCompCOunt++;
								// }
								break;
							}
						}

						if (session.getRootNode().getNode("content").getNode("company").hasNode(companyid)) {

							Node comp = session.getRootNode().getNode("content").getNode("company").getNode(companyid);
							compname = comp.getProperty("title").getString();
						}

						if (productnode.hasNode("media") && productnode.getNode("media").hasNode("images")
								&& productnode.getNode("media").getNode("images").hasNodes()) {
							NodeIterator itr1 = productnode.getNode("media").getNode("images").getNodes();
							// int iImgCOunt = 0;
							while (itr1.hasNext()) {
								// if(iImgCOunt == 0){
								imgpath = itr1.nextNode().getProperty("imgpath").getString();
								// iImgCOunt++;
								// }
								break;
							}
						}
						// image=imgpath[1];
						obj = new JSONObject();
						obj.put("prodid", pid[i]);
						obj.put("prodname", pname);
						obj.put("prodimage", imgpath);
						obj.put("compid", companyid);
						obj.put("compname", compname);

						objArray.put(obj);

					}

				}
				objMainJson.put("data", objArray);
				response.getWriter().print(objMainJson.toString());
			} catch (Exception e) {
				response.getWriter().print("error====" + e.getMessage());
			}

			// request.getRequestDispatcher("/content/products/.compareproduct").forward(
			// request, response);

		} else if (request.getRequestPathInfo().getExtension().endsWith("sendMessage")) {
			Node rootNode = null;
			Session session;
			try {
				session = repos.login(new SimpleCredentials("admin", "admin".toCharArray()));
				String pid = request.getParameter("productid");
				String pname = request.getParameter("pname");
				String compid = request.getParameter("compid");
				String compname = null;

				JSONObject objMainJson = new JSONObject();
				JSONArray objArray = new JSONArray();
				JSONObject obj = null;
				if (pid != null && pid != "") {
					rootNode = session.getRootNode().getNode("content").getNode("product");
					if (rootNode.hasNode("products") && rootNode.getNode("products").hasNode(pid)) {
						Node productnode = rootNode.getNode("products").getNode(pid);
						
						if (productnode.hasNode("sup") && productnode.getNode("sup").hasNodes()) {
							// companyid=productnode.getNode("sup").getNode("1").getProperty("supid").toString();
							NodeIterator itr = productnode.getNode("sup").getNodes();
							// int iCompCOunt = 0;
							while (itr.hasNext()) {
								Node comp = session.getRootNode().getNode("content").getNode("company");
								if (comp.hasNode(compid)) {
									compname = comp.getNode(compid).getProperty("title").getString();

								}

								break;
							}
						}

						String imgpath = "";
						if (productnode.hasNode("media") && productnode.getNode("media").hasNode("images")
								&& productnode.getNode("media").getNode("images").hasNodes()) {
							NodeIterator itr1 = productnode.getNode("media").getNode("images").getNodes();
							// int iImgCOunt = 0;
							while (itr1.hasNext()) {
								// if(iImgCOunt == 0){
								imgpath = itr1.nextNode().getProperty("imgpath").getString();
								// iImgCOunt++;
								// }
								break;
							}
						}
						// image=imgpath[1];
						obj = new JSONObject();
						obj.put("prodid", pid);
						obj.put("prodname", pname);
						obj.put("prodimage", imgpath);
						obj.put("compid", compid);
						obj.put("compname", compname);
						objArray.put(obj);

					}

				}
				objMainJson.put("data", objArray);
				response.getWriter().print(objMainJson.toString());
			} catch (Exception e) {
				response.getWriter().print("error====" + e.getMessage());
			}

			// request.getRequestDispatcher("/content/products/.compareproduct").forward(
			// request, response);

		} else if (request.getRequestPathInfo().getExtension().endsWith("loadsttemplate")) {

			request.getRequestDispatcher("/content/products/.loadTableProductStandard").forward(request, response);

		} else if (request.getRequestPathInfo().getExtension().endsWith("createproduct")) {
			if (request.getAttribute("catalogpath") != null) {
				request.setAttribute("catalogpath", request.getAttribute("catalogpath"));
			} else {
				request.setAttribute("catalogpath", request.getParameter("id"));
			}
			if (request.getSession(false).getAttribute("error") != null) {
				request.setAttribute("error", request.getSession(false).getAttribute("error"));
			} else {
				request.setAttribute("error", "no session");
			}

			if (request.getSession(false) != null) {
				// request.setAttribute("fileError",(String)request.getSession().getAttribute("fileError"));

				request.getRequestDispatcher("/content/products/.createProductNew").forward(request, response);
			} else {
				request.getRequestDispatcher("/content/products/.createProductNew").forward(request, response);
			}

		} else if (request.getRequestPathInfo().getExtension().endsWith("viewcart")) {

			request.getRequestDispatcher("/content/products/.shoppingcart").forward(request, response);

		} else if (request.getRequestPathInfo().getExtension().equals("getTypeInfo")) {
			Node rootNode = null;
			Session session;

			// PrintWriter p = response.getWriter();
			try {

				session = repos.login(new SimpleCredentials("admin", "admin".toCharArray()));

				if (session.getRootNode().getNode("content").getNode("attribute").getNode("type")
						.hasNode(request.getParameter("typeId"))) {
					// String
					// labelname=session.getRootNode().getNode("content").getNode("attribute").getNode("type").getNode(request.getParameter("typeId")).getNodes();
					Node label = session.getRootNode().getNode("content").getNode("attribute").getNode("label")
							.getNode(request.getParameter("labelId"));
					String res = "";
					NodeIterator itr = session.getRootNode().getNode("content").getNode("attribute").getNode("type")
							.getNode(request.getParameter("typeId")).getNodes();
					// int j=0;
					while (itr.hasNext()) {
						String a = itr.nextNode().getProperty("placeholder").getString();
						String labelName = label.getProperty("name").getString();
						String result = labelName + "_" + a;
						// .getProperty("placeholder").getString();
						res = res + "<div class=''><input type='text' required class='form-control' name='" + result
								+ "' placeholder='" + a + "'></div>";
					}

					response.getWriter().print(res);

				}

			} catch (Exception e) {
				// p.print(e.getMessage());
				// p.print("From add exception" + e.getCause());

				// request.getRequestDispatcher("/content/products/*.error")
				// /.forward(request, response);

			}

		} else if (request.getRequestPathInfo().getExtension().equals("productslist")) {
			try {

				Node prodNode = null;
				Session session;

				String from = null;
				String to = null;
				int t, f;
				from = request.getParameter("from");
				to = from;

				if (to != null && from != null) {
					try {
						t = Integer.parseInt(to);
						f = Integer.parseInt(from);
						f = (f - 1) * NUMBEROFRESULTSPERPAGE;
						t = t * NUMBEROFRESULTSPERPAGE;
						Node tempCmpNode = null;
						ArrayList<Node> m = new ArrayList<Node>();
						session = repos.login(new SimpleCredentials("admin", "admin".toCharArray()));
						prodNode = session.getRootNode().getNode("content").getNode("product").getNode("products");
						if (prodNode.hasNodes()) {
							NodeIterator iterator = prodNode.getNodes();
							while (iterator.hasNext()) {
								tempCmpNode = iterator.nextNode();
								m.add(tempCmpNode);

							}
						}
						request.setAttribute("total", m.size());
						ArrayList<Node> alist = new ArrayList<Node>();
						if (m.size() > t) {
							for (int i = f; i < t; i++) {
								alist.add(m.get(i));
							}
						} else {
							for (int i = f; i < m.size(); i++) {
								alist.add(m.get(i));
							}
						}
						request.setAttribute("companylist", alist);
						request.getRequestDispatcher("/content/company/.companyListDiv").forward(request, response);
					} catch (NumberFormatException e) {
						Node tempCmpNode = null;
						ArrayList<Node> m = new ArrayList<Node>();
						session = repos.login(new SimpleCredentials("admin", "admin".toCharArray()));
						prodNode = session.getRootNode().getNode("content").getNode("product").getNode("products");
						if (prodNode.hasNodes()) {
							NodeIterator iterator = prodNode.getNodes();
							while (iterator.hasNext()) {
								tempCmpNode = iterator.nextNode();
								m.add(tempCmpNode);

							}
						}

						request.setAttribute("total", m.size());
						ArrayList<Node> alist = new ArrayList<Node>();
						if (m.size() > NUMBEROFRESULTSPERPAGE) {
							for (int i = 0; i < NUMBEROFRESULTSPERPAGE; i++) {
								alist.add(m.get(i));

							}
						} else {
							for (int i = 0; i < m.size(); i++) {
								alist.add(m.get(i));

							}
						}

						request.setAttribute("companylist", alist);
						request.getRequestDispatcher("/content/company/.companyListDiv").forward(request, response);
					}

				} else {
					Node tempCmpNode = null;
					ArrayList<Node> m = new ArrayList<Node>();
					session = repos.login(new SimpleCredentials("admin", "admin".toCharArray()));
					prodNode = session.getRootNode().getNode("content").getNode("product").getNode("products");
					if (prodNode.hasNodes()) {
						NodeIterator iterator = prodNode.getNodes();
						while (iterator.hasNext()) {
							tempCmpNode = iterator.nextNode();
							m.add(tempCmpNode);
						}
					}

					request.setAttribute("total", m.size());
					ArrayList<Node> alist = new ArrayList<Node>();
					if (m.size() > NUMBEROFRESULTSPERPAGE) {
						for (int i = 0; i < NUMBEROFRESULTSPERPAGE; i++) {
							alist.add(m.get(i));

						}
					} else {
						for (int i = 0; i < m.size(); i++) {
							alist.add(m.get(i));

						}
					}
					request.setAttribute("productslist", alist);
					request.getRequestDispatcher("/content/products/.prodlistcart").forward(request, response);
				}

			} catch (Exception e) {

			}

		} else if (request.getRequestPathInfo().getExtension().equals("addcart")) {
			String strProductId = request.getParameter("productid");
			if (prodID.contains(strProductId)) {
				response.getWriter().print("false");
			} else {
				prodID.add(strProductId);

				request.getSession().setAttribute("productIdList", prodID);
				response.getWriter().print("true");
			}

		} else if (request.getRequestPathInfo().getExtension().equals("checkout")) {

			ArrayList productIdLis = (ArrayList) request.getSession().getAttribute("productIdList");
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < productIdLis.size(); i++) {
				sb.append(productIdLis.get(i));
			}
			response.getWriter().print(sb);
		} else if (request.getRequestPathInfo().getExtension().equals("removefromcart")) {
			String strProductId1 = request.getParameter("prdId");
			String r = "";
			ArrayList productIdLis1 = (ArrayList) request.getSession().getAttribute("productIdList");
			StringBuffer sb1 = new StringBuffer();
			for (int i = 0; i < productIdLis1.size(); i++) {
				if (strProductId1.equals(productIdLis1.get(i))) {
					boolean result = prodID.remove(prodID.get(i));
					request.getSession().setAttribute("productIdList", prodID);
				}
			}

			for (int i = 0; i < productIdLis1.size(); i++) {
				if (strProductId1.equals(productIdLis1.get(i))) {

				} else {
					sb1.append(productIdLis1.get(i));
				}
			}

			response.getWriter().print(sb1);
		} else if (request.getRequestPathInfo().getExtension().endsWith("addCartInfo")) {

			request.getRequestDispatcher("/content/user/.customerVerification").forward(request, response);

		} else if (request.getRequestPathInfo().getExtension().equals("companyproductlst")) {
			try {

				Node prodNode = null;
				Node companyNode = null;
				Session session;
				String companyName = null;
				String from = null;
				String to = null;
				int t, f;
				companyName = request.getParameter("compN");
				from = request.getParameter("from");
				to = from;

				if (to != null && from != null) {
					try {
						t = Integer.parseInt(to);
						f = Integer.parseInt(from);
						f = (f - 1) * NUMBEROFRESULTSPERPAGE;
						t = t * NUMBEROFRESULTSPERPAGE;
						Node tempCmpNode = null;
						ArrayList<Node> m = new ArrayList<Node>();
						session = repos.login(new SimpleCredentials("admin", "admin".toCharArray()));
						companyNode = session.getRootNode().getNode("content").getNode("company").getNode(companyName);
						// prodNode =
						// session.getRootNode().getNode("content").getNode("product").getNode("products");
						if (companyNode.hasNode("product")) {
							prodNode = companyNode.getNode("product");
							if (prodNode.hasNodes()) {
								NodeIterator iterator = prodNode.getNodes();
								while (iterator.hasNext()) {
									tempCmpNode = iterator.nextNode();
									m.add(tempCmpNode);
								}
							}
						}
						request.setAttribute("total", m.size());
						ArrayList<Node> alist = new ArrayList<Node>();
						if (m.size() > t) {
							for (int i = f; i < t; i++) {
								alist.add(m.get(i));
							}
						} else {
							for (int i = f; i < m.size(); i++) {
								alist.add(m.get(i));
							}
						}
						request.setAttribute("productslist", alist);
						request.getRequestDispatcher("/content/company/.paginationcompanyProdView").forward(request,
								response);
					} catch (NumberFormatException e) {
						Node tempCmpNode = null;
						ArrayList<Node> m = new ArrayList<Node>();
						session = repos.login(new SimpleCredentials("admin", "admin".toCharArray()));
						companyNode = session.getRootNode().getNode("content").getNode("company").getNode(companyName);
						// prodNode =
						// session.getRootNode().getNode("content").getNode("product").getNode("products");
						if (companyNode.hasNode("product")) {
							prodNode = companyNode.getNode("product");
							if (prodNode.hasNodes()) {
								NodeIterator iterator = prodNode.getNodes();
								while (iterator.hasNext()) {
									tempCmpNode = iterator.nextNode();
									m.add(tempCmpNode);
								}
							}
						}

						request.setAttribute("total", m.size());
						ArrayList<Node> alist = new ArrayList<Node>();
						if (m.size() > NUMBEROFRESULTSPERPAGE) {
							for (int i = 0; i < NUMBEROFRESULTSPERPAGE; i++) {
								alist.add(m.get(i));

							}
						} else {
							for (int i = 0; i < m.size(); i++) {
								alist.add(m.get(i));

							}
						}

						request.setAttribute("productslist", alist);
						request.getRequestDispatcher("/content/company/.paginationcompanyProdView").forward(request,
								response);
					}

				} else {
					Node tempCmpNode = null;
					ArrayList<Node> m = new ArrayList<Node>();
					session = repos.login(new SimpleCredentials("admin", "admin".toCharArray()));
					companyNode = session.getRootNode().getNode("content").getNode("company").getNode(companyName);
					// prodNode =
					// session.getRootNode().getNode("content").getNode("product").getNode("products");
					if (companyNode.hasNode("product")) {
						prodNode = companyNode.getNode("product");
						if (prodNode.hasNodes()) {
							NodeIterator iterator = prodNode.getNodes();
							while (iterator.hasNext()) {
								tempCmpNode = iterator.nextNode();
								m.add(tempCmpNode);
							}
						}
					}

					request.setAttribute("total", m.size());
					ArrayList<Node> alist = new ArrayList<Node>();
					if (m.size() > NUMBEROFRESULTSPERPAGE) {
						for (int i = 0; i < NUMBEROFRESULTSPERPAGE; i++) {
							alist.add(m.get(i));

						}
					} else {
						for (int i = 0; i < m.size(); i++) {
							alist.add(m.get(i));

						}
					}
					request.setAttribute("productslist", alist);
					request.getRequestDispatcher("/content/company/.companyProdView").forward(request, response);
				}

			} catch (Exception e) {

			}

		} else if (request.getRequestPathInfo().getExtension().equals("companymanagementlst")) {
			try {

				Node prodNode = null;
				Node companyNode = null;
				Session session;
				String companyName = null;
				String from = null;
				String to = null;
				int t, f;
				companyName = request.getParameter("compN");
				from = request.getParameter("from");
				to = from;

				if (to != null && from != null) {
					try {
						t = Integer.parseInt(to);
						f = Integer.parseInt(from);
						f = (f - 1) * NUMBEROFRESULTSPERPAGE;
						t = t * NUMBEROFRESULTSPERPAGE;
						Node tempCmpNode = null;
						ArrayList<Node> m = new ArrayList<Node>();
						session = repos.login(new SimpleCredentials("admin", "admin".toCharArray()));
						companyNode = session.getRootNode().getNode("content").getNode("company").getNode(companyName);
						// prodNode =
						// session.getRootNode().getNode("content").getNode("product").getNode("products");
						if (companyNode.hasNode("managementteam")) {
							prodNode = companyNode.getNode("managementteam");
							if (prodNode.hasNodes()) {
								NodeIterator iterator = prodNode.getNodes();
								while (iterator.hasNext()) {
									tempCmpNode = iterator.nextNode();
									m.add(tempCmpNode);
								}
							}
						}
						request.setAttribute("total", m.size());
						ArrayList<Node> alist = new ArrayList<Node>();
						if (m.size() > t) {
							for (int i = f; i < t; i++) {
								alist.add(m.get(i));
							}
						} else {
							for (int i = f; i < m.size(); i++) {
								alist.add(m.get(i));
							}
						}
						request.setAttribute("productslist", alist);
						request.getRequestDispatcher("/content/user/.paginationmanagementProdView").forward(request,
								response);
					} catch (NumberFormatException e) {
						Node tempCmpNode = null;
						ArrayList<Node> m = new ArrayList<Node>();
						session = repos.login(new SimpleCredentials("admin", "admin".toCharArray()));
						companyNode = session.getRootNode().getNode("content").getNode("company").getNode(companyName);
						// prodNode =
						// session.getRootNode().getNode("content").getNode("product").getNode("products");
						if (companyNode.hasNode("managementteam")) {
							prodNode = companyNode.getNode("managementteam");
							if (prodNode.hasNodes()) {
								NodeIterator iterator = prodNode.getNodes();
								while (iterator.hasNext()) {
									tempCmpNode = iterator.nextNode();
									m.add(tempCmpNode);
								}
							}
						}

						request.setAttribute("total", m.size());
						ArrayList<Node> alist = new ArrayList<Node>();
						if (m.size() > NUMBEROFRESULTSPERPAGE) {
							for (int i = 0; i < NUMBEROFRESULTSPERPAGE; i++) {
								alist.add(m.get(i));

							}
						} else {
							for (int i = 0; i < m.size(); i++) {
								alist.add(m.get(i));

							}
						}

						request.setAttribute("productslist", alist);
						request.getRequestDispatcher("/content/user/.paginationmanagementProdView").forward(request,
								response);
					}

				} else {
					Node tempCmpNode = null;
					ArrayList<Node> m = new ArrayList<Node>();
					session = repos.login(new SimpleCredentials("admin", "admin".toCharArray()));
					companyNode = session.getRootNode().getNode("content").getNode("company").getNode(companyName);
					// prodNode =
					// session.getRootNode().getNode("content").getNode("product").getNode("products");
					if (companyNode.hasNode("managementteam")) {
						prodNode = companyNode.getNode("managementteam");
						if (prodNode.hasNodes()) {
							NodeIterator iterator = prodNode.getNodes();
							while (iterator.hasNext()) {
								tempCmpNode = iterator.nextNode();
								m.add(tempCmpNode);
							}
						}
					}

					request.setAttribute("total", m.size());
					ArrayList<Node> alist = new ArrayList<Node>();
					if (m.size() > NUMBEROFRESULTSPERPAGE) {
						for (int i = 0; i < NUMBEROFRESULTSPERPAGE; i++) {
							alist.add(m.get(i));

						}
					} else {
						for (int i = 0; i < m.size(); i++) {
							alist.add(m.get(i));

						}
					}
					request.setAttribute("productslist", alist);
					request.getRequestDispatcher("/content/user/.managementProdView").forward(request, response);
				}

			} catch (Exception e) {

			}

		} else if (request.getRequestPathInfo().getExtension().equals("cat")) {
			try {

				String search_key = request.getParameter("cat");
				String currNode = "";
				Session session;

				session = repos.login(new SimpleCredentials("admin", "admin".toCharArray()));

				if (session.getRootNode().getNode("content").hasNode("category")) {
					currNode = "/content/unspsc/";
				}

				HashMap getList = (HashMap) product.getCategoryByNode(currNode, search_key, request, response);

				PrintWriter out = response.getWriter();

				/*
				 * out.println(getList.size()); out.println(currNode);
				 * out.print(search_key);
				 */
				// Get a set of the entries
				Set set = getList.entrySet();
				// Get an iterator
				Iterator i = set.iterator();
				// Display elements
				out.print("<select id='cat' style='height:25px'>");
				if (!getList.isEmpty()) {
					while (i.hasNext()) {
						Map.Entry me = (Map.Entry) i.next();
						out.print("<option value='" + me.getKey() + "'>" + me.getValue() + "</option>");

					}
					out.print(
							"</select><input style='height:25px' type='button' value='Get Selected Value' onclick='getCategoryValue()'>");
				} else {
					out.print("No record found !");
				}
			} catch (Exception e) {
				PrintWriter out = response.getWriter();
				out.print(e.getMessage());
			}
		} else if (request.getRequestPathInfo().getExtension().equals("comp")) {
			try {

				String search_key = request.getParameter("userId");
				String currNode = "";
				Session session;

				session = repos.login(new SimpleCredentials("admin", "admin".toCharArray()));

				if (session.getRootNode().getNode("content").hasNode("company")) {
					currNode = "/content/company/";
				}

				HashMap getList = (HashMap) product.getCompanyByNode(currNode, search_key, request, response);

				PrintWriter out = response.getWriter();

				/*
				 * out.println(getList.size()); out.println(currNode);
				 * out.print(search_key);
				 */
				// Get a set of the entries
				Set set = getList.entrySet();
				// Get an iterator
				Iterator i = set.iterator();
				// Display elements

				if (!getList.isEmpty()) {
					if (set.size() > 1) {
						out.print("<select class='form-control' id='companyId' name='companyId' style='height:30px'>");
						// out.print("<select readonly class='form-control'
						// id='companyId' name='companyId'
						// style='height:30px'>");
					} else {
						out.print(
								"<select readonly class='form-control' id='companyId' name='companyId' style='height:30px'>");
					}
					while (i.hasNext()) {
						Map.Entry me = (Map.Entry) i.next();
						if (request.getParameter("vsupid").equals("new")) {
							out.print("<option value='" + me.getKey() + "'>" + me.getValue() + "</option>");
						} else {
							if (request.getParameter("vtitle").equals(me.getValue())) {
								out.print(
										"<option selected value='" + me.getKey() + "'>" + me.getValue() + "</option>");
							} else {
								out.print("<option value='" + me.getKey() + "'>" + me.getValue() + "</option>");
							}
						}
					}
					out.print("</select>");

					// out.print("</select><input style='height:25px'
					// type='button' value='Get Selected Value'
					// onclick='getCategoryValue()'>");
				} else {
					out.print("<b>Company Not found. please create company !</b>");
				}
			} catch (Exception e) {
				PrintWriter out = response.getWriter();
				out.print(e.getMessage());
			}
		} else if (request.getRequestPathInfo().getExtension().endsWith("addproduct")) {
			if (request.getSession(false) != null) {
				request.setAttribute("fileError", (String) request.getSession().getAttribute("fileError"));
				request.getRequestDispatcher("/content/products/.createProduct1").forward(request, response);

			} else {
				request.getRequestDispatcher("/content/products/.createProduct1").forward(request, response);

			}
		} else if (request.getRequestPathInfo().getExtension().equals("downloadtemp")) {
			String prodid = request.getParameter("id");
			try {
				product.createTemplate(request, response, prodid);
			} catch (Exception e) {
				response.getWriter().print("-----------" + e.getMessage());
			}
		} else if (request.getRequestPathInfo().getExtension().equals("viewuploades")) {

			request.setAttribute("products", (ArrayList<String>) request.getSession().getAttribute("products"));
			request.getRequestDispatcher("/content/products/.uploadExtraFiles").forward(request, response);
		} else if (request.getRequestPathInfo().getExtension().endsWith("checkmodelno")) {
			String strCompanyId = request.getParameter("companyId");
			String strModelNo = request.getParameter("modelno");
			String result = product.checkModelExistence(strCompanyId, strModelNo);
			response.getWriter().print(result);

		} else if (request.getRequestPathInfo().getExtension().equals("viewMasterCountry")) {
			request.getRequestDispatcher("/content/products/.uploadCountry").forward(request, response);
		} else if (request.getRequestPathInfo().getExtension().equals("productEdit")) {
			if (request.getRemoteUser() != null && !request.getRemoteUser().equals("anonymous")) {

				request.getRequestDispatcher("/content/products/.editProduct").forward(request, response);
			} else {
				response.getWriter().print("----------");

			}
		} else if (request.getRequestPathInfo().getExtension().equals("myproduct")) {
			if (request.getRemoteUser() != null && !request.getRemoteUser().equals("anonymous")) {

				HashMap getList = (HashMap) product.getCompanyByNode("/content/company/", request.getRemoteUser(),
						request, response);
				Set set = getList.entrySet();
				try {
					Session session = repos.login(new SimpleCredentials("admin", "admin".toCharArray()));

					Iterator i = set.iterator();
					HashMap sm = new HashMap();
					ArrayList<Node> al = new ArrayList<Node>();
					while (i.hasNext()) {
						Map.Entry me = (Map.Entry) i.next();
						Node com = session.getRootNode().getNode("content").getNode("company")
								.getNode(me.getKey().toString());
						if (com.hasNode("product") && com.getNode("product").hasNodes()) {
							NodeIterator it = com.getNode("product").getNodes();

							while (it.hasNext()) {
								sm.put(it.nextNode().getName(), "");
								// al.add(session.getRootNode().getNode("content").getNode("product").getNode("products").getNode(it.nextNode().getName()));

							}
						}

					}
					Set p = sm.entrySet();
					Iterator itr = p.iterator();
					while (itr.hasNext()) {
						Map.Entry me = (Map.Entry) itr.next();
						al.add(session.getRootNode().getNode("content").getNode("product").getNode("products")
								.getNode(me.getKey().toString()));
					}
					request.setAttribute("product", al);
					request.getRequestDispatcher("/content/products/.myproductList").forward(request, response);
				} catch (LoginException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RepositoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				response.getWriter().print("----------");

			}
		}

	}

	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		if (request.getRequestPathInfo().getExtension().equals("save")) {
			// response.getWriter().print("vzvdzvdssss");
			try {

				// changes made for different tab
				// String[] attrValue = request.getParameterValues("label");
				// String[] freeattrValue =
				// request.getParameterValues("txtheading");
				// String compId = request.getParameter("companyId");
				Long n = product.saveProductInfo(request, response);
				// product.createTemplate(request,response,n);
				// response.getWriter().print("end---"+n);
				// response.sendRedirect(request.getContextPath()
				// + "/servlet/service/productselection.addproduct?pid=" + n);
				response.sendRedirect(request.getContextPath() + "/servlet/service/productselection.createproduct?id="
						+ n + "&param=edit&tab=2&catalogpath=" + n);
			} catch (Exception e) {

			}
		} else if (request.getRequestPathInfo().getExtension().equals("sendMailRfqSeller")) {
			// response.getWriter().print("vzvdzvdssss");
			try {
				String value[] = new String[3];
				value[0] = request.getParameter("email");
				
				value[1] = request.getParameter("msg");
				
				value[2] = request.getParameter("subject");
				String result = product.sendEmailToken(value);

				response.getWriter().print(result);
			} catch (Exception e) {

			}
		} else if (request.getRequestPathInfo().getExtension().endsWith("saveMessage")) {
			Node rootNode = null;
			Node currentNode = null;
			Session session;
			try {

				PrintWriter out = response.getWriter();
				session = repos.login(new SimpleCredentials("admin", "admin".toCharArray()));
				Node userNode = null;
				String contactNo = "", userName = "";
				Node node1 = session.getRootNode().getNode("content").getNode("user");

				String core = request.getParameter("core");
				String search = request.getParameter("search");
				String compid = request.getParameter("compid");
				String compname = request.getParameter("compname");
				String pid = request.getParameter("pid");
				String pname = request.getParameter("pname");
				String pimage = request.getParameter("image");
				// String pquantity = request.getParameter("rfqQuantity");
				String pdescription = request.getParameter("comment");
				String email = request.getParameter("email");

				// check if user exist

				// ResourceBundle bundle =
				// ResourceBundle.getBundle("serverConfig");bundle.getString("userServiceUrl")
				String serviceUrl = "http://www.prod.bizlem.io:8180/UserValidation";
				String url = "";
				InputStream inputXml = null;
				url = serviceUrl + "/services/UserValidation/raveUserExistence?userId=" + email;
				inputXml = new URL(url).openConnection().getInputStream();

				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(inputXml);
				doc.getDocumentElement().normalize();
				NodeList nList1 = doc.getElementsByTagName("ns:raveUserExistenceResponse");
				org.w3c.dom.Node nNode = nList1.item(0);
				org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode;
				String userresult = eElement.getElementsByTagName("ns:return").item(0).getTextContent();

				if (node1.hasNode(email.replace("@", "_"))) {
					userNode = node1.getNode(email.replace("@", "_"));

					// userName = userNode.getProperty("name").getString();
				}

				// add date time of creation of each node in rfq

				rootNode = session.getRootNode().getNode("content").getNode("company");
				Node rfqNode = null;
				long messagecount1;
				if (rootNode.hasNode(compid)) {
					Node compnode = rootNode.getNode(compid);
					if (compnode.hasNode("sendMessage")) {
						Node sendMessagenode = compnode.getNode("sendMessage");
						long messagecount = sendMessagenode.getProperty("messagecount").getLong();
						messagecount1 = messagecount + 1;
						Node messageNode = sendMessagenode.addNode(String.valueOf(messagecount1));
						sendMessagenode.setProperty("messagecount", messagecount1);
						messageNode.setProperty("userid", request.getRemoteUser());
						messageNode.setProperty("status", "new");
						SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");
						Date rfqdate = new Date();
						messageNode.setProperty("CreationDate", df.format(rfqdate).toString());
						messageNode.setProperty("messageno", messagecount1);

						messageNode.setProperty("productid", pid);
						messageNode.setProperty("productName", pname);
						messageNode.setProperty("productImage", pimage);
						messageNode.setProperty("senderemailid", email);
						messageNode.setProperty("productDescription", pdescription);
					} else {
						Node sendMessagenode = compnode.addNode("sendMessage");
						sendMessagenode.setProperty("messagecount", 1);
						Node messageNode = sendMessagenode.addNode("1");

						messageNode.setProperty("userid", request.getRemoteUser());
						messageNode.setProperty("status", "new");
						SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");
						Date rfqdate = new Date();
						messageNode.setProperty("CreationDate", df.format(rfqdate).toString());
						messageNode.setProperty("messageno", 1);

						messageNode.setProperty("productid", pid);
						messageNode.setProperty("productName", pname);
						messageNode.setProperty("productImage", pimage);
						messageNode.setProperty("senderemailid", email);
						messageNode.setProperty("productDescription", pdescription);
					}
					session.save();

					String[] checkparamKey = { "email", "supId", "msg", "name", "company", "productname",
							"productquantity", "pid" };
					String[] checkparamValue = { email, compid, pdescription.replaceAll(" ", "%20"), "",
							compname.replaceAll(" ", "%20"), pname.replaceAll(" ", "%20"), "", pid };
					// out.print(checkparamKey+"~~~~~~~~~~"+checkparamValue);
					String res = product.callGetService("http://prod.bizlem.io:8082/portal/servlet/service/event.sendMail",
							checkparamKey, checkparamValue);

				}

				response.sendRedirect("http://prod.bizlem.io:8082/portal/servlet/service/globalsearch.process?core=" + core
						+ "&search=" + search);

			} catch (Exception e) {
				PrintWriter out = response.getWriter();
				out.print(e.getMessage());
				e.printStackTrace();

			}

		} else if (request.getRequestPathInfo().getExtension().endsWith("saveQuote")) {
			Node rootNode = null;
			Node currentNode = null;
			Session session;
			try {

				PrintWriter out = response.getWriter();
				session = repos.login(new SimpleCredentials("admin", "admin".toCharArray()));
				// out.print(request.getRemoteUser());
				Node userNode = null;
				String contactNo = "", userName = "";
				Node node1 = session.getRootNode().getNode("content").getNode("user");
				// out.print(node1);
				// out.print("------");

				if (node1.hasNode(request.getRemoteUser().replace("@", "_"))) {
					// out.print("1");
					userNode = node1.getNode(request.getRemoteUser().replace("@", "_"));

					// out.print(userNode);
					userName = userNode.getProperty("name").getString();
					// contactNo=userNode.getProperty("number").getString();
				}

				// String core = request.getParameter("core");
				// String search = request.getParameter("search");
				String[] compid = request.getParameterValues("compid");
				String[] pid = request.getParameterValues("prodid");
				String[] pname = request.getParameterValues("pname");
				String[] pimage = request.getParameterValues("image");
				String[] pquantity = request.getParameterValues("rfqQuantity");
				String[] pdescription = request.getParameterValues("rfqMsgContent");
				// add date time of creation of each node in rfq

				rootNode = session.getRootNode().getNode("content").getNode("company");
				Node rfqNode = null;
				String tempComp[] = new String[compid.length];
				// String tempRFQCount = "";
				int tempCompCount = 0;
				for (int i = 0; i < compid.length; i++) {
					if (rootNode.hasNode(compid[i])) {

						currentNode = rootNode.getNode(compid[i]);
						if (currentNode.hasNode("Rfq")) {
							rfqNode = currentNode.getNode("Rfq");
							Node workNode = null;
							Node pNode = null;
							long count = rfqNode.getProperty("rfqcount").getLong();
							// Convert String Array to List
							List<String> list = Arrays.asList(tempComp);

							if (list.contains(compid[i])) {
								workNode = rfqNode.getNode(String.valueOf(count));
								pNode = workNode.addNode(pid[i]);
							} else {
								// long c=Long.parseLong(count);
								long count1 = count + 1;
								workNode = rfqNode.addNode(String.valueOf(count1));
								rfqNode.setProperty("rfqcount", count1);
								workNode.setProperty("userid", request.getRemoteUser());
								workNode.setProperty("status", "new");
								SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");
								Date rfqdate = new Date();
								workNode.setProperty("CreationDate", df.format(rfqdate).toString());
								workNode.setProperty("rfqNo", String.valueOf(count1));
								pNode = workNode.addNode(pid[i]);
								tempComp[tempCompCount] = compid[i];
							}
							// session.save();
							// Node workNode =
							// rfqNode.getNode(String.valueOf(count1));

							// session.save();
							// Node pNode=workNode.getNode(pid[i]);
							pNode.setProperty("productid", pid[i]);
							pNode.setProperty("productName", pname[i]);
							pNode.setProperty("productImage", pimage[i]);
							pNode.setProperty("productpQuantity", pquantity[i]);
							pNode.setProperty("productDescription", pdescription[i]);

						} else {
							rfqNode = currentNode.addNode("Rfq");
							rfqNode.setProperty("rfqcount", 1);

							Node workNode = rfqNode.addNode("1");
							// rfqNode.setProperty("rfqcount", count1);
							// session.save();
							// Node workNode =
							// rfqNode.getNode(String.valueOf(count1));
							workNode.setProperty("userid", request.getRemoteUser());
							workNode.setProperty("status", "new");
							SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");
							Date rfqdate = new Date();
							workNode.setProperty("CreationDate", df.format(rfqdate).toString());
							workNode.setProperty("rfqNo", "1");

							Node pNode = workNode.addNode(pid[i]);
							// session.save();
							// Node pNode=workNode.getNode(pid[i]);
							pNode.setProperty("productid", pid[i]);
							pNode.setProperty("productName", pname[i]);
							pNode.setProperty("productImage", pimage[i]);
							pNode.setProperty("productpQuantity", pquantity[i]);
							pNode.setProperty("productDescription", pdescription[i]);
							tempComp[tempCompCount] = compid[i];
						}
						tempCompCount++;

					}
					session.save();

				}
				String htmlText = null;

				// String htmldata[]=new String[tempComp.length];
				String htmldata = null;
				String compid1 = null;
				String pid1 = null;
				String pname1 = null;
				String pquantity1 = null;
				String pdescription1 = null;

				for (int j = 0; j < tempComp.length; j++) {

					if (tempComp[j] != null && tempComp[j] != "") {
						htmldata = "";
						Node comp = rootNode.getNode(tempComp[j]);

						String compcreator = comp.getNode("BasicInfo").getProperty("creatorEmailId").getString(); // basicinfo
																													// node
																													// and
																													// get
																													// creatorEmailId

						String cout = String.valueOf(comp.getNode("Rfq").getProperty("rfqcount").getLong());
						NodeIterator it = comp.getNode("Rfq").getNode(cout).getNodes();
						// int co=0;
						while (it.hasNext()) {

							Node tempNode = it.nextNode();
							pid1 = tempNode.getProperty("productid").getString();

							pname1 = tempNode.getProperty("productName").getString();

							pquantity1 = tempNode.getProperty("productpQuantity").getString();

							pdescription1 = tempNode.getProperty("productDescription").getString();

							/*
							 * if(tempNode.hasNode("attachment")&&tempNode.
							 * getNode("attachment").hasNodes()){ NodeIterator
							 * itr =tempNode.getNode("attachment").getNodes();
							 * while(itr.hasNext()) {
							 * attachment1[co]=itr.nextNode().getProperty(
							 * "docpath").getString(); break; }}
							 */
							// co++;
							htmldata = htmldata + "<p>Product Name : <strong>" + pname1 + "</strong></p><p>Quantity : "
									+ pquantity1 + "</p><p>Buyer Description : " + pdescription1
									+ "</p><p>Product Link : <a href='http://prod.bizlem.io:8082/portal/servlet/service/productselection.brief?pid="
									+ pid1
									+ "'>http://prod.bizlem.io:8082/portal/servlet/service/productselection.brief?pid="
									+ pid1 + "</a></p>";

						}

						htmlText = "<html><body><table width='100%' border='0' cellspacing='0' cellpadding='0' style='border-radius:0px '0px  10px 10px; border:1px solid #4096EE; background-color:#ffffff;'><tr><td align='center' valign='top'><table width='100%' border='0' align='center' cellpadding='5' cellspacing='5' style='border-top:6px solid #4096EE;'><tr><td align='left' valign='middle' style='font-family:Arial, Helvetica, sans-serif; color:#4e4e4e; font-size:13px; padding-right:10px;'><div style='font-size:24px; color:#4096EE;'>Dear "
								+ compcreator + ", </div><p>You have recieved a new frq for following Products</p><br/>"
								+ htmldata
								+ "<tr><td align='left' valign='middle' style='font-family:Arial, Helvetica, sans-serif; color:#4e4e4e; font-size:13px;'></tr><tr><td align='left' valign='middle' style='font-family:Arial, Helvetica, sans-serif; color:#333; font-size:13px;'><span style='color:#333; font-size:12px; font-family:Arial, Helvetica, sans-serif;'>With Regards,<br /><strong>Administrator</strong></span></tr></table></td></tr></table></body></html>";
						String subject = "Enquiry Received";
						String[] checkparamKey = { "email", "msg", "subject" };
						String[] checkparamValue = { compcreator, htmlText, subject };
						String res = product.callPostService(
								"http://prod.bizlem.io:8082/portal/servlet/service/productselection.sendMailRfqSeller",
								checkparamKey, checkparamValue);

					}
				}

				response.sendRedirect(request.getParameter("url"));

			} catch (Exception e) {
				PrintWriter out = response.getWriter();
				e.printStackTrace();

			}

		} else if (request.getRequestPathInfo().getExtension().endsWith("respondQuote")) {

			String compid = request.getParameter("rfq_compid");
			String rfqid = request.getParameter("rfq_noid");
			String limitRes = product.checkRfqResponseLimit(request, response);
			if (limitRes.equals("true")) {
				product.responseQuoteRfq(request, response);
				response.sendRedirect("http://prod.bizlem.io:8082/portal/servlet/service/globalsearch.rfqdata?Companyid="
						+ compid + "&rfqNo=" + rfqid);
			} else {
				response.sendRedirect("http://prod.bizlem.io:8082/portal/servlet/service/globalsearch.rfqdata?Companyid="
						+ compid + "&rfqNo=" + rfqid + "&limit=1");
			}

		} else if (request.getRequestPathInfo().getExtension().equals("saveother")) {
			// response.getWriter().print("vzvdzvdssss");
			try {

				// changes made for different tab
				// String[] attrValue = request.getParameterValues("label");
				// String[] freeattrValue =
				// request.getParameterValues("txtheading");
				// String compId = request.getParameter("companyId");
				Long n = product.saveProductOtherInfo(request, response);
				// product.createTemplate(request,response,n);
				// response.getWriter().print("end---"+n);
				// response.sendRedirect(request.getContextPath()
				// + "/servlet/service/productselection.addproduct?pid=" + n);
				response.sendRedirect(request.getContextPath() + "/servlet/service/productselection.createproduct?id="
						+ n + "&param=edit&catalogpath=" + n);
			} catch (Exception e) {

			}
		} else if (request.getRequestPathInfo().getExtension().equals("saveCatalog")) {
			// response.getWriter().print("vzvdzvdssss");
			try {

				// changes made for different tab
				// String[] attrValue = request.getParameterValues("label");
				// String[] freeattrValue =
				// request.getParameterValues("txtheading");
				// String compId = request.getParameter("companyId");
				if (request.getParameter("save").equals("std")) {
					Long n = product.saveProductStdCatalogInfo(request, response);
				} else {
					Long n = product.saveProductCatalogInfo(request, response);
				}
				String pathChild = request.getParameter("childPath");
				request.setAttribute("catalogpath", pathChild);
				response.sendRedirect(request.getContextPath() + "/servlet/service/productselection.createproduct?id="
						+ request.getParameter("id") + "&param=edit&tab=3&catalogpath=" + pathChild);
				// product.createTemplate(request,response,n);
				// response.getWriter().print("end---"+n);
				// String btnName = request.getParameter("save");
				// if(btnName.equals("Save Catalog")){

				// response.sendRedirect(request.getContextPath()
				// +
				// "/servlet/service/productselection.createproduct?id="+request.getParameter("id")+"&param=edit&catalogpath="+request.getParameter("catalogpath"));
				// }else{
				// response.sendRedirect(request.getContextPath()
				// +
				// "/servlet/service/productselection.createproduct?id="+n+"&param=edit&download=yes");
				// PrintWriter o = response.getWriter();
				// // request.getContextPath()+
				// "/servlet/service/productselection.viewuploades?pid=" +
				// prodid;
				// String prod = request.getParameter("id");
				// String uri =
				// request.getContextPath()+"/servlet/service/productselection.downloadtemp?pid="+prod;
				// o.print("<script>var link = document.createElement('a');if
				// (typeof link.download === 'string') {link.href =
				// "+uri+";link.download =
				// Template;document.body.appendChild(link);link.click();document.body.removeChild(link);}
				// else {window.open(uri);}</script>");
				// }
			} catch (Exception e) {

			}
		} else if (request.getRequestPathInfo().getExtension().equals("saveChild")) {

			product.saveChildProduct(request, response);
			String pathChild = request.getParameter("childPath");
			request.setAttribute("catalogpath", pathChild);
			response.sendRedirect(request.getContextPath() + "/servlet/service/productselection.createproduct?id="
					+ request.getParameter("id") + "&param=edit&catalogpath=" + pathChild);

		} else if (request.getRequestPathInfo().getExtension().equals("uploadTemplate")) {

			try {
				String prodid = request.getParameter("id");
				// response.getWriter().print(prodid);
				String state = "";
				ArrayList<String> al = product.validateXLSFile(request, response);
				if (al.size() == 0) {
					state = product.readXLSTemplateFile(request, response, prodid);
					response.getWriter().print(state + "-----ddddd---");
					response.sendRedirect(
							request.getContextPath() + "/servlet/service/productselection.createproduct?id=" + prodid
									+ "&param=edit&tab=2&catalogpath=" + request.getParameter("catalogpath"));

				} else {
					// for(int i=0;i<al.size();i++){
					// response.getWriter().print(al.get(i));
					// }
					//
					HttpSession session = request.getSession(true);
					session.setAttribute("error", al);
					// if(request.getSession(true)!=null){
					// //request.setAttribute("fileError",(String)request.getSession().getAttribute("fileError"));
					// if(request.getSession(false).getAttribute("error")!=null){
					// request.getSession(false).setAttribute("error",al);
					// }}
					response.sendRedirect(
							request.getContextPath() + "/servlet/service/productselection.createproduct?id=" + prodid
									+ "&param=edit&tab=2&catalogpath=" + request.getParameter("catalogpath"));

				}

				// session.setAttribute("fileError","Sorry there is some issue
				// with file ,we are unble to processs the file !");
				// response.sendRedirect(request.getContextPath()
				// +"/servlet/service/productselection.createproduct?id="+prodid+"&param=edit");
				// + "/servlet/service/productselection.addproduct?pid=" +
				// prodid);

				// response.getWriter().print(a);
			} catch (Exception e) {
				response.getWriter().print(e.getMessage() + "-----ddddd---");
			}
		} else if (request.getRequestPathInfo().getExtension().equals("uploadAttach")) {
			String pid = request.getParameter("productid");
			String status = product.uploadAttachements(request, response, pid);
			if (status != null && status.equals("success")) {
				response.getWriter().print(status);
			} else {
				response.getWriter().print("Fails" + status);
			}
		} else if (request.getRequestPathInfo().getExtension().equals("uploadCountryMaster")) {

			try {
				String productCodes = "";
				productCodes = product.readPincodeView(request, response);
				response.getWriter().print(productCodes + "-----country---");

			} catch (Exception e) {
				response.getWriter().print(e.getMessage() + "-----ddddd---");
			}
		}
	}

}