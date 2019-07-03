package biz.com.service;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;

import org.apache.sling.api.SlingHttpServletRequest;

public interface EventService {

public ArrayList<Node> getEventList(SlingHttpServletRequest r,String d,String d1);
public ArrayList<Node> getnewsList(SlingHttpServletRequest request);
public ArrayList<Node> getPhotoStoryListList(SlingHttpServletRequest request);
public String getHotProductSearchText(SlingHttpServletRequest request);
public String getHotSellerSearchText(SlingHttpServletRequest request);
public String getProductSearchText(SlingHttpServletRequest request);
public List getCustomerDetails(SlingHttpServletRequest RE,String customerId, PrintWriter out) ;
public String getPhotoStorySearchText(SlingHttpServletRequest request);
public String getRandomNumber(SlingHttpServletRequest request,String widgetName);
public String getUserPassword(String uid);

}
