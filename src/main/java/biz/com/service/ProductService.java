package biz.com.service;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import jxl.*;
import jxl.write.*;
import jxl.write.biff.RowsExceededException;

import javax.jcr.Node;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;


//import biz.com.service.impl.WriteException;

//import biz.com.service.impl.WritableSheet;

public interface ProductService{
	public String checkModelExistence(String companyId,String ModelNo);
	 public ArrayList<Node> getProductList(String data); 
	 public ArrayList<Node> getProductListByCatId(String data); 
	    public Long saveProductInfo(SlingHttpServletRequest request, SlingHttpServletResponse response)
	            throws ServletException, IOException;
	    public Long saveProductOtherInfo(SlingHttpServletRequest request, SlingHttpServletResponse response)
	            throws ServletException, IOException;
	    public Long saveProductCatalogInfo(SlingHttpServletRequest request, SlingHttpServletResponse response)
	            throws ServletException, IOException;
	    public Long saveProductStdCatalogInfo(SlingHttpServletRequest request, SlingHttpServletResponse response)
	            throws ServletException, IOException;
	    public HashMap getCategoryByNode(String dNodeStr,
	    		   String qsrchparam, SlingHttpServletRequest request,SlingHttpServletResponse response);
	    public HashMap getCompanyByNode(String dNodeStr,
	    		   String qsrchparam, SlingHttpServletRequest request,SlingHttpServletResponse response);
	    public void createTemplate(SlingHttpServletRequest request, SlingHttpServletResponse response,String data)throws WriteException, RowsExceededException,IOException;
	    public String uploadTemplateProducts(ArrayList<String> t,
	    		ArrayList<String>tech , Node toadd)throws WriteException, RowsExceededException,IOException;
	    public String uploadTemplateProductsBackup(ArrayList<String> t,
	    		ArrayList<String>tech ,ArrayList<String>comm, Node toadd)throws WriteException, RowsExceededException,IOException;
	    public String readXLSTemplateFile(SlingHttpServletRequest request,SlingHttpServletResponse response,String p) throws IOException;
	    public String uploadAttachements(SlingHttpServletRequest request,
				SlingHttpServletResponse response, String pid) throws IOException;
	    public String readCountryMaster(SlingHttpServletRequest request,SlingHttpServletResponse response) throws IOException;
	    public String readStateView(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException;
	    public String readDistrictView(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException;
	    public String readCityView(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException;
	    public String readPincodeView(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException;
	    public String saveChildProduct(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException;
	    public String responseQuoteRfq(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException;
	    public String checkRfqResponseLimit(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException;
	    public ArrayList<String>validateXLSFile(SlingHttpServletRequest request, SlingHttpServletResponse response)throws IOException;
	    public String callGetService(String urlStr, String[] paramName,
				String[] paramValue);
	    public String callPostService(String urlStr, String[] paramName,
				String[] paramValue);
	    public String sendEmailToken(String values[]);
}
