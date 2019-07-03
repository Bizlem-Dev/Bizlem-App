package biz.com.app.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
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
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.jcr.api.SlingRepository;

import biz.com.service.ActiveMQProducer;
import biz.com.service.NetworkExpandService;

@Component(immediate = true, metatype = false)
@Service(value = javax.servlet.Servlet.class)
@Properties({
		@Property(name = "service.description", value = "Invivtaion Servlet "),
	@Property(name = "sling.servlet.paths", value = { "/servlet/service/invite" }),
		@Property(name = "sling.servlet.resourceTypes", value = "sling/servlet/default"),
		@Property(name = "sling.servlet.extensions", value = {"startprocess", "pageview" }) })
@SuppressWarnings("serial")
public class NetworkExpandServlet extends SlingAllMethodsServlet {

	@Reference
	private NetworkExpandService nwtservice;
	
	@Reference
	ActiveMQProducer producer;

	@Reference
	private SlingRepository repo;

	
	@Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		
		
		
		 if (request.getRequestPathInfo().getExtension().equals("addContact")) {
			//if(request.getParameter("fname")!=null && request.getParameter("lname")!=null && request.getParameter("email")!=null){
				 String re=nwtservice.addContact(request);
				 if(re!=null && re.equals("success")){
					response.getWriter().print("success"); 
				 }else{
					 response.getWriter().print("Failed"+re); 
				 }
//			 	}else{
//			 		 response.getWriter().print("No parmeter found!");
//			 		//request.getRequestDispatcher("/content/invite/.expandnetwork").forward(request, response);	
//			 	}
//			 
				
			}else  if (request.getRequestPathInfo().getExtension().equals("startprocess")) {
			if(request.getParameter("response")!=null){
				request.setAttribute("response", request.getParameter("response"));
				request.getRequestDispatcher("/content/invite/.expandnetwork").forward(request, response);
			}else{
				request.getRequestDispatcher("/content/invite/.expandnetwork").forward(request, response);	
			}
			
		}else if(request.getRequestPathInfo().getExtension().equals("searchNode")){
			String path=request.getParameter("path");
			try {
				Session	session = repo.login(new SimpleCredentials("admin", "admin"
						.toCharArray()));
				Node rootNode=session.getRootNode();
				String []way=path.split("/");
				Node real=null;
				for(int i=1;i<way.length;i++){
					rootNode=rootNode.getNode(way[i]);
					//response.getWriter().print("Ex"+rootNode.getName());
				}
				NodeIterator itr=rootNode.getNodes();
				JSONArray ar=new JSONArray();
				while(itr.hasNext()){					
					ar.put(itr.nextNode().getName());
				}
				response.getWriter().print(ar.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				response.getWriter().print("");
			}
			
		}	else if (request.getRequestPathInfo().getExtension().equals("invite")) {
			String msg=nwtservice.processInvite(request, response);
			response.getWriter().print(msg);
		}
	}
	@Override
	protected void doPost(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		try {
		@SuppressWarnings("unused")
		Node rootNode,rootNode1=null;	
		if (request.getRequestPathInfo().getExtension().equals("uploadFile")) {
			String re=nwtservice.uploadImportedContacts(request);
			response.getWriter().print(re);
			response.sendRedirect(request.getContextPath()+"/servlet/service/invite.startprocess?response=success");
			}	
		}catch (Exception e) {
			// TODO Auto-generated catch block
			response.getWriter().print("exev"+e.getMessage());
			
		}
	}
}

	