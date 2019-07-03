package biz.com.app.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.jcr.api.SlingRepository;

import biz.com.service.CompanyService;
import biz.com.service.PersonService;
import biz.com.service.ProductService;

@Component(immediate = true, metatype = false)
@Service(value = javax.servlet.Servlet.class)
@Properties({
		@Property(name = "service.description", value = "Save product Servlet"),
		@Property(name = "service.vendor", value = "VISL Company"),
		@Property(name = "sling.servlet.paths", value = { "/servlet/service/globalsearch" }),
		@Property(name = "sling.servlet.resourceTypes", value = "sling/servlet/default"),
		@Property(name = "sling.servlet.extensions", value = { "process",
				"latestproducts", "brief", "productlist" }) })
@SuppressWarnings({ "serial", "unused" })
public class GlobalSearchServlet extends SlingAllMethodsServlet {

	@Reference
	private SlingRepository repos;

	@Reference
	private PersonService person;

	@Reference
	private CompanyService company;
	
	@Reference
	private ProductService product;
	
	final int NUMBEROFRESULTSPERPAGE = 10;

	@Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {

		if (request.getRequestPathInfo().getExtension().equals("process")) {
			Node rootNode = null;
			Session session;

			PrintWriter o = response.getWriter();
			
							
			try {
				String path = request.getParameter("core");
				String s1 = request.getParameter("search");
				while(s1.indexOf(" ") != -1){
					s1 = s1.replace(" ", "");
				}
				//o.println(s1);
				String url="http://34.193.219.25:8983/solr/company/select?q="+s1+"&wt=json&indent=true&facet=true&&facet=true&facet.field=v_keyword1&facet.field=v_keyword2";
				String url1="http://34.193.219.25:8983/solr/product/select?q="+s1+"&wt=json&indent=true&facet=true&&facet=true&facet.field=v_keyword1&facet.field=v_keyword2";
				String url2="http://34.193.219.25:8983/solr/person/select?q="+s1+"&wt=json&indent=true&facet=true&&facet=true&facet.field=v_keyword1&facet.field=v_keyword2";
				request.setAttribute("com", person.readJsonFromUrl(url));
				//o.print("1");
				request.setAttribute("per", person.readJsonFromUrl(url1));
				//o.print("1");
				request.setAttribute("pro", person.readJsonFromUrl(url2));
				//o.print("1");
				session = repos.login(new SimpleCredentials("admin", "admin"
						.toCharArray()));
				rootNode = session.getRootNode().getNode("content");

				if (path != null && !"".equals(path)) {

					if (path.equals("product")) {
						ArrayList<Node> m =new ArrayList<Node>();
						ArrayList<Node> searchRes =new ArrayList<Node>();
						// o.print("i2");
						// o.print("i3");
						String from = null;
						String to = null;
						int t, f;
						String search = request.getParameter("search");
						from = request.getParameter("from");
						to =from;
						searchRes = product.getProductList(search);
						request.setAttribute("totalperson", searchRes.size());
						if (to != null && from != null) {
							try {
								t = Integer.parseInt(to);
								f = Integer.parseInt(from);
								f=(f-1)*NUMBEROFRESULTSPERPAGE;
								t=t*NUMBEROFRESULTSPERPAGE;
								m = searchRes;
								ArrayList<Node> alist = new ArrayList<Node>();
								if(m.size()>t){
									for (int i = f; i < t; i++) {
										alist.add(m.get(i));
									}
								}else{
								for (int i = f; i < m.size(); i++) {
									alist.add(m.get(i));
								}
								}
								request.setAttribute("product", alist);
								request.getRequestDispatcher(
										"/content/products/.propagination")
										.forward(request, response);
							} catch (NumberFormatException e) {
								ArrayList<Node> m1 = searchRes;
								ArrayList<Node> alist = new ArrayList<Node>();
								if (m1.size() > NUMBEROFRESULTSPERPAGE) {
									for (int i = 0; i < NUMBEROFRESULTSPERPAGE; i++) {
										alist.add(m1.get(i));

									}
								} else {
									for (int i = 0; i < m1.size(); i++) {
										alist.add(m1.get(i));

									}
								}

								request.setAttribute("product", "");
								request.getRequestDispatcher(
										"/content/products/.propagination")
										.forward(request, response);

							}

						} else {
							ArrayList<Node> m2 = searchRes;
							ArrayList<Node> alist = new ArrayList<Node>();
							if (m2.size() > NUMBEROFRESULTSPERPAGE) {
								for (int i = 0; i < NUMBEROFRESULTSPERPAGE; i++) {
									alist.add(m2.get(i));

								}
							} else {
								for (int i = 0; i < m2.size(); i++) {
									alist.add(m2.get(i));

								}
							}
							request.setAttribute("product", m2);
							request.getRequestDispatcher("/content/products/.searchresults").forward(
									request, response);
						}
						
						
						
					} else if (path.equals("person")) {
						// o.print("i3");
						String from = null;
						String to = null;
						int t, f;
						String search = request.getParameter("search");
						from = request.getParameter("from");
						to =from;
						request.setAttribute("totalperson", person.getPersonList(search).size());
						if (to != null && from != null) {
							try {
								t = Integer.parseInt(to);
								f = Integer.parseInt(from);
								f=(f-1)*NUMBEROFRESULTSPERPAGE;
								t=t*NUMBEROFRESULTSPERPAGE;
								ArrayList<Node> m = person
										.getPersonList(search);
								ArrayList<Node> alist = new ArrayList<Node>();

								if(m.size()>t){
									for (int i = f; i < t; i++) {
										alist.add(m.get(i));
									}
								} else {
									for (int i = f; i < m.size(); i++) {
										alist.add(m.get(i));

									}
								}
								request.setAttribute("person", alist);
								request.getRequestDispatcher(
										"/content/products/.perpagination")
										.forward(request, response);
							} catch (NumberFormatException e) {
								ArrayList<Node> m = person
										.getPersonList(search);
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

								request.setAttribute("person", alist);
								request.getRequestDispatcher(
										"/content/products/.perpagination")
										.forward(request, response);

							}

						} else {
							ArrayList<Node> m = person.getPersonList(search);
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
							request.setAttribute("person", alist);
							request.getRequestDispatcher(
									"/content/products/.searchPersonList")
									.forward(request, response);
						}
					} else if (path.equals("company")) { // o.print("i4");
						// o.print("i3");
						String from = null;
						String to = null;
						int t, f;
						String search = request.getParameter("search");
						from = request.getParameter("from");
						to =from;
						//String querryStr = "select [companyName] from [nt:base] where (contains('companyName','*"+search +"*'))  and ISDESCENDANTNODE('/content/company/')";
						//o.print("sql query---  "+querryStr);
						request.setAttribute("totalperson", company.getComapnyList(search).size());
						if (to != null && from != null) {
							try {
								t = Integer.parseInt(to);
								f = Integer.parseInt(from);
								f=(f-1)*NUMBEROFRESULTSPERPAGE;
								t=t*NUMBEROFRESULTSPERPAGE;
								ArrayList<Node> m = company.getComapnyList(search);
								ArrayList<Node> alist = new ArrayList<Node>();

								if(m.size()>t){
									for (int i = f; i < t; i++) {
										alist.add(m.get(i));
									}
								} else {
									for (int i = f; i < m.size(); i++) {
										alist.add(m.get(i));

									}
								}
								request.setAttribute("company", alist);
								request.getRequestDispatcher(
										"/content/products/.compagination")
										.forward(request, response);
							} catch (NumberFormatException e) {
								ArrayList<Node> m =company.getComapnyList(search);
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

								request.setAttribute("company", alist);
								request.getRequestDispatcher(
										"/content/products/.compagination")
										.forward(request, response);

							}

						} else {
							ArrayList<Node> m =company.getComapnyList(search);
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
							request.setAttribute("company", alist);
						request.getRequestDispatcher("/content/products/.searchCompanyList").forward(request, response);
						}
					} else {
						request.getRequestDispatcher(
								"/content/common/.404error").forward(request,
								response);
					}

				} else {
					request.getRequestDispatcher("/content/common/.404error")
							.forward(request, response);
				}
			} catch (Exception e) {
				o.print("Error" + e.getMessage());
			}

			// response.getOutputStream().println(request.getParameter("companyName"));

		}else if(request.getRequestPathInfo().getExtension().equals("rfq")){
			request.getRequestDispatcher("/content/products/.rfqlist")
			.forward(request, response);
			
		}else if(request.getRequestPathInfo().getExtension().equals("rfqdata")){
			
			
			request.getRequestDispatcher("/content/products/.rfqdetails")
			.forward(request, response);
	}else if(request.getRequestPathInfo().getExtension().equals("messagedata")){
		
		
		request.getRequestDispatcher("/content/products/.messagedetails")
		.forward(request, response);
    }else if(request.getRequestPathInfo().getExtension().equals("addrfqdraft")){
		String companyid= request.getParameter("Companyid");
		String rfqNo= request.getParameter("rfqNo");
		Session session;
		Node rootNode,groupNode = null;
		PrintWriter out = response.getWriter();

		try{
		session = repos.login(new SimpleCredentials("admin", "admin"
				.toCharArray()));
		rootNode = session.getRootNode().getNode("content").getNode("company");
		out.println("1");
		if(rootNode.hasNode(companyid)&&rootNode.getNode(companyid).hasNode("Rfq")&&rootNode.getNode(companyid).getNode("Rfq").hasNode(rfqNo)){
			//out.println("1");
			Node rfqNode=rootNode.getNode(companyid).getNode("Rfq").getNode(rfqNo);
			//out.println("1");
			if(rfqNode.hasProperty("status")){
				//out.println("1");
				rfqNode.setProperty("status", "wip");
				//out.println("1");
			}
		}
		session.save();
		request.getRequestDispatcher("/content/products/.rfqlist")
		.forward(request, response);
		}catch(Exception e){
			e.getMessage();
		}
		
}else if(request.getRequestPathInfo().getExtension().equals("searchlist")){
			Node rootNode,groupNode = null;
			Session session;

			PrintWriter o = response.getWriter();

			try {
				
				String path = request.getParameter("core1");
				session = repos.login(new SimpleCredentials("admin", "admin"
						.toCharArray()));
				rootNode = session.getRootNode().getNode("content");

				if (path != null && !"".equals(path)) {

					if (path.equals("group")) {
						// o.print("i2");
						// o.print("i3");
						String from = null;
						String to = null;
						int t, f;
						String search = request.getParameter("searchByName");
						from = request.getParameter("from");
						to =from;
						request.setAttribute("total", company.searchGroup(search).size());
						if (to != null && from != null) {
							try {
								t = Integer.parseInt(to);
								f = Integer.parseInt(from);
								f=(f-1)*NUMBEROFRESULTSPERPAGE;
								t=t*NUMBEROFRESULTSPERPAGE;
								ArrayList<Node> m = company.searchGroup(search);
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
								request.setAttribute("grouplist", alist);
								 request.getRequestDispatcher(
						                    "/content/group/.searchgroupListDiv").forward(request,
						                    response);
							} catch (NumberFormatException e) {
								ArrayList<Node> m =company.searchGroup(search);
								ArrayList<Node> alist = new ArrayList<Node>();
								if (m.size() > NUMBEROFRESULTSPERPAGE) {
									for (int i = 0; i < NUMBEROFRESULTSPERPAGE; i++) {
										alist.add(m.get(i));

									}
								} else {
									for (int i = 0; i < m.size(); i++) {
										alist.add(m.get(i));

									}
								}								request.setAttribute("grouplist", alist);
								 request.getRequestDispatcher(
						                    "/content/group/.searchgroupListDiv").forward(request,
						                    response);
							}

						} else {
							ArrayList<Node> m =company.searchGroup(search);
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
							request.setAttribute("grouplist", alist);
							request.getRequestDispatcher(
									"/content/group/.searchgroupList")
									.forward(request, response);
						}
						
					} else if (path.equals("person")) {
						// o.print("i3");
						String from = null;
						String to = null;
						int t, f;
						String search = request.getParameter("searchByName");
						from = request.getParameter("from");
						to =from;
						request.setAttribute("totalperson", person.getPersonList(search).size());
						if (to != null && from != null) {
							try {
								t = Integer.parseInt(to);
								f = Integer.parseInt(from);
								f=(f-1)*NUMBEROFRESULTSPERPAGE;
								t=t*NUMBEROFRESULTSPERPAGE;
								ArrayList<Node> m = person
										.getPersonList(search);
								ArrayList<Node> alist = new ArrayList<Node>();

								if(m.size()>t){
									for (int i = f; i < t; i++) {
										alist.add(m.get(i));
									}
								} else {
									for (int i = f; i < m.size(); i++) {
										alist.add(m.get(i));

									}
								}
								request.setAttribute("person", alist);
								request.getRequestDispatcher(
										"/content/user/.userListDiv")
										.forward(request, response);
							} catch (NumberFormatException e) {
								ArrayList<Node> m = person
										.getPersonList(search);
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

								request.setAttribute("person", alist);
								request.getRequestDispatcher(
										"/content/user/.userListDiv")
										.forward(request, response);

							}

						} else {
							ArrayList<Node> m = person.getPersonList(search);
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
							request.setAttribute("person", alist);
							request.getRequestDispatcher(
									"/content/user/.userList")
									.forward(request, response);
						}
					} else if (path.equals("company")) { // o.print("i4");
						// o.print("i3");
						String from = null;
						String to = null;
						int t, f;
						String search = request.getParameter("searchByName");
						from = request.getParameter("from");
						to =from;
						request.setAttribute("total", company.getComapnyList(search).size());
						if (to != null && from != null) {
							try {
								t = Integer.parseInt(to);
								f = Integer.parseInt(from);
								f=(f-1)*NUMBEROFRESULTSPERPAGE;
								t=t*NUMBEROFRESULTSPERPAGE;
								ArrayList<Node> m = company.getComapnyList(search);
								ArrayList<Node> alist = new ArrayList<Node>();

								if(m.size()>t){
									for (int i = f; i < t; i++) {
										alist.add(m.get(i));
									}
								} else {
									for (int i = f; i < m.size(); i++) {
										alist.add(m.get(i));

									}
								}
								request.setAttribute("companylist", alist);
								request.getRequestDispatcher(
										"/content/company/.searchcompanyListDiv")
										.forward(request, response);
							} catch (NumberFormatException e) {
								ArrayList<Node> m =company.getComapnyList(search);
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
								request.getRequestDispatcher(
										"/content/company/.searchcompanyListDiv")
										.forward(request, response);

							}

						} else {
							ArrayList<Node> m =company.getComapnyList(search);
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
							request.getRequestDispatcher(
									"/content/company/.searchcompanyList")
									.forward(request, response);
						}
					} else {
						request.getRequestDispatcher(
								"/content/common/.404error").forward(request,
								response);
					}

				} else {
					request.getRequestDispatcher("/content/common/.404error")
							.forward(request, response);
				}
			} catch (Exception e) {
				o.print("Error" + e.getMessage());
			}
		}
	}
}