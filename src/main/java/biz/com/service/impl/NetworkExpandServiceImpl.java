package biz.com.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.felix.scr.annotations.Component;

import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.Properties;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.jcr.api.SlingRepository;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import biz.com.service.ActiveMQProducer;
import biz.com.service.EventService;
import biz.com.service.NetworkExpandService;

@SuppressWarnings("unused")
@Component(configurationFactory = true)
@Service(NetworkExpandService.class)
public class NetworkExpandServiceImpl implements NetworkExpandService {
	@Reference
	private SlingRepository repos;


	private ResourceBundle bundle = ResourceBundle.getBundle("server");
	
	@Reference
	ActiveMQProducer producer;

	public String addContact(SlingHttpServletRequest request) {
		Node node, rootNode, contact, otherNode = null;
		Session session;
		//String result=request.getParameter("fname")+request.getParameter("lname")+request.getParameter("email");
		try {
			session = repos.login(new SimpleCredentials("admin", "admin"
					.toCharArray()));
			rootNode = session.getRootNode().getNode("content").getNode("user")
					.getNode(request.getParameter("user").replace("@", "_"));
			//result=result+"stage 1 pass";
			if (rootNode.hasNode("ContactList")) {
				contact = rootNode.getNode("ContactList");
				if (contact.hasNode("Others")) {
					otherNode = contact.getNode("Others");
				} else {
					otherNode = contact.addNode("Others");
				}
			} else {
				contact = rootNode.addNode("ContactList");

				otherNode = contact.addNode("Others");
			}
			//result=result+"stage 2 pass";
			Node emailnode = null;
			String fname[]=request.getParameter("fname").split("~!");
			String lname[]=request.getParameter("lname").split("~!");
			String emails[]=request.getParameter("email").split("~!");
			
			
			for(int p=0;p<emails.length;p++){
				String usr=emails[p];
				if(usr!=null){
				//	result=result+"stage 3pass";
				
			if (otherNode.hasNode(usr.replace("@","_"))) {
				//result=result+"stage 5pass"+usr;
				emailnode = otherNode.getNode(usr
						.replace("@", "_"));
				emailnode.setProperty("importedFirstName",
						fname[p]);
				emailnode.setProperty("importedlastName",
						lname[p]);
				emailnode.setProperty("importedEmail",
						emails[p]);

			} else {
				//re//sult=result+"stage innnn pass"+usr;

				String[] a = { usr };
				callService("", null, a);
				emailnode = otherNode.addNode(usr
						.replace("@", "_"));
				emailnode.setProperty("importedFirstName",
						fname[p]);
				emailnode.setProperty("importedlastName",
						lname[p]);
				emailnode.setProperty("importedEmail",
						emails[p]);
				//zresult=result+"stage 4 pass";

			}}
			}
			session.save();
		} catch (Exception e) {
			return e.getMessage();
		}
		return "success";
	}

