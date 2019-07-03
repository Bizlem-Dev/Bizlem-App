package biz.com.service;

import java.util.ArrayList;

import javax.jcr.Node;
import org.apache.sling.api.SlingHttpServletResponse;
public interface CompanyService {

public ArrayList<Node> getComapnyList(String d);
public ArrayList<Node> searchGroup(String keyword);
public ArrayList<Node> getPersonList(String searchText);

}