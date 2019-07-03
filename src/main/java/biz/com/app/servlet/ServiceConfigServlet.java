package biz.com.app.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import org.apache.commons.collections.map.HashedMap;
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
import biz.com.service.EventService;
import biz.com.service.PersonService;
import biz.com.service.ProductService;
import biz.com.service.ReportService;

@Component(immediate = true, metatype = false)
@Service(value = javax.servlet.Servlet.class)
@Properties({
		@Property(name = "service.description", value = "Save product Servlet"),
		@Property(name = "service.vendor", value = "VISL Company"),
		@Property(name = "sling.servlet.paths", value = { "/servlet/service/config" }),
		@Property(name = "sling.servlet.resourceTypes", value = "sling/servlet/default"),
		@Property(name = "sling.servlet.extensions", value = { "process",
				"latestproducts", "brief", "productlist" }) })
@SuppressWarnings({ "serial", "unused" })
public class ServiceConfigServlet extends SlingAllMethodsServlet {

	@Reference
	private SlingRepository repos;

	@Reference
	private EventService service;

	@Reference
	private ReportService report;
	final int NUMBEROFRESULTSPERPAGE = 10;

	@Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {

		if (request.getRequestPathInfo().getExtension().equals("paidsearch")) {

		} else if (request.getRequestPathInfo().getExtension()
				.equals("serviceUrl")) {

			String userId = request.getRemoteUser();
			String name = request.getParameter("name");
			if (name != null) {
				List al = service.getCustomerDetails(request, userId,
						response.getWriter());
				// response.getWriter().print("ddd"+al.size());
				if (al != null) {
					for (int i = 0; i < al.size(); i++) {

						HashMap<String, String> mp = (HashMap) al.get(i);
						// response.getWriter().print("ddd"+al.size()+mp.containsValue(name));
						if (mp.get("serviceId").equals(name)) {
							request.setAttribute("url", mp.get("url"));
							request.getRequestDispatcher(
									"/content/products/.paidsearch").forward(
									request, response);
						}
					}
				}
			}
			// request.getSession(true).setAttribute("services", servicesList);

			// response.sendRedirect("http://prod.bizlem.io:8082/portal/servlet/service/config.paidsearch?name="+name);

		} else if (request.getRequestPathInfo().getExtension()
				.equals("sponseredsearch")) {

			request.getRequestDispatcher("/content/products/.sponseredsearch")
					.forward(request, response);

		} else if (request.getRequestPathInfo().getExtension()
				.equals("bannercreation")) {

			request.getRequestDispatcher("/content/products/.bannercreation")
					.forward(request, response);

		} else if (request.getRequestPathInfo().getExtension()
				.equals("serviceReport")) {
			String userId = request.getRemoteUser();
			String name = request.getParameter("name");
			if (name != null) {
				List al = service.getCustomerDetails(request, userId,
						response.getWriter());
				// response.getWriter().print("ddd"+al.size());
				if (al != null) {
					for (int i = 0; i < al.size(); i++) {

						HashMap<String, String> mp = (HashMap) al.get(i);
						// response.getWriter().print("ddd"+al.size()+mp.containsValue(name));
						if (mp.get("serviceId").equals(name)) {
							
							request.getRequestDispatcher(
									"/content/products/."+report.fetchReport(request, response, mp))
									.forward(request, response);
						}
					}
				}
			}
		}
	}
}