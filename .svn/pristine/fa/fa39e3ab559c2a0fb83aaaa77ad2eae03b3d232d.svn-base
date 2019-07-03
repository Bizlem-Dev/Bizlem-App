package biz.com.app.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
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

import biz.com.service.SendMail;

@Component(immediate = true, metatype = false)
@Service(value = javax.servlet.Servlet.class)
@Properties({
		@Property(name = "service.description", value = "Save product Servlet"),
		@Property(name = "service.vendor", value = "VISL Company"),
	@Property(name = "sling.servlet.paths", value = { "/servlet/service/mail" }),
		@Property(name = "sling.servlet.resourceTypes", value = "sling/servlet/default"),
		@Property(name = "sling.servlet.extensions", value = {"send", "pageview" }) })
@SuppressWarnings("serial")
public class MailerServlet extends SlingAllMethodsServlet {

	@Reference
	private SlingRepository repos;	
	
	@Reference
	private SendMail service;	
	
	@Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		
		@SuppressWarnings("unused")
		Node rootNode,rootNode1=null;	
		if (request.getRequestPathInfo().getExtension().equals("Send")) {
			Session session;
			PrintWriter o=response.getWriter();
			try{
				o.print(service.sendMail("atulmarch91@gmail.com","atul56@ymail.com", "Testig RFQ service","This is send request message"));
			} catch (Exception e) {
	           o.print(e.getMessage());
	           }		

			

		}
		
	}
}

	