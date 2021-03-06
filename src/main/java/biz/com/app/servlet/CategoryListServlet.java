package biz.com.app.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;

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
import biz.com.service.NewsService;
import biz.com.service.PersonService;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import java.net.URLClassLoader;
import net.sf.ehcache.Element;
import java.util.ArrayList;


@Component(immediate = true, metatype = false)
@Service(value = javax.servlet.Servlet.class)
@Properties({
		@Property(name = "service.description", value = "Save product Servlet"),
		@Property(name = "service.vendor", value = "VISL Company"),
	@Property(name = "sling.servlet.paths", value = { "/servlet/service/index" }),
		@Property(name = "sling.servlet.resourceTypes", value = "sling/servlet/default"),
		@Property(name = "sling.servlet.extensions", value = {"catlist", "pageview" }) })
@SuppressWarnings("serial")
public class CategoryListServlet extends SlingAllMethodsServlet {
	
	
	@Reference
	private SlingRepository repos;	
	
	@Reference
	private EventService eventService;
	
	@Reference
	private NewsService service;
	
	@Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		
		response.setHeader("Cache-Control","public");
		response.setHeader("Cache-Control","max-age=100000");
		response.setHeader("Vary","Accept-Encoding");
		
		@SuppressWarnings("unused")
		Node rootNode,rootNode1=null;	
		if (request.getRequestPathInfo().getExtension().equals("pageview")) {
			//U
			Session session;
			PrintWriter o=response.getWriter();
			try{
	//			URL url = this.getClass().getResource("ehcache.xml");
				
				//Get file from resources folder
				//ClassLoader classLoader = getClass().getClassLoader();
				//o.println("start---"+classLoader);
				//InputStream inputStream1 = classLoader.getResourceAsStream("/content/config/ehcache.xml");
				o.println("Loading echache.xml");
				//InputStream inputStream1 = classLoader.getResourceAsStream("../resources/SLING-INF/apps/config/ehcache.xml");
				
//				String classpath = System.getProperty("java.class.path");
//				o.println("classpath---"+classpath);
//				
//				
//				
//				//InputStream inputStream = this.getClass().getResourceAsStream("ehcache.xml");
//				InputStream inputStream = ClassLoader.class.getResourceAsStream("/resources/SLING-INF/apps/config/ehcache.xml");
//				o.println("start");
//				o.println("start 1"+inputStream);
				
				//Create a singleton CacheManager using defaults
			//	CacheManager manager = CacheManager.create();

				//Create a Cache specifying its configuration.
//				Cache testCache = new Cache(
//				  new CacheConfiguration("testCache", 10)
//				    .memoryStoreEvictionPolicy("LFU")
//				    .eternal(false)
//				    .timeToLiveSeconds(600)
//				    .timeToIdleSeconds(300)
//				    .diskExpiryThreadIntervalSeconds(0)
//				    );
				//  manager.addCache("testCache");
				
				
				//String k=request.getParameter("idp");
			session = repos.login(new SimpleCredentials("admin", "admin"
		            .toCharArray()));
		
			ArrayList<Node> n=null;
			
			n = eventService.getEventList(request,"Pump","selected_category");
			
			
			
			request.setAttribute("event",n);
		
	       
	        
	        //o.println("list-----"+indexCache.get("exh").getValue());
	        
	//        ArrayList s = new ArrayList();
	        String s = "";
	        //ArrayList<String> retrievedList2 = (ArrayList<String>) indexCache.get("exh").getValue();
			
			
			
			request.setAttribute("hotproductsearch",eventService.getHotProductSearchText(request));	
			
			rootNode = session.getRootNode().getNode("content");
			
			String[] para = { "process", "query" };
			String[] paravalue = { "notstore", request.getParameter("metadeta") };

			String res = service.callPostService(
					"http://34.193.219.25/bsearch/ressfeedtoDBwidget.php", para,
					paravalue);
			String []da=res.split(",");
			request.setAttribute("widgets", da);
		   
			//response.getOutputStream().println("eleeeeeeeeeee---"+ele);
//			response.getOutputStream().println("s---"+s);
                      
			request.getRequestDispatcher("/content/products/.index").forward(request, response);
					} catch (Exception e) {
	           o.print(e.getMessage());
	           }		

				// response.getOutputStream().println(request.getParameter("companyName"));

		}else if (request.getRequestPathInfo().getExtension().equals("catlist")) {
			Session session;
			PrintWriter o=response.getWriter();
			try{
				
			session = repos.login(new SimpleCredentials("admin", "admin"
		            .toCharArray()));
		    
			String catId = request.getParameter("catid");
		    	rootNode = session.getRootNode().getNode("content").getNode("category");
		    
		    request.setAttribute("hotproductsearch",eventService.getHotProductSearchText(request));
		    request.setAttribute("hotsellersearch",eventService.getHotSellerSearchText(request));
		    request.setAttribute("photostory",eventService.getPhotoStorySearchText(request));	
		    request.setAttribute("products",eventService.getProductSearchText(request));
		     
			request.getRequestDispatcher("/content/category/.subcategary").forward(
					request, response);
			} catch (Exception e) {
	           o.print(e.getMessage());
	           }		

				// response.getOutputStream().println(request.getParameter("companyName"));

		}else if (request.getRequestPathInfo().getExtension().equals("login")) {
			//java.util.Properties p=eventService.propertie();
			response.sendRedirect("http://prod.bizlem.io/portal/login");
					
	}else if (request.getRequestPathInfo().getExtension().equals("signup")) {
		//java.util.Properties p=eventService.propertie();
		response.sendRedirect("http://prod.bizlem.io/portal/app/newaccount.jsp");
		}else if (request.getRequestPathInfo().getExtension().equals("countrycode")) {
			//java.util.Properties p=eventService.propertie();
			//
			//response.sendRedirect("http://prod.bizlem.io/portal/app/newaccount.jsp");
			
			}  

	}
}

	