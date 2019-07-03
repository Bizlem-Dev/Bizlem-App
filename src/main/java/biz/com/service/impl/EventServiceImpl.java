package biz.com.service.impl;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.ehcache.Cache;

import org.apache.felix.scr.annotations.Component;

import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.Properties;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.jcr.api.SlingRepository;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import biz.com.app.servlet.EhCacheManager;
import biz.com.service.EventService;

@Component(configurationFactory = true)
@Service(EventService.class)
@Properties({ @Property(name = "com", value = "com") })
public class EventServiceImpl implements EventService {
	@Reference
	private SlingRepository repo;
	private ResourceBundle bundle;
	Session session = null;

	public ArrayList<Node> getEventList(SlingHttpServletRequest request,String searchText, String condition) {
		ArrayList<Node> pb = null;
		
		Cache indexCache = EhCacheManager.getCache();
		
		net.sf.ehcache.Element ele = indexCache.get("exh");
		
		if(ele == null){
			System.out.println("Cache miss");
			System.out.println("Fetching data from databse");
			 
			Node tempPrdNode, childNode;

			if (!searchText.trim().equals("")) {
				try {

					pb = new ArrayList<Node>();
					session = repo.login(new SimpleCredentials("admin", "admin"
							.toCharArray()));

					//String querryStr = "select * from [nt:unstructured] where  ISDESCENDANTNODE('/content/exhibition/') and "
					//		+ condition + "  like '" + this.getRandomNumber(request, "event") + "%'";
					
					String querryStr = "select * from [nt:unstructured] where   contains('catName','%Agriculture%') and ISDESCENDANTNODE('/content/exhibition/')";
					Workspace workspace = session.getWorkspace();
					Query query = workspace.getQueryManager().createQuery(
							querryStr, Query.JCR_SQL2);
					QueryResult result = query.execute();
					NodeIterator iterator = result.getNodes();

					while (iterator.hasNext()) {

						tempPrdNode = iterator.nextNode();
						tempPrdNode = tempPrdNode.getParent();
						tempPrdNode = tempPrdNode.getParent();
						pb.add(tempPrdNode);

					}

				} catch (LoginException e) {

				} catch (RepositoryException e) {
					// TODO Auto-generated catch block

				} catch (Exception e) {
					// TODO Auto-generated catch block

				}
			}
			
			indexCache.put(new net.sf.ehcache.Element("exh", pb));
			
			
		}else{
			System.out.println("Reading from teh cache");
			pb = (ArrayList<Node>)ele.getObjectValue();
			System.out.println("start 4--"+pb.size());		
			
			
		}

		
		return pb;
	}

