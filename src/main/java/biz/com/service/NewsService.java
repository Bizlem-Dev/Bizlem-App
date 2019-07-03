package biz.com.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.jcr.Node;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

public interface NewsService{
	public ArrayList<Node> getNewsList(String search);

	public String addtoDb(SlingHttpServletRequest respo,String id,int code);
	String callPostService(String urlStr, String[] paramName,
			String[] paramValue);
	StringBuilder callGetService(String urlStr, String[] paramName,
				String[] paramValue);
	 public HashMap<String, Object> bzlem(String url);
	 public HashMap<String, Object> ebay(String url) ;
	 public HashMap<String, Object> amazon(String url) ;
	 public HashMap<String, Object> alibaba(String url) ;
}