	@SuppressWarnings("unused")
	public String uploadImportedContacts(SlingHttpServletRequest request)
			throws ServletException, IOException {

		String userName = request.getParameter("userId");
		String extention = "";
		ArrayList<String> userList = new ArrayList<String>();
		ArrayList<String> contact = new ArrayList<String>();
		String stt = "";
		try {
			for (Entry<String, RequestParameter[]> e : request
					.getRequestParameterMap().entrySet()) {
				for (RequestParameter p : e.getValue()) {
					if (!p.isFormField()) {
						extention = p.getFileName().substring(
								p.getFileName().lastIndexOf("."),
								p.getFileName().length());
						if (extention.equals(".csv")) {

							BufferedReader br = new BufferedReader(
									new InputStreamReader(p.getInputStream()));
							String line = "";
							StringTokenizer st = null;

							int lineNumber = 0;
							int tokenNumber = 0;

							while ((line = br.readLine()) != null) {
								lineNumber++;

								// use comma as token separator
								st = new StringTokenizer(line, ",");
								if (request.getParameter("type") != null
										&& request.getParameter("type").equals(
												"other")) {
									if (lineNumber != 1) {
										userList = new ArrayList<String>();
										while (st.hasMoreTokens()) {
											tokenNumber++;
											userList.add(st.nextToken()
													.replaceAll("\"", ""));
											System.out.println(userList);
										}
										if (userList.get(2) != null
												&& !userList.get(2).equals("")) {
											importContacts(
													userName,
													"Others",
													userList.get(2)
															.replace("@", "_")
															.trim(), userList
															.get(2).trim(),
													userList.get(0).trim(),
													userList.get(1).trim());
										}

									}
								}
							}

						} else {
							InputStream isp = p.getInputStream();
							HSSFWorkbook workbook = new HSSFWorkbook(isp);
							HSSFSheet sheet = workbook.getSheetAt(0);
							Iterator rows = sheet.rowIterator();
							int x = 0;
							DataFormatter formatter = new DataFormatter();
							while (rows.hasNext()) {
								x = 0;
								HSSFRow row = (HSSFRow) rows.next();
								Iterator cells = row.cellIterator();
								while (cells.hasNext()) {
									// creating formatter using the default
									// locale
									HSSFCell cell = (HSSFCell) cells.next();
									switch (x) {
									case 0:
										contact.add(formatter.formatCellValue(
												cell).replace("@", "_"));
										break;
									case 1:
										contact.add(formatter
												.formatCellValue(cell));
										break;
									case 2:
										contact.add(formatter
												.formatCellValue(cell));
										break;
									}
									x++;
								}
							}
							return stt = addImportedContacts(request
									.getRemoteUser().replace("@", "_"),
									"Others", "", contact);
						}
					}
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			return "---error---" + e1.getMessage();
		}
		return stt;
	}

	@SuppressWarnings("unused")
	public void importContacts(String userName, String providerId,
			String emailNode, String email, String firstName, String lastName) {

		Node node, connection, friendNode, lastNode = null;
		Session session;
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();

		try {

			session = repos.login(new SimpleCredentials("admin", "admin"
					.toCharArray()));

			// response.getOutputStream().print("if in");

			node = session.getRootNode().getNode("content").getNode("user")
					.getNode(userName);

			if (node.hasNode("ContactList")) {
				connection = node.getNode("ContactList");
			} else {
				connection = node.addNode("ContactList");
			}
			if (connection.hasNode(providerId)) {
				friendNode = connection.getNode(providerId);
			} else {
				friendNode = connection.addNode(providerId);
			}
			if (friendNode.hasNode(emailNode)) {
				lastNode = friendNode.getNode(emailNode);
			} else {
				lastNode = friendNode.addNode(emailNode);
			}

			lastNode.setProperty("importedEmail", email);
			lastNode.setProperty("importedFirstName", firstName);
			lastNode.setProperty("importedlastName", lastName);
			String a[] = { "userId" };
			String b[] = { email.replace("@", "_") };
			callService(
					"http://prod.bizlem.io:8180/UserValidation/services/UserValidation/raveUserExistence",
					a, b);
			session.save();

		} catch (PathNotFoundException e) {

			// response.getOutputStream().print("else"+ar);
			// request.getRequestDispatcher("posts/*.profile").forward(request,
			// response);
			e.printStackTrace();
		} catch (RepositoryException e) {
			// return pl;
			// response.getOutputStream().print("elsew"+ar);
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unused")
	public String addImportedContacts(String userName, String providerId,
			String emailNode, ArrayList<String> importedValues) {
		String status = "";
		Node node, connection, friendNode, lastNode = null;
		Session session;
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();

		try {

			session = repos.login(new SimpleCredentials("admin", "admin"
					.toCharArray()));

			node = session.getRootNode().getNode("content").getNode("user")
					.getNode(userName);

			if (node.hasNode("ContactList")) {
				connection = node.getNode("ContactList");
			} else {
				connection = node.addNode("ContactList");
			}
			if (connection.hasNode(providerId)) {
				friendNode = connection.getNode(providerId);
			} else {
				friendNode = connection.addNode(providerId);
			}
			String[] a = { "userId" };
			for (int i = 0; i < importedValues.size(); i++) {

				if (friendNode.hasNode(importedValues.get(i).trim())) {

					lastNode = friendNode.getNode(importedValues.get(i).trim());
				} else {

					lastNode = friendNode.addNode(importedValues.get(i).trim());
				}
				String[] b = { importedValues.get(i).trim() };
				lastNode.setProperty("importedEmail", importedValues.get(i++)
						.trim().replace("_", "@"));

				lastNode.setProperty("importedFirstName",
						importedValues.get(i++).trim());

				lastNode.setProperty("importedlastName", importedValues.get(i)
						.trim());

				callService(
						"http://prod.bizlem.io:8180/UserValidation/services/UserValidation/raveUserExistence",
						a, b);
			}
			session.save();
			status = status + "success";
		} catch (Exception e) {

			status = status + e.getMessage();
		}
		return status;

	}

	public String callService(String urlStr, String[] paramName,
			String[] paramValue) {

		String userresult = "";
		ResourceBundle bundle = ResourceBundle.getBundle("server");
		String serviceUrl = bundle.getString("userServiceUrl");
		String url = "";
		try {
			InputStream inputXml = null;
			url = serviceUrl
					+ "/services/UserValidation/raveUserExistence?userId="
					+ paramValue[0].replace("_", "@");
			inputXml = new URL(url).openConnection().getInputStream();

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputXml);
			doc.getDocumentElement().normalize();
			NodeList nList1 = doc
					.getElementsByTagName("ns:raveUserExistenceResponse");
			org.w3c.dom.Node nNode = nList1.item(0);
			Element eElement = (Element) nNode;
			userresult = eElement.getElementsByTagName("ns:return").item(0)
					.getTextContent();
		} catch (MalformedURLException e) {

			userresult = e.getMessage();
		} catch (Exception e) {

			userresult = e.getMessage();
		}
		return userresult;
	}

	public String processInvite(SlingHttpServletRequest request,
			SlingHttpServletResponse responce) {
		String status="not insode";
		try {
			
		if (request.getParameter("invitenodeid").equals("0")) {
			status= inviteEvent(request, responce);
			//status="equals to 0";
		} else if (request.getParameter("invitenodeid").equals("1")) {
			status=  inviteGroup(request, responce);
			//status="equals to 1";
		}else if (request.getParameter("invitenodeid").equals("2")) {
			status=  this.inviteCompany(request, responce);
			//status="equals to 1";
		}else if (request.getParameter("invitenodeid").equals("3")) {
			status=  this.inviteProfile(request, responce);
			//status="equals to 1";
		}
		
		}catch (Exception e) {
			// TODO Auto-generated catch block
			try {
				status=e.getMessage();
				responce.getWriter().print(e.getMessage());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return status;
	}

	public String inviteGroup(SlingHttpServletRequest request,
			SlingHttpServletResponse response) {
String status="";
		try {
			String emails = request.getParameter("emails");
		String[] value=emails.split(",");
		
		Session session = repos.login(new SimpleCredentials("admin",
				"admin".toCharArray()));
		String event = "";
		
		Node node = session.getRootNode().getNode("content")
				.getNode("invite")
				.getNode(request.getParameter("invitenodeid"));
		event = node.getProperty("customtempid").getString();
		
		Node userNode = session.getRootNode().getNode("content").getNode("user")
				.getNode(request.getRemoteUser().replace("@", "_"));
		Node groupode= session.getRootNode().getNode("content")
				.getNode("group");
		String other[]=request.getParameter("others").split(",");
		
		if(groupode.hasNode(request.getParameter("eventId"))){
			groupode=groupode.getNode(request.getParameter("eventId"));
			if(groupode.hasNode("invities")){
				//groupode=groupode.getNode(request.getParameter("groupid"));
				groupode=groupode.getNode("invities");	
			}else{
				//groupode=groupode.getNode(request.getParameter("groupid"));
				groupode=groupode.addNode("invities");
			}
			//=status+"process lie 1 pass";
		String identifier="";
		
		for (int i = 0; i < value.length; i++) {
			if(value[i]!=null && !value[i].equals("")){
				String msg = request.getParameter("messagecustom");
				//map.put("customerId", value[i]);
			status=	this.createUser(value[i]);
				//status=status+"process lie 2 pass";	
			
				if(groupode.hasNode(value[i].replace("@", "_"))){
						
					Node ini=groupode.getNode(value[i].replace("@","_"));
					identifier=	ini.getProperty("identifier").getString();
					}else{
						Node ini=groupode.addNode(value[i].replace("@","_"));
						identifier=	ini.getIdentifier();
						ini.setProperty("identifier",identifier);
					}
				//status=status+"process lie 3 pass";
				
			if (request.getParameter("customtemp") != null
					&& request.getParameter("customtemp").equals("true")) {
				

				//String others[] = request.getParameter("others").split(",");
				
				if (msg.indexOf("{OtherField0}") != -1) {
					msg = msg.replace("{OtherField0}",
							request.getParameter("eventId"));
					if (msg.indexOf("{username}") != -1) {
						msg = msg.replace("{username}",
								userNode.getProperty("name").getString());
					}
					if (msg.indexOf("{OtherField1}") != -1) {
						msg = msg.replace("{OtherField1}",
								identifier);
					}	
					if (msg.indexOf("{OtherField2}") != -1) {
						msg = msg.replace("{OtherField2}",
								value[i]);
					}
					
					
				} else {
					msg = msg
							+ node.getProperty("footer")
									.getString()
									.replace("{OtherField0}",
											request.getParameter("eventId"));
					msg = msg.replace("{OtherField1}",
							identifier);
					msg = msg.replace("{OtherField2}",
							value[i]);
				}
			
				
				//map.put("other0", msg);
			
				//status=status+"process lie4 pass"+msg;
				//response.getWriter().print(msg);

			} else {
				
			 msg = node.getProperty("eventTemplate").getString();
				msg = msg.replace("{OtherField0}",
						request.getParameter("eventId"));
				if (msg.indexOf("{username}") != -1) {
					msg = msg.replace("{username}", userNode
							.getProperty("name").getString());
				}
				if (msg.indexOf("{OtherField1}") != -1) {
					msg = msg.replace("{OtherField1}",
							identifier);
					}
				if (msg.indexOf("{OtherField2}") != -1) {
					msg = msg.replace("{OtherField2}",
							value[i]);
					}
				
				//response.getWriter().print(msg);
			}
			if(msg.indexOf("Link will appear here automatically")!=-1){
				msg=msg.replace("Link will appear here automatically","Click here");
			}
			if(msg.indexOf("Hi,")!=-1){
				Node reverName=session.getRootNode().getNode("content").getNode("user")
						.getNode(value[i].replace("@", "_"));
				if(reverName.hasProperty("name")){
					msg=msg.replace("Hi,", "Hi "+reverName.getProperty("name").getString());
				}
				
			}
			
			msg=node.getProperty("mailtemplate").getString().replace("{messageBody}", msg);
			
			status=this.sendToken(value[i], userNode.getProperty("name").getString()+"<admin@prod.bizlem.io>", other[0], msg, response);
			//producer.producerCall(request.getParameter("mailplatformid"), map,
				//	"");
			status=status+"process  producers pass";
			response.getWriter().print(status+msg);
		}}}
		session.save();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			try {
				status="Exception "+e.getMessage();
				response.getWriter().print(e.getMessage());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return status;

	}
	public String inviteCompany(SlingHttpServletRequest request,
			SlingHttpServletResponse response) {
String status="";
		try {
			String emails = request.getParameter("emails");
		String[] value=emails.split(",");
		
		Session session = repos.login(new SimpleCredentials("admin",
				"admin".toCharArray()));
		String event = "";
		
		Node node = session.getRootNode().getNode("content")
				.getNode("invite")
				.getNode(request.getParameter("invitenodeid"));
		event = node.getProperty("customtempid").getString();
		
		Node userNode = session.getRootNode().getNode("content").getNode("user")
				.getNode(request.getRemoteUser().replace("@", "_"));
		Node groupode= session.getRootNode().getNode("content")
				.getNode("company");
		String other[]=request.getParameter("others").split(",");
		
		if(groupode.hasNode(request.getParameter("eventId"))){
			groupode=groupode.getNode(request.getParameter("eventId"));
			if(groupode.hasNode("invities")){
				//groupode=groupode.getNode(request.getParameter("groupid"));
				groupode=groupode.getNode("invities");	
			}else{
				//groupode=groupode.getNode(request.getParameter("groupid"));
				groupode=groupode.addNode("invities");
			}
			//=status+"process lie 1 pass";
		String identifier="";
		
		for (int i = 0; i < value.length; i++) {
			if(value[i]!=null && !value[i].equals("")){
				String msg = request.getParameter("messagecustom");
				//map.put("customerId", value[i]);
			status=	this.createUser(value[i]);
				//status=status+"process lie 2 pass";	
			
				if(groupode.hasNode(value[i].replace("@", "_"))){
						
					Node ini=groupode.getNode(value[i].replace("@","_"));
					identifier=	ini.getProperty("identifier").getString();
					}else{
						Node ini=groupode.addNode(value[i].replace("@","_"));
						identifier=	ini.getIdentifier();
						ini.setProperty("identifier",identifier);
					}
				
				
			if (request.getParameter("customtemp") != null
					&& request.getParameter("customtemp").equals("true")) {
				

				//String others[] = request.getParameter("others").split(",");
				
				if (msg.indexOf("{OtherField0}") != -1) {
					msg = msg.replace("{OtherField0}",
							request.getParameter("eventId"));
					if (msg.indexOf("{username}") != -1) {
						msg = msg.replace("{username}",
								userNode.getProperty("name").getString());
					}
					if (msg.indexOf("{OtherField1}") != -1) {
						msg = msg.replace("{OtherField1}",
								identifier);
					}	
					if (msg.indexOf("{OtherField2}") != -1) {
						msg = msg.replace("{OtherField2}",
								value[i]);
					}
					
					if (msg.indexOf("{OtherField3}") != -1) {
						msg = msg.replace("{OtherField3}",
								other[1]);
					}
					
					
				} else {
					msg = msg
							+ node.getProperty("footer")
									.getString()
									.replace("{OtherField0}",
											request.getParameter("eventId"));
					msg = msg.replace("{OtherField1}",
							identifier);
					msg = msg.replace("{OtherField2}",
							value[i]);
					
						msg = msg.replace("{OtherField3}",
								other[1]);
					
				}
			
				
				//map.put("other0", msg);
			
				//status=status+"process lie4 pass"+msg;
				//response.getWriter().print(msg);

			} else {
				
			 msg = node.getProperty("eventTemplate").getString();
				msg = msg.replace("{OtherField0}",
						request.getParameter("eventId"));
				if (msg.indexOf("{username}") != -1) {
					msg = msg.replace("{username}", userNode
							.getProperty("name").getString());
				}
				if (msg.indexOf("{OtherField1}") != -1) {
					msg = msg.replace("{OtherField1}",
							identifier);
					}
				if (msg.indexOf("{OtherField2}") != -1) {
					msg = msg.replace("{OtherField2}",
							value[i]);
					}
				if (msg.indexOf("{OtherField3}") != -1) {
					msg = msg.replace("{OtherField3}",
							other[1]);
				}
				//response.getWriter().print(msg);
			}
			if(msg.indexOf("Link will appear here automatically")!=-1){
				msg=msg.replace("Link will appear here automatically","Click here");
			}
			if(msg.indexOf("Hi,")!=-1){
				Node reverName=session.getRootNode().getNode("content").getNode("user")
						.getNode(value[i].replace("@", "_"));
				if(reverName.hasProperty("name")){
					msg=msg.replace("Hi,", "Hi "+reverName.getProperty("name").getString());
				}
				
			}
			
			msg=node.getProperty("mailtemplate").getString().replace("{messageBody}", msg);
			
			status=this.sendToken(value[i], userNode.getProperty("name").getString()+"<admin@prod.bizlem.io>", other[0], msg, response);
			//producer.producerCall(request.getParameter("mailplatformid"), map,
				//	"");
			status=status+"process  producers pass";
			response.getWriter().print(status+msg);
		}}}
		session.save();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			try {
				status="Exception "+e.getMessage();
				response.getWriter().print(e.getMessage());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return status;

	}

	public String inviteProfile(SlingHttpServletRequest request,
			SlingHttpServletResponse response) {
String status="";
		try {
			String emails = request.getParameter("emails");
		String[] value=emails.split(",");
		
		Session session = repos.login(new SimpleCredentials("admin",
				"admin".toCharArray()));
		String event = "";
		
		Node node = session.getRootNode().getNode("content")
				.getNode("invite")
				.getNode(request.getParameter("invitenodeid"));
		event = node.getProperty("customtempid").getString();
		
		Node userNode = session.getRootNode().getNode("content").getNode("user")
				.getNode(request.getRemoteUser().replace("@", "_"));
		Node groupode= session.getRootNode().getNode("content").getNode("user").getNode(request.getRemoteUser().replace("@", "_"));
		String other[]=request.getParameter("others").split(",");
		
		
			
			if(groupode.hasNode("invities")){
				//groupode=groupode.getNode(request.getParameter("groupid"));
				groupode=groupode.getNode("invities");	
			}else{
				//groupode=groupode.getNode(request.getParameter("groupid"));
				groupode=groupode.addNode("invities");
			}
			//=status+"process lie 1 pass";
		String identifier="";
		
		for (int i = 0; i < value.length; i++) {
			if(value[i]!=null && !value[i].equals("")){
				String msg = request.getParameter("messagecustom");
				//map.put("customerId", value[i]);
			status=	this.createUser(value[i]);
				//status=status+"process lie 2 pass";	
			
				if(groupode.hasNode(value[i].replace("@", "_"))){
						
					Node ini=groupode.getNode(value[i].replace("@","_"));
					identifier=	ini.getProperty("identifier").getString();
					}else{
						Node ini=groupode.addNode(value[i].replace("@","_"));
						identifier=	ini.getIdentifier();
						ini.setProperty("identifier",identifier);
					}
				
				
			if (request.getParameter("customtemp") != null
					&& request.getParameter("customtemp").equals("true")) {
				

				//String others[] = request.getParameter("others").split(",");
				
				if (msg.indexOf("{OtherField0}") != -1) {
					msg = msg.replace("{OtherField0}",
							request.getParameter("eventId"));
					if (msg.indexOf("{username}") != -1) {
						msg = msg.replace("{username}",
								userNode.getProperty("name").getString());
					}
					if (msg.indexOf("{OtherField1}") != -1) {
						msg = msg.replace("{OtherField1}",
								identifier);
					}	
					if (msg.indexOf("{OtherField2}") != -1) {
						msg = msg.replace("{OtherField2}",
								value[i]);
					}
					
					
					
				} else {
					msg = msg
							+ node.getProperty("footer")
									.getString()
									.replace("{OtherField0}",
											request.getParameter("eventId"));
					msg = msg.replace("{OtherField1}",
							identifier);
					msg = msg.replace("{OtherField2}",
							value[i]);
					
					
				}
			
				
				//map.put("other0", msg);
			
				//status=status+"process lie4 pass"+msg;
				//response.getWriter().print(msg);

			} else {
				
			 msg = node.getProperty("eventTemplate").getString();
				msg = msg.replace("{OtherField0}",
						request.getParameter("eventId"));
				if (msg.indexOf("{username}") != -1) {
					msg = msg.replace("{username}", userNode
							.getProperty("name").getString());
				}
				if (msg.indexOf("{OtherField1}") != -1) {
					msg = msg.replace("{OtherField1}",
							identifier);
					}
				if (msg.indexOf("{OtherField2}") != -1) {
					msg = msg.replace("{OtherField2}",
							value[i]);
					}
				
				//response.getWriter().print(msg);
			}
			if(msg.indexOf("Link will appear here automatically")!=-1){
				msg=msg.replace("Link will appear here automatically","Click here");
			}
			if(msg.indexOf("Hi,")!=-1){
				Node reverName=session.getRootNode().getNode("content").getNode("user")
						.getNode(value[i].replace("@", "_"));
				if(reverName.hasProperty("name")){
					msg=msg.replace("Hi,", "Hi "+reverName.getProperty("name").getString());
				}
				
			}
			
			msg=node.getProperty("mailtemplate").getString().replace("{messageBody}", msg);
			
			status=this.sendToken(value[i], userNode.getProperty("name").getString()+"<admin@prod.bizlem.io>", other[0], msg, response);
			//producer.producerCall(request.getParameter("mailplatformid"), map,
				//	"");
			status=status+"process  producers pass";
			response.getWriter().print(status+msg);
		}}
		session.save();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			try {
				status="Exception "+e.getMessage();
				response.getWriter().print(e.getMessage());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return status;

	}

	public String inviteEvent(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws IOException {
		String status="";
		try {
			String emails = request.getParameter("emails");
		String[] value=emails.split(",");
		
		Session session = repos.login(new SimpleCredentials("admin",
				"admin".toCharArray()));
		String event = "";
		
		Node node = session.getRootNode().getNode("content")
				.getNode("invite")
				.getNode(request.getParameter("invitenodeid"));
		event = node.getProperty("customtempid").getString();
		
		Node userNode = session.getRootNode().getNode("content").getNode("user")
				.getNode(request.getRemoteUser().replace("@", "_"));
		
		String other[]=request.getParameter("others").split(",");
		
	
		
		for (int i = 0; i < value.length; i++) {
			if(value[i]!=null && !value[i].equals("")){
				String msg = request.getParameter("messagecustom");
				//map.put("customerId", value[i]);
			status=	this.createUser(value[i]);
				//status=status+"process lie 2 pass";	
			
				
				
			if (request.getParameter("customtemp") != null
					&& request.getParameter("customtemp").equals("true")) {
				

				//String others[] = request.getParameter("others").split(",");
				
				if (msg.indexOf("{OtherField0}") != -1) {
					msg = msg.replace("{OtherField0}",
							request.getParameter("eventId"));
					if (msg.indexOf("{username}") != -1) {
						msg = msg.replace("{username}",
								userNode.getProperty("name").getString());
					}
				} else {
					msg = msg
							+ node.getProperty("footer")
									.getString()
									.replace("{OtherField0}",
											request.getParameter("eventId"));
					
				}
			
				
			
				response.getWriter().print(msg);

			} else {
				
			 msg = node.getProperty("eventTemplate").getString();
				msg = msg.replace("{OtherField0}",
						request.getParameter("eventId"));
				if (msg.indexOf("{username}") != -1) {
					msg = msg.replace("{username}", userNode
							.getProperty("name").getString());
				}
				
				response.getWriter().print(msg);
			}
			if(msg.indexOf("Link will appear here automatically")!=-1){
				msg=msg.replace("Link will appear here automatically","Click here");
			}
			if(msg.indexOf("Hi,")!=-1){
				Node reverName=session.getRootNode().getNode("content").getNode("user")
						.getNode(value[i].replace("@", "_"));
				if(reverName.hasProperty("name")){
					msg=msg.replace("Hi,", "Hi "+reverName.getProperty("name").getString());
				}
				
			}
			msg=node.getProperty("mailtemplate").getString().replace("{messageBody}", msg);
			status=this.sendToken(value[i], userNode.getProperty("name").getString()+"<admin@prod.bizlem.io>", other[0], msg, response);
			//producer.producerCall(request.getParameter("mailplatformid"), map,
				//	"");
			status=status+"process  producers pass";
			response.getWriter().print(status+msg);
		}}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			try {
				status=e.getMessage();
				response.getWriter().print(e.getMessage());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return status;
	}

	/**
	 * @param emails
	 * @return
	 */
	public String createUser(String emails) {
String status="";
		try {
//			ResourceBundle bundle = ResourceBundle.getBundle("serverConfig");
//			String serviceUrl = bundle.getString("userServiceUrl");
//
			String url = "";
			InputStream inputXml = null;
//			url = serviceUrl
//					+ "/services/UserValidation/raveUserExistence?userId="
//					+ emails;
			
			url = "http://prod.bizlem.io:8180/UserValidation/services/UserValidation/raveUserExistence?userId="
					+ emails;
			// customerDeatilList = new ArrayList<HashMap<String,
			// String>>();

			inputXml = new URL(url).openConnection().getInputStream();

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputXml);
			doc.getDocumentElement().normalize();
			// NodeList nList = doc.getElementsByTagName("ns:return");
			// String userresult = nList.toString();
			NodeList nList1 = doc
					.getElementsByTagName("ns:raveUserExistenceResponse");
			org.w3c.dom.Node nNode = nList1.item(0);
			Element eElement = (Element) nNode;
			// System.out.println(""+eElement.getElementsByTagName("ns:return").item(0).getTextContent());
			 status = eElement.getElementsByTagName("ns:return")
					.item(0).getTextContent();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			status=e.getMessage();
			e.printStackTrace();
		}
		return status;
	}
	
	 public String sendToken(String emailTo, String emailFrom, String sub, String msg, SlingHttpServletResponse response) throws ServletException,IOException {		 
		 	try{
		 		 String url = bundle.getString("sendMail.address");	               
	                String[] paramName = { "emailto[]", "emailfrom[]",
	                        "emailsub[]", "emailmsg[]" };
	               
	                String[] paramValue = { emailTo,
	                		emailFrom,
	                		sub, msg };
	                
	               return url+callService(url, paramName, paramValue,response);
	              
	                
		 		        }catch (Exception e) {
		 		        	return "error";
	            //e.printStackTrace();
	            
	        }

	    }
	 
	 public String callService(String urlStr, String[] paramName,
	            String[] paramValue,SlingHttpServletResponse response) {
	    	
	    	PrintWriter pout = null;
	        StringBuilder sb = new StringBuilder();
	        URL url;
	        try {
	        	pout=response.getWriter();
	            url = new URL(urlStr);
	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	            conn.setRequestMethod("POST");
	            conn.setDoOutput(true);
	            conn.setDoInput(true);
	            conn.setUseCaches(false);
	            conn.setAllowUserInteraction(false);
	            conn.setRequestProperty("Content-Type",
	                    "application/x-www-form-urlencoded");

	            // Create the form content
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
	                throw new IOException(conn.getResponseMessage());
	            }
	            // Buffer the result into a string
	            BufferedReader rd = new BufferedReader(new InputStreamReader(
	                    conn.getInputStream()));
	            sb = new StringBuilder();
	            String line;
	            while ((line = rd.readLine()) != null) {
	                sb.append(line);
	            }
	            rd.close();

	            conn.disconnect();
	        } catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}catch (Exception e) {
	            // TODO Auto-generated catch block
	           // e.printStackTrace();
	        	pout.print(e.getMessage());

	        }
	        return sb.toString();
	    }

}