	public List getCustomerDetails(SlingHttpServletRequest request,
			String customerId, PrintWriter out) {
		bundle = ResourceBundle.getBundle("serverConfig");
		InputStream inputXml = null;
		String content = "";
		String serviceUrl = "";
		String url = "";
		HashMap<String, String> map = null;
		List<HashMap<String, String>> customerDeatilList = null;
		String currentDate = new SimpleDateFormat("yyyy-MM-dd")
				.format(new Date());
//		String bannerService = "Home_Left,Home_Right,Home_Top,Home_bottom";
		String bannerService = bundle.getString("bannerServices");
		String[] bannerListArray = bannerService.split(",");
		ArrayList<String> bannerList = new ArrayList<String>(
				Arrays.asList(bannerListArray));
		Date endDate = new Date();
		endDate.setDate(endDate.getDate() - 1);
		Date startDate = new Date();
		String url1 = "";

		try {
			session = repo.login(new SimpleCredentials("admin", "admin"
					.toCharArray()));
			if (session.getNode("/content/ip").hasProperty("auctionService")) {
				serviceUrl = session.getNode("/content/ip")
						.getProperty("auctionService").getString();
			}
			url = serviceUrl
					+ "/services/Auctions_WSDL/getCustomerServiceStatus?customerId="
					+ customerId;
			customerDeatilList = new ArrayList<HashMap<String, String>>();
			inputXml = new URL(url).openConnection().getInputStream();

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputXml);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("ns:return");
			
			for (int temp = 0; temp < nList.getLength(); temp++) {
				url1="";
				org.w3c.dom.Node nNode = nList.item(temp);
				if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					map = new HashMap<String, String>();
					Element eElement = (Element) nNode;
					map.put("orderId",
							eElement.getElementsByTagName("ax21:orderId")
									.item(0).getTextContent());
					map.put("productCode",
							eElement.getElementsByTagName("ax21:productCode")
									.item(0).getTextContent());
					map.put("configId",
							eElement.getElementsByTagName("ax21:configId")
									.item(0).getTextContent());
					map.put("consumptionQuantity", eElement
							.getElementsByTagName("ax21:consumptionQuantity")
							.item(0).getTextContent());
					map.put("customerId",
							eElement.getElementsByTagName("ax21:customerId")
									.item(0).getTextContent());
					map.put("customerName",
							eElement.getElementsByTagName("ax21:customerName")
									.item(0).getTextContent());
					map.put("lastConsumptionDate", eElement
							.getElementsByTagName("ax21:lastConsumptionDate")
							.item(0).getTextContent());
					map.put("productDescription", eElement
							.getElementsByTagName("ax21:productDescription")
							.item(0).getTextContent());
					map.put("quantity",
							eElement.getElementsByTagName("ax21:quantity")
									.item(0).getTextContent());
					map.put("rfq", eElement.getElementsByTagName("ax21:rfq")
							.item(0).getTextContent());
					map.put("serviceEndDate",
							eElement.getElementsByTagName("ax21:serviceEndDate")
									.item(0).getTextContent());
					map.put("serviceStartDate",
							eElement.getElementsByTagName(
									"ax21:serviceStartDate").item(0)
									.getTextContent());
					map.put("status",
							eElement.getElementsByTagName("ax21:status")
									.item(0).getTextContent());
					map.put("serviceId", eElement.getElementsByTagName("ax21:serviceId").item(0).getTextContent());
					String productCode = eElement
							.getElementsByTagName("ax21:productCode").item(0)
							.getTextContent();
					String sdate = eElement
							.getElementsByTagName("ax21:serviceStartDate")
							.item(0).getTextContent();
					String edate = eElement
							.getElementsByTagName("ax21:serviceEndDate")
							.item(0).getTextContent();

					// if(startDate.toString().compareTo(sdate) > 0 &&
					// (endDate.toString().compareTo(edate) < 0)){
					// String endServiceDate = new
					// SimpleDateFormat("yyyy-MM-dd").format(edate);

					String protocol = bundle.getString("services");
			        String a[] = protocol.split(",");
			        List<String> service = new ArrayList<String>();
			        for (String s : a) {
			            service.add(s);
			        }
			        double quantity=Double.valueOf(eElement.getElementsByTagName("ax21:quantity").item(0).getTextContent().toString());
			        long longquantity=(long)quantity;
					
					if (productCode.equalsIgnoreCase(service.get(0)) || productCode.equalsIgnoreCase(service.get(1))) {
						url1 = bundle.getString("chatnvchat") + "&val=" + longquantity + "&productCode=" + productCode
								+ "&serviceId="
								+ eElement.getElementsByTagName("ax21:serviceId").item(0).getTextContent().toString();

					} else if (productCode.equalsIgnoreCase(service.get(13))) {

						url1 = bundle.getString("PaidAdd") + "?startdate=" + currentDate + "&enddate="
								+ eElement.getElementsByTagName("ax21:serviceEndDate").item(0).getTextContent()
								+ "&username=" + customerId + "&serviceId="
								+ eElement.getElementsByTagName("ax21:serviceId").item(0).getTextContent().toString();

					} else if (productCode.equalsIgnoreCase(service.get(14))) {
						url1 = bundle.getString("SponsoredAdd") + "?startdate=" + currentDate + "&enddate="
								+ eElement.getElementsByTagName("ax21:serviceEndDate").item(0).getTextContent()
								+ "&username=" + customerId + "&serviceId="
								+ eElement.getElementsByTagName("ax21:serviceId").item(0).getTextContent().toString();

					} else if (bannerList.contains(productCode)) {
						url1 = bundle.getString("bannerAdd") + "?startdate=" + currentDate + "&enddate="
								+ eElement.getElementsByTagName("ax21:serviceEndDate").item(0).getTextContent()
								+ "&username=" + customerId + "&zonename=" + productCode + "&serviceId="
								+ eElement.getElementsByTagName("ax21:serviceId").item(0).getTextContent().toString();

					} else if (productCode.equalsIgnoreCase(service.get(18))) {

						url1 = bundle.getString("webmail") + "&val=" + longquantity + "&productCode=" + productCode;

					} else if (productCode.equalsIgnoreCase(service.get(2))
							|| productCode.equalsIgnoreCase(service.get(16))
							|| productCode.equalsIgnoreCase(service.get(11))) {
						url1 = bundle.getString("emailnsmsncall") + "&productCode=" + productCode;
					} else if (productCode.equalsIgnoreCase(service.get(3))) {

						url1 = bundle.getString("dcamp") + "&productCode=" + productCode;

					} else if (productCode.equalsIgnoreCase(service.get(4))) {

						url1 = bundle.getString("dlime") + "&productCode=" + productCode;

					} else if (productCode.equalsIgnoreCase(service.get(5))) {

						url1 = bundle.getString("dADHome") + "&productCode=" + productCode;

					} else if (productCode.equalsIgnoreCase(service.get(6))) {

						url1 = bundle.getString("dWebConf") + "&productCode=" + productCode;

					} else if (productCode.equalsIgnoreCase(service.get(7))) {

						url1 = bundle.getString("dChatLog") + "&productCode=" + productCode;

					} else if (productCode.equalsIgnoreCase(service.get(8))) {

						url1 = bundle.getString("d360_Deg") + "&productCode=" + productCode;

					} else if (productCode.equalsIgnoreCase(service.get(9))) {

						url1 = bundle.getString("dOnlineExam") + "&productCode=" + productCode;

					} else if (productCode.equalsIgnoreCase(service.get(10))) {

						url1 = bundle.getString("dcs") + "&productCode=" + productCode;

					} else if (productCode.equalsIgnoreCase(service.get(12))) {

						url1 = bundle.getString("dMailPlatform") + "&productCode=" + productCode;

					} else if (productCode.equalsIgnoreCase(service.get(15))) {

						url1 = bundle.getString("dcallMB") + "&val=" + longquantity + "&productCode=" + productCode
								+ "&serviceId="
								+ eElement.getElementsByTagName("ax21:serviceId").item(0).getTextContent().toString();

					} else if (productCode.equalsIgnoreCase(service.get(17))) {

						url1 = bundle.getString("dhelpdesk") + "&productCode=" + productCode;

					} else if (productCode.equalsIgnoreCase(service.get(19))) {
						url1 = bundle.getString("mdmservice") + "&val=" + longquantity + "&productCode=" + productCode
								+ "&serviceId="
								+ eElement.getElementsByTagName("ax21:serviceId").item(0).getTextContent().toString();

					} else if (productCode.equalsIgnoreCase(service.get(20))) {
						url1 = bundle.getString("livechat") + "&val=" + longquantity + "&productCode=" + productCode
								+ "&serviceId="
								+ eElement.getElementsByTagName("ax21:serviceId").item(0).getTextContent().toString();

					} else if (productCode.equalsIgnoreCase(service.get(21))) {
						url1 = bundle.getString("rfqservice") + "?serviceId="
								+ eElement.getElementsByTagName("ax21:serviceId").item(0).getTextContent().toString();

					} else if (productCode.toLowerCase().indexOf(service.get(22)) != -1) {
						url1 = bundle.getString("provisioning_d") + "&val=" + longquantity + "&productCode="
								+ productCode + "&serviceId=" + eElement.getElementsByTagName("ax21:serviceId").item(0)
										.getFirstChild().getNodeValue().toString()
								+"&configId=" + eElement.getElementsByTagName("ax21:configId")
								.item(0).getTextContent()+"&sdate="+sdate+"&edate="+edate;
					}
					// /}
					map.put("url", url1);
					customerDeatilList.add(map);
				}

			}

		} catch (Exception e) {
			out.println(e);

		} finally {
//			session.logout();

		}
		return customerDeatilList;

	}

	public String getHotProductSearchText(SlingHttpServletRequest request) {

		return this.getRandomNumber(request, "hotproduct");
	}

	public String getProductSearchText(SlingHttpServletRequest request) {

		return this.getRandomNumber(request, "product");
	}

	public ArrayList<Node> getnewsList(SlingHttpServletRequest request) {
		Node tempPrdNode, childNode;
		ArrayList<Node> pb = null;
		if (!"test".trim().equals("")) {
			try {

				pb = new ArrayList<Node>();
				session = repo.login(new SimpleCredentials("admin", "admin"
						.toCharArray()));

				String querryStr = "select * from [nt:unstructured] where  ISDESCENDANTNODE('/content/news/') and Title  like '"+this.getRandomNumber(request, "news")+"%'";
				Workspace workspace = session.getWorkspace();
				Query query = workspace.getQueryManager().createQuery(
						querryStr, Query.JCR_SQL2);
				QueryResult result = query.execute();
				NodeIterator iterator = result.getNodes();

				while (iterator.hasNext()) {

					tempPrdNode = iterator.nextNode();
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

	public ArrayList<Node> getPhotoStoryListList(SlingHttpServletRequest request) {
		Node tempPrdNode, childNode;
		ArrayList<Node> pb = null;
		if (!"test".trim().equals("")) {
			try {

				pb = new ArrayList<Node>();
				session = repo.login(new SimpleCredentials("admin", "admin"
						.toCharArray()));

				String querryStr = "select * from [nt:unstructured] where  ISDESCENDANTNODE('/content/photo/') and title  like '"
						+ this.getRandomNumber(request, "photoStory")+ "%'";
				Workspace workspace = session.getWorkspace();
				Query query = workspace.getQueryManager().createQuery(
						querryStr, Query.JCR_SQL2);
				QueryResult result = query.execute();
				NodeIterator iterator = result.getNodes();

				while (iterator.hasNext()) {

					tempPrdNode = iterator.nextNode();
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

	public String getHotSellerSearchText(SlingHttpServletRequest request) {
		return this.getRandomNumber(request, "hotseller");
	}

	public String getPhotoStorySearchText(SlingHttpServletRequest request) {
		
		return this.getRandomNumber(request, "photoStory");
	}

	public String getRandomNumber(SlingHttpServletRequest request,String widgetName) {
		Random ran = new Random();
		int x = 0;//;ran.nextInt(1);
		if (widgetName.equals("event")) {
			switch(x){
			case 0:return "Pump";
			case 1:return "Machine";
			case 2:return "Electric";
			case 3:return "Information";
			case 4:return "Software";
			//case 5:return "Pump";
			}
		} else if (widgetName.equals("hotproduct")) {
			switch(x){
			case 0:return "cycle";
			case 1:return "motor";
			case 2:return "cloth";
			case 3:return "mobile";
			case 4:return "bottle";
			//case 5:return "bag";
			}

		} else if (widgetName.equals("hotseller")) {
			switch(x){
			case 0:return "seller";
			case 1:return "Pump";
			case 2:return "maruti";
			case 3:return "amul";
			case 4:return "flipcart";
			//case 5:return "amazon";
			}
		} else if (widgetName.equals("topnews")) {
			switch(x){
			case 0:return "t";
			case 1:return "t";
			case 2:return "t";
			case 3:return "t";
			case 4:return "t";
			//case 5:return "t";
			}
		} else if (widgetName.equals("news")) {
			switch(x){
			case 0:return "T";
			case 1:return "science";
			case 2:return "infomation";
			case 3:return "technology";
			case 4:return "envoirment";
			//case 5:return "market";
			}
		} else if (widgetName.equals("product")) {
			switch(x){
			case 0:return "cycle";
			case 1:return "pump";
			case 2:return "machine";
			case 3:return "mobile";
			case 4:return "bag";
			//case 5:return "bag";
			}
		} else if (widgetName.equals("photoStory")) {
			switch(x){
			case 0:return "T";
			case 1:return "varanasi";
			case 2:return "T";
			case 3:return "Mumbai";
			case 4:return "Shahrukh";
			//case 5:return "Amitabh";
			}
		}

		return x + "";
	}
	
	
	public String getUserPassword(String uid) {
		Connection conn = null;  
		String password="";
		String entityId="";
		ResultSet rs=null;
		try {
			conn =DriverManager.getConnection("jdbc:mysql://localhost:3306/rave2?" +
			        "user=root&password=password");
			String sql="select password,entity_id from person where username='"+uid+"'";
			java.sql.Statement smt=conn.createStatement();
			rs=smt.executeQuery(sql);
		
			while(rs.next()){
			password=rs.getString("password");
			entityId=rs.getString("entity_id");
		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//password="fail"+e.getMessage();
		}
		return password;

	}
}
