package biz.com.app.servlet;

import java.io.BufferedReader;
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
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.jcr.api.SlingRepository;

@Component(immediate = true, metatype = false)
@Service(value = javax.servlet.Servlet.class)
@Properties({
		@Property(name = "service.description", value = "Save product Servlet"),
		@Property(name = "service.vendor", value = "VISL Company"),
	@Property(name = "sling.servlet.paths", value = { "/servlet/service/provisionproduct" }),
		@Property(name = "sling.servlet.resourceTypes", value = "sling/servlet/default"),
		@Property(name = "sling.servlet.extensions", value = {"productnode"}) })
@SuppressWarnings("serial")
public class ProductProvAddServlet extends SlingAllMethodsServlet{

	@Reference
	private SlingRepository repo;

	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = null;

		try {
			out = response.getWriter();
				out.println("Hello Do get");
		} catch (Exception e) {
			// out.println(e.getMessage());
		}

	}
	
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {

		PrintWriter out = null;
		Session session = null;
		Node content = null;
		Node services = null;
		Node serviceId = null;
		Node serviceCode = null;
		Node user = null;
		Node userName = null;
		StringBuilder builder = null;
		String username = null;
		String productid = null;
		String productcode = null;
		BufferedReader bufferedReaderCampaign = null;
		JSONObject fulljsonobject = null;
		try {
			out = response.getWriter();
			session = repo.login(new SimpleCredentials("admin", "admin".toCharArray()));
			content = session.getRootNode().getNode("content");
			///content/services/service001(789)(Property producttype)/users/gaurav.bhandari_damacgroup.com.partial
			//jcr:root/content/user/gaurav.bhandari_damacgroup.com.partial  serivces/doctiger/789/G1
			if (request.getRequestPathInfo().getExtension().equals("productnode")) {
				
				builder = new StringBuilder();

				bufferedReaderCampaign = request.getReader();
				String line = null;
				while ((line = bufferedReaderCampaign.readLine()) != null) {
					builder.append(line + "\n");
				}
				fulljsonobject = new JSONObject(builder.toString());
				 //userId --mailId,serviceId --800, 
				username = fulljsonobject.getString("userId").replace("@", "_");
				productid = fulljsonobject.getString("serviceId");
				productcode = fulljsonobject.getString("productCode");
				if(!content.hasNode("user")){
					user = content.addNode("user");
				}else{
					user = content.getNode("user");
				}
				if(!user.hasNode(username)){
					userName = user.addNode(username);
				}else{
					userName = user.getNode(username);
				}
				if(!userName.hasNode("services")){
					services = userName.addNode("services");
				}else{
					services = userName.getNode("services");
				}
				if(!services.hasNode(productcode)){
					serviceCode = services.addNode(productcode);
				}else{
					serviceCode = services.getNode(productcode);
				}
				if(!serviceCode.hasNode(productid)){
					serviceId = serviceCode.addNode(productid);
				}else{
					serviceId = serviceCode.getNode(productid);
				}
				
					//jcr:root/content/user/gaurav.bhandari_damacgroup.com.partial  serivces/doctiger/789/G1
				
			}
			session.save();
			} catch (Exception e) {
				// out.println(e.getMessage());
			}

	}
}
