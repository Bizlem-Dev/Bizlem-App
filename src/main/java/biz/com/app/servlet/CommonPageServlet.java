package biz.com.app.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

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

import biz.com.service.EventService;
import biz.com.service.PersonService;

@Component(immediate = true, metatype = false)
@Service(value = javax.servlet.Servlet.class)
@Properties({
		@Property(name = "service.description", value = "Save product Servlet"),
		@Property(name = "service.vendor", value = "VISL Company"),
	@Property(name = "sling.servlet.paths", value = { "/servlet/service/page" }),
		@Property(name = "sling.servlet.resourceTypes", value = "sling/servlet/default"),
		@Property(name = "sling.servlet.extensions", value = {"buyer", "manufacturer" }) })
@SuppressWarnings("serial")
public class CommonPageServlet extends SlingAllMethodsServlet {

	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/customerservice";

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "password";
	
	@Reference
	private EventService eventService;
	@Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		
			
		if (request.getRequestPathInfo().getExtension().equals("keydifferentiators")) {
			
			PrintWriter o=response.getWriter();
			try{
			request.getRequestDispatcher("/content/static/.keydifferentiators").forward(
					request, response);
			} catch (Exception e) {
	           o.print(e.getMessage());
	           }		

				// response.getOutputStream().println(request.getParameter("companyName"));

		}else if (request.getRequestPathInfo().getExtension().equals("allcategories")) {
			 
			 PrintWriter o=response.getWriter();
			 try{
			 request.getRequestDispatcher("/content/products/.allcategories").forward(
			   request, response);
			 } catch (Exception e) {
			       o.print(e.getMessage());
			       }  

			  // response.getOutputStream().println(request.getParameter("companyName"));

			
		}else if (request.getRequestPathInfo().getExtension().equals("userservicedata")) {
			
			PrintWriter o=response.getWriter();
			String result = "0";
	    	Statement stmt = null;
	    	try{
	    //		bundle = ResourceBundle.getBundle("serverConfig");
	    		//  Class.forName(JDBC_DRIVER);
					Connection con = (Connection) DriverManager.getConnection(
							DB_URL,
							USER,
							PASS);
			
			stmt = (Statement) con.createStatement();
			String query = "select serviceId from mdmperson where emailId = '"+request.getRemoteUser()+"' and flag = 0 " ;
			ResultSet rs = stmt.executeQuery(query);
			if(rs.next()){
				result = String.valueOf(rs.getInt("serviceId"));
			}else{
				result = "false";
			}
	    	}catch(Exception e){
	    		o.print(e.getMessage());
	    	}
	    	//return result;
			
			o.print(result);
				// response.getOutputStream().println(request.getParameter("companyName"));

		}else if (request.getRequestPathInfo().getExtension().equals("userserviceowncloud")) {
			
			PrintWriter o=response.getWriter();
			String result = "false";
	    	Statement stmt = null;
	    	try{
	    //		bundle = ResourceBundle.getBundle("serverConfig");
	    		//  Class.forName(JDBC_DRIVER);
					Connection con = (Connection) DriverManager.getConnection(
							DB_URL,
							USER,
							PASS);
			
			stmt = (Statement) con.createStatement();
			//String query = "select serviceId from mdmperson where emailId = '"+request.getRemoteUser()+"' and flag = 0 " ;
			String query = "select * from customer_service_status where customerId = '"+request.getRemoteUser() + "' and status = 'active' and service_end_date > now() and productCode = 'secure_document'";
			ResultSet rs = stmt.executeQuery(query);
			if(rs.next()){
				//result = String.valueOf(rs.getInt("serviceId"));
				 result = eventService.getUserPassword(request.getRemoteUser());
			}else{
				result = "false";
			}
	    	}catch(Exception e){
	    		o.print(e.getMessage());
	    		result = "false";
	    	}
	    	//return result;
			
			o.print(result);
				// response.getOutputStream().println(request.getParameter("companyName"));

		}else if (request.getRequestPathInfo().getExtension().equals("userservicerfq")) {
			
			PrintWriter o=response.getWriter();
			String result = "false";
	    	Statement stmt = null;
	    	try{
	    //		bundle = ResourceBundle.getBundle("serverConfig");
	    		//  Class.forName(JDBC_DRIVER);
					Connection con = (Connection) DriverManager.getConnection(
							DB_URL,
							USER,
							PASS);
			
			stmt = (Statement) con.createStatement();
			//String query = "select serviceId from mdmperson where emailId = '"+request.getRemoteUser()+"' and flag = 0 " ;
			String query = "select * from customer_service_status where customerId = '"+request.getRemoteUser() + "' and status = 'active' and service_end_date > now() and productCode = 'RFQ_Service'";
			ResultSet rs = stmt.executeQuery(query);
			if(rs.next()){
				result = String.valueOf(rs.getInt("serviceId"));
				// result = eventService.getUserPassword(request.getRemoteUser());
			}else{
				result = "false";
			}
	    	}catch(Exception e){
	    		o.print(e.getMessage());
	    		result = "false";
	    	}
	    	//return result;
			
			o.print(result);
				// response.getOutputStream().println(request.getParameter("companyName"));

		}
		else if (request.getRequestPathInfo().getExtension().equals("buyers")) {
			
			PrintWriter o=response.getWriter();
			try{
			request.getRequestDispatcher("/content/static/.buyers").forward(
					request, response);
			} catch (Exception e) {
	           o.print(e.getMessage());
	           }		

				// response.getOutputStream().println(request.getParameter("companyName"));

		}else if (request.getRequestPathInfo().getExtension().equals("opensecuredoc")) {
			
			PrintWriter o=response.getWriter();
			try{
			request.getRequestDispatcher("/content/products/.opensecuredoc").forward(
					request, response);
			} catch (Exception e) {
	           o.print(e.getMessage());
	           }		

				// response.getOutputStream().println(request.getParameter("companyName"));

		}		else if (request.getRequestPathInfo().getExtension().equals("mdmpage")) {
			
			PrintWriter o=response.getWriter();
			try{
			request.getRequestDispatcher("/content/user/.mdmframe").forward(
					request, response);
			} catch (Exception e) {
	           o.print(e.getMessage());
	           }		

				// response.getOutputStream().println(request.getParameter("companyName"));

		}
else if (request.getRequestPathInfo().getExtension().equals("advertise")) {
			
			PrintWriter o=response.getWriter();
			try{
			request.getRequestDispatcher("/content/static/.advertise").forward(
					request, response);
			} catch (Exception e) {
	           o.print(e.getMessage());
	           }		

				// response.getOutputStream().println(request.getParameter("companyName"));

		}		
else if (request.getRequestPathInfo().getExtension().equals("profile")) {
	
	PrintWriter o=response.getWriter();
	try{
	request.getRequestDispatcher("/content/static/.profile").forward(
			request, response);
	} catch (Exception e) {
       o.print(e.getMessage());
       }		

		// response.getOutputStream().println(request.getParameter("companyName"));

}		
else if (request.getRequestPathInfo().getExtension().equals("aboutus")) {
	
	PrintWriter o=response.getWriter();
	try{
	request.getRequestDispatcher("/content/static/.aboutus").forward(
			request, response);
	} catch (Exception e) {
       o.print(e.getMessage());
       }		

		// response.getOutputStream().println(request.getParameter("companyName"));

}		
else if (request.getRequestPathInfo().getExtension().equals("productlist")) {
	
	PrintWriter o=response.getWriter();
	try{
	request.getRequestDispatcher("/content/static/.productlist").forward(
			request, response);
	} catch (Exception e) {
       o.print(e.getMessage());
       }		

		// response.getOutputStream().println(request.getParameter("companyName"));

}
else if (request.getRequestPathInfo().getExtension().equals("contactus")) {
	
	PrintWriter o=response.getWriter();
	try{
	request.getRequestDispatcher("/content/static/.contactus").forward(
			request, response);
	} catch (Exception e) {
       o.print(e.getMessage());
       }		

		// response.getOutputStream().println(request.getParameter("companyName"));

}else if (request.getRequestPathInfo().getExtension().equals("servicelist")) {
	
	PrintWriter o=response.getWriter();
	try{
	request.getRequestDispatcher("/content/products/.servicelist").forward(
			request, response);
	} catch (Exception e) {
       o.print(e.getMessage());
       }		

		// response.getOutputStream().println(request.getParameter("companyName"));

}else if (request.getRequestPathInfo().getExtension().equals("topnews")) {
	
	PrintWriter o=response.getWriter();
	try{
		if(request.getParameter("actionType")!=null){
			request.setAttribute("news",eventService.getnewsList(request)); 
			request.getRequestDispatcher("/content/products/.topnews").forward(request, response);
			}
	} catch (Exception e) {
       o.print(e.getMessage());
       }		

		// response.getOutputStream().println(request.getParameter("companyName"));

}else if (request.getRequestPathInfo().getExtension()
		.equals("loadTable")) {
	request.getParameter("pid");
	request.getRequestDispatcher("/content/products/.loadTableProduct")
			.forward(request, response);
}


	}
}

	