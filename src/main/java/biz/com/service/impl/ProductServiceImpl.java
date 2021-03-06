package biz.com.service.impl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.io.*;
import java.util.*;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.output.*;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.output.*;

import jxl.*;
import jxl.write.*;
import jxl.write.biff.*;

import javax.jcr.ItemExistsException;
import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Workspace;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.jcr.version.VersionException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import javax.servlet.ServletException;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;

import biz.com.service.ProductService;
import biz.com.service.TemplateDataBean;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;


//import biz.com.service.WriteException;

/**
 * 
 * @author atul
 */
@SuppressWarnings("unused")
@Component(configurationFactory = true)
@Service(ProductService.class)
@Properties({ @Property(name = "com", value = "com") })
public class ProductServiceImpl implements ProductService {

	/** The repo variable is an object of SlingRepository interface. */

	
	@Reference
	private SlingRepository repo;
	final String VIDEOEXTENSION[] = { ".3g2", ".3gp", ".asf", ".asx", ".avi",
			".flv", ".mov", ".mp4", ".mpg", ".rm", ".swf", ".vob", ".wmv" };

	final String dataTypeArray[]={"String","Double","Integer","Long"};
	
	public ArrayList<Node> getProductList(String searchText) {
		ArrayList<Node> pb = null;
		Session session = null;
		Node tempPrdNode, childNode;

		if (!searchText.trim().equals("")) {
			try {

				pb = new ArrayList<Node>();
				session = repo.login(new SimpleCredentials("admin", "admin"
						.toCharArray()));

				// String querryStr =
				// "select * from [nt:unstructured] where  ISDESCENDANTNODE('/content/product/products/') and (contains('name','*"+searchText
				// +"*'))";
				String querryStr = "select [name] from [nt:base] where (contains('name','*"
						+ searchText
						+ "*'))  and ISDESCENDANTNODE('/content/product/products/')";

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

	public String checkModelExistence(String companyId, String ModelNo) {
		Node tempPrdNode, prodNode, childNode, prodCmpProd, prodCmpNode;
		String truevalue = "true";
		String falsevalue = "false";
		Session session = null;
		if (!ModelNo.trim().equals("")) {
			try {
				session = repo.login(new SimpleCredentials("admin", "admin"
						.toCharArray()));
				prodCmpNode = session.getRootNode().getNode("content")
						.getNode("company").getNode(companyId);
				if (prodCmpNode.hasNode("product")) {
					prodCmpProd = prodCmpNode.getNode("product");
					if (prodCmpProd.hasNodes()) {
						NodeIterator iterator = prodCmpProd.getNodes();

						while (iterator.hasNext()) {

							tempPrdNode = iterator.nextNode();
							prodNode = session.getRootNode().getNode("content")
									.getNode("product").getNode("products")
									.getNode(tempPrdNode.getName());
							if (prodNode.hasProperty("modelno")) {
								String strModelNo = prodNode.getProperty(
										"modelno").getString();
								if (strModelNo.trim().equals(ModelNo.trim())) {
									return truevalue;
								} else {
									// /return falsevalue;
								}
							} else {
								// return falsevalue;
							}
						}
					} else {
						return falsevalue;
					}
				} else {
					return falsevalue;
				}

			} catch (LoginException e) {
				return falsevalue;
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				return falsevalue;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return falsevalue;
			}
		}

		return falsevalue;
	}

	public ArrayList<Node> getProductListByCatId(String searchText) {
		ArrayList<Node> pb = null;

		Node tempPrdNode, childNode;
		Session session = null;
		if (!searchText.trim().equals("")) {
			try {

				pb = new ArrayList<Node>();
				session = repo.login(new SimpleCredentials("admin", "admin"
						.toCharArray()));

				String querryStr = "select * from [nt:unstructured] where  ISDESCENDANTNODE('/content/product/products/') and catid  like '"
						+ searchText + "%'";
				Workspace workspace = session.getWorkspace();
				Query query = workspace.getQueryManager().createQuery(
						querryStr, Query.JCR_SQL2);
				QueryResult result = query.execute();
				NodeIterator iterator = result.getNodes();

				while (iterator.hasNext()) {

					tempPrdNode = iterator.nextNode();
					pb.add(tempPrdNode.getParent().getParent().getParent());

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

	public void createTemplate(SlingHttpServletRequest request,
			SlingHttpServletResponse response, String mn)
			throws RowsExceededException, IOException {
		try {
			Session session = null;
			session = repo.login(new SimpleCredentials("admin", "admin"
					.toCharArray()));
			Node rootNode = session.getRootNode().getNode("content");
			// Node prodNode = session.getRootNode().getNode("content")
			// .getNode("product").getNode("products").getNode(n);
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet firstSheet = workbook.createSheet(this.getClass()
					.getName());
			ArrayList<String> header = new ArrayList<String>();
			ArrayList<String> child = new ArrayList<String>();
			ArrayList<Integer> merge = new ArrayList<Integer>();
			
			 ArrayList<String> celltype = new ArrayList<String>();
			// response.getWriter().print(prodNode.getProperty("name"));

			if (rootNode.hasNode("product")) {
				rootNode = rootNode.getNode("product");
			} else {
				rootNode = rootNode.addNode("product");
			}
			if (rootNode.hasNode("products")) {
				rootNode = rootNode.getNode("products");
			} else {
				rootNode = rootNode.addNode("products");
			}
			String id = request.getParameter("id");
			if (id != "new") {
				if (request.getParameter("catalogpath") != null) {
					String path = request.getParameter("catalogpath");
					String flowPath[] = path.split("/");
					for (int i = 0; i < flowPath.length; i++) {
						rootNode = rootNode.getNode(flowPath[i]);

					}
					header.add("Catalog Number");
					merge.add(1);
					child.add("value");

					
// Changes Made for download template as commercial attr no longer needed
					
					if (rootNode.hasNode("template")
							&& rootNode.getNode("template").hasNode("attr")) {
						Node techNode = rootNode.getNode("template").getNode(
								"attr");
						NodeIterator prop = techNode.getNodes();
						while (prop.hasNext()) {
							Node p = prop.nextNode();
							Node labelNodeTech = session
									.getRootNode()
									.getNode("content")
									.getNode("attribute")
									.getNode("label")
									.getNode(p.getProperty("label").getString());
							NodeIterator typeNodeTech = session.getRootNode()
									.getNode("content").getNode("attribute")
									.getNode("type")
									.getNode(p.getProperty("type").getString())
									.getNodes();
							Node uomNodeTech = session.getRootNode()
									.getNode("content").getNode("uom")
									.getNode(p.getProperty("uom").getString());
							header.add(labelNodeTech.getProperty("name")
									.getString()
									+ "("
									+ uomNodeTech.getProperty("description")
											.getString() + ")");
							celltype.add(uomNodeTech.getProperty("uomDataType").getString());
							int cc = 0;
							while (typeNodeTech.hasNext()) {
								Node n = typeNodeTech.nextNode();
								child.add(n.getProperty("placeholder")
										.getString());
								cc++;
							}
							merge.add(cc);

						}

						}
	
	// Commented Backup code				
					
//					if (rootNode.hasNode("template")
//							&& rootNode.getNode("template").hasNode("attr")
//							&& rootNode.getNode("template").hasNode("comm")) {
//						Node techNode = rootNode.getNode("template").getNode(
//								"attr");
//						Node comNode = rootNode.getNode("template").getNode(
//								"comm");
//						NodeIterator prop = techNode.getNodes();
//						NodeIterator fprop = comNode.getNodes();
//						while (prop.hasNext()) {
//							Node p = prop.nextNode();
//							Node labelNodeTech = session
//									.getRootNode()
//									.getNode("content")
//									.getNode("attribute")
//									.getNode("label")
//									.getNode(p.getProperty("label").getString());
//							NodeIterator typeNodeTech = session.getRootNode()
//									.getNode("content").getNode("attribute")
//									.getNode("type")
//									.getNode(p.getProperty("type").getString())
//									.getNodes();
//							Node uomNodeTech = session.getRootNode()
//									.getNode("content").getNode("uom")
//									.getNode(p.getProperty("uom").getString());
//							header.add(labelNodeTech.getProperty("name")
//									.getString()
//									+ "("
//									+ uomNodeTech.getProperty("description")
//											.getString() + ")");
//							celltype.add(uomNodeTech.getProperty("uomDataType").getString());
//							int cc = 0;
//							while (typeNodeTech.hasNext()) {
//								Node n = typeNodeTech.nextNode();
//								child.add(n.getProperty("placeholder")
//										.getString());
//								cc++;
//							}
//							merge.add(cc);
//
//						}
//
//						while (fprop.hasNext()) {
//							Node f = fprop.nextNode();
//							Node labelNodeCom = session.getRootNode()
//									.getNode("content").getNode("attribute")
//									.getNode("commercial")
//									.getNode(f.getProperty("id").getString());
//							NodeIterator typeNodeCom = session
//									.getRootNode()
//									.getNode("content")
//									.getNode("attribute")
//									.getNode("type")
//									.getNode(
//											f.getProperty("typeCom")
//													.getString()).getNodes();
//
//							Node uomNodeTech = session
//									.getRootNode()
//									.getNode("content")
//									.getNode("uom")
//									.getNode(
//											f.getProperty("uomCom").getString());
//							header.add(labelNodeCom.getProperty("name")
//									.getString()
//									+ "("
//									+ uomNodeTech.getProperty("description")
//											.getString() + ")");
//							int cc = 0;
//							while (typeNodeCom.hasNext()) {
//								Node n = typeNodeCom.nextNode();
//								child.add(n.getProperty("placeholder")
//										.getString());
//								cc++;
//							}
//							merge.add(cc);
//
//						}
//
//					}

				}
			}

			// response.getWriter().print(header.size()+"---------"+value.size());
			HSSFRow rowzero = firstSheet.createRow(0);
			HSSFRow rowone = firstSheet.createRow(1);
			// response.getWriter().print("------header size---" +header);
			int rowtomerge = 1;
			for (int j = 0; j < header.size(); j++) {
				if (j == 0) {
					// firstSheet.addMergedRegion(new
					// CellRangeAddress(0,0,0,merge.get(j)));
					rowzero.createCell(j).setCellValue(
							new HSSFRichTextString(header.get(j)));
					// rowtomerge=merge.get(j);
					// response.getWriter().print(header.get(j)+"-------0-0-0-1"+merge.get(j)+"<br>");
				} else {
					if (j == header.size()) {
						firstSheet.addMergedRegion(new CellRangeAddress(0, 0,
								rowtomerge, rowtomerge + merge.get(j)));
						rowzero.createCell(j).setCellValue(
								new HSSFRichTextString(header.get(j)));
						rowtomerge = rowtomerge + merge.get(j);
						// response.getWriter().print(header.get(j)+"---j==header----0-0-0-1-- rw to merger "+rowtomerge+"----"+(merge.get(j))+"<br>");

					} else {
						firstSheet.addMergedRegion(new CellRangeAddress(0, 0,
								rowtomerge, rowtomerge + merge.get(j) - 1));
						rowzero.createCell(rowtomerge).setCellValue(
								new HSSFRichTextString(header.get(j)));
						// response.getWriter().print(header.get(j)+"---j!=header----0-0-0-1-- rw to merger "+rowtomerge+"----"+(merge.get(j))+"<br>");
						rowtomerge = rowtomerge + merge.get(j);
					}
				}
			}

			for (int j = 0; j < child.size(); j++) {
				rowone.createCell(j).setCellValue(
						new HSSFRichTextString(child.get(j)));
				// rowone.createCell(j).setCellValue(
				// new HSSFRichTextString(value.get(j)));

			}
			// write it as an excel attachment
			ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();

			workbook.write(outByteStream);
			byte[] outArray = outByteStream.toByteArray();
			response.setContentType("application/ms-excel");
			response.setContentLength(outArray.length);
			response.setHeader("Expires:", "0"); // eliminates browser caching
			response.setHeader("Content-Disposition",
					"attachment; filename=Template.xls");
			OutputStream outStream = response.getOutputStream();
			outStream.write(outArray);
			outStream.flush();

		} catch (Exception e) {
			response.getWriter().print("----------" + e.getMessage());
			// e.printStackTrace();
		}

	}

	public Long saveProductInfo(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		Session session = null;
		String prod = request.getParameter("id");
		String prod1 = request.getParameter("param");
		//String tnc=request.getParameter("agree");
		
		
		
		
		String id = "";
		Node node = null, compNode = null, compProNode = null, supNode = null, chsupNode = null, catUnspscNode = null, catNode = null, specNode = null, prodNode = null, mediaNode = null, docNode = null, dcNode = null, videoNode = null, vidNode = null, imgNode = null, picNode = null, jcrNode = null, indNode = null, tgeoNode = null, indNodeP = null, tgeoNodeP = null;
		// Session session;
		long a = 0;
		DateFormat dateFormat = new SimpleDateFormat("MMM d,yyyy");
		Date date = new Date();
		String f = "";
		try {

			session = repo.login(new SimpleCredentials("admin", "admin"
					.toCharArray()));

			if (session.getRootNode().getNode("content").getNode("product")
					.hasNode("products")) {
				prodNode = session.getRootNode().getNode("content")
						.getNode("product").getNode("products");
			} else {
				prodNode = session.getRootNode().getNode("content")
						.getNode("product").addNode("products");
				prodNode.setProperty("prodCount", 0);
			}

			if (prod != null && !prod.equals("new") && prod1.equals("edit")) {
				node = prodNode.getNode(prod);
				a = Long.parseLong(prod);
				f = String.valueOf(a);
			} else {
				// response.getWriter().print("Enter--");
				Long s = prodNode.getProperty("prodCount").getLong();
				f = Long.toString(s + 1);
				node = prodNode.addNode(f);
				// node.setProperty("prodDate", dateFormat.format(date));
				a = s + 1;
				node.setProperty("prodid", s + 1);
				node.setProperty("prd_code", s + 1);
				prodNode.setProperty("prodCount", s + 1);
				// response.getWriter().print("Exit----))-"+s);
			}

			// a=prodNode.getProperty("prodCount").getLong();
			node.setProperty("name", request.getParameter("proname"));
			node.setProperty("creator", request.getRemoteUser());
			node.setProperty("modelno", request.getParameter("modelno"));
			node.setProperty("shortdescription",
					request.getParameter("proshortdesc"));
			node.setProperty("description", request.getParameter("prolongdesc"));
			node.setProperty("keyword", request.getParameter("keyword"));
			if (!request.getParameter("agree").equals("")) {
				node.setProperty("producttnc", "accept");
			} else {
				node.setProperty("producttnc", "accept");
			}
			// node.setProperty("price", request.getParameter("whprice"));
			// node.setProperty("moq", request.getParameter("minorder"));
			// node.setProperty("sbl", request.getParameter("sellbylot"));
			response.getWriter().println("Path----going inside media pass)))-");



			if (node.hasNode("cat")) {
				catNode = node.getNode("cat");
				if (node.getNode("cat").hasNode("eclass")) {

				} else {
					catNode.addNode("eclass");
				}
				if (node.getNode("cat").hasNode("unspsc")) {
					if (request.getParameter("selprdcat1") != null
							&& !request.getParameter("selprdcat1").equals("")) {
						if (node.getNode("cat").getNode("unspsc").hasNodes()) {
							node.getNode("cat").getNode("unspsc").remove();
							catUnspscNode = catNode.addNode("unspsc");
						}
						if (request.getParameter("selprdcat1").indexOf(",") != -1) {
							String selPrdCat[] = request.getParameter(
									"selprdcat1").split(",");
							for (int i = 0; i < selPrdCat.length; i++) {
								catUnspscNode = node.getNode("cat")
										.getNode("unspsc")
										.addNode(selPrdCat[i]);
								catUnspscNode
										.setProperty("catid", selPrdCat[i]);

							}
						} else {
							catUnspscNode = node
									.getNode("cat")
									.getNode("unspsc")
									.addNode(request.getParameter("selprdcat1"));
							catUnspscNode.setProperty("catid",
									request.getParameter("selprdcat1"));
						}

					}

				} else {
					catUnspscNode = catNode.addNode("unspsc");
					if (request.getParameter("selprdcat1") != null
							&& !request.getParameter("selprdcat1").equals("")) {
						if (request.getParameter("selprdcat1").indexOf(",") != -1) {
							String selPrdCat[] = request.getParameter(
									"selprdcat1").split(",");
							for (int i = 0; i < selPrdCat.length; i++) {
								catUnspscNode = node.getNode("cat")
										.getNode("unspsc")
										.addNode(selPrdCat[i]);
								catUnspscNode
										.setProperty("catid", selPrdCat[i]);

							}
						} else {
							catUnspscNode = node
									.getNode("cat")
									.getNode("unspsc")
									.addNode(request.getParameter("selprdcat1"));
							catUnspscNode.setProperty("catid",
									request.getParameter("selprdcat1"));
						}
					}

				}
			} else {
				catNode = node.addNode("cat");
				catNode.addNode("eclass");
				catUnspscNode = catNode.addNode("unspsc");
				if (request.getParameter("selprdcat1") != null
						&& !request.getParameter("selprdcat1").equals("")) {
					if (request.getParameter("selprdcat1").indexOf(",") != -1) {
						String selPrdCat[] = request.getParameter("selprdcat1")
								.split(",");
						for (int i = 0; i < selPrdCat.length; i++) {
							catUnspscNode = node.getNode("cat")
									.getNode("unspsc").addNode(selPrdCat[i]);
							catUnspscNode.setProperty("catid", selPrdCat[i]);

						}
					} else {
						catUnspscNode = node.getNode("cat").getNode("unspsc")
								.addNode(request.getParameter("selprdcat1"));
						catUnspscNode.setProperty("catid",
								request.getParameter("selprdcat1"));
					}
				}
			}



			// response.getWriter().println("Path----cat pass)))-");
			// if (node.hasNode("spec") && node.getNode("spec").hasNode("attr")
			// && node.getNode("spec").hasNode("fattr")) {
			// Node att = node.getNode("spec").getNode("attr");
			// Node fatt = node.getNode("spec").getNode("fattr");
			// String value = "";
			// if(att.hasNodes()){
			// att.remove();
			// att=node.getNode("spec").addNode("attr");
			// }
			// if(fatt.hasNodes()){
			// fatt.remove();
			// fatt=node.getNode("spec").addNode("fattr");
			// }
			//
			// if (request.getParameterValues("label") != null
			// && !request.getParameterValues("label").equals("")) {
			// String[] attrLabel = request.getParameterValues("label");
			// String[] attrType = request.getParameterValues("type");
			// String[] attrMin = request.getParameterValues("min");
			// String[] attrMax = request.getParameterValues("max");
			// String[] attrUom = request.getParameterValues("uom");
			// for (int i = 0; i < attrLabel.length; i++) {
			// Node subatt = att.addNode(String.valueOf(i));
			// subatt.setProperty("attrname", attrLabel[i]);
			// subatt.setProperty("value", attrType[i]);
			// subatt.setProperty("min", attrMin[i]);
			// subatt.setProperty("max", attrMax[i]);
			// subatt.setProperty("uom", attrUom[i]);
			// }
			// }
			// if (request.getParameterValues("txtheading") != null
			// && !request.getParameterValues("txtheading").equals("")) {
			// String[] freeattrHeading = request
			// .getParameterValues("txtheading");
			// String[] freeattrValue = request
			// .getParameterValues("txtvalue");
			// for (int i = 0; i < freeattrHeading.length; i++) {
			// Node subatt = fatt.addNode(String.valueOf(i));
			// subatt.setProperty("attrname", freeattrHeading[i]);
			// subatt.setProperty("value", freeattrValue[i]);
			// }
			// }
			//
			// } else {
			// specNode = node.addNode("spec");
			// Node att = specNode.addNode("attr");
			// Node fatt = specNode.addNode("fattr");
			// if (request.getParameterValues("label") != null
			// && !request.getParameterValues("label").equals("")) {
			// String[] attrLabel = request.getParameterValues("label");
			// String[] attrType = request.getParameterValues("type");
			// String[] attrMin = request.getParameterValues("min");
			// String[] attrMax = request.getParameterValues("max");
			// String[] attrUom = request.getParameterValues("uom");
			// for (int i = 0; i < attrLabel.length; i++) {
			// Node subatt = att.addNode(String.valueOf(i));
			// subatt.setProperty("attrname", attrLabel[i]);
			// subatt.setProperty("value", attrType[i]);
			// subatt.setProperty("min", attrMin[i]);
			// subatt.setProperty("max", attrMax[i]);
			// subatt.setProperty("uom", attrUom[i]);
			// }
			// }
			// if (request.getParameterValues("txtheading") != null
			// && !request.getParameterValues("txtheading").equals("")) {
			// String[] freeattrHeading = request
			// .getParameterValues("txtheading");
			// String[] freeattrValue = request
			// .getParameterValues("txtvalue");
			// for (int i = 0; i < freeattrHeading.length; i++) {
			// Node subatt = fatt.addNode(String.valueOf(i));
			// subatt.setProperty("attrname", freeattrHeading[i]);
			// subatt.setProperty("value", freeattrValue[i]);
			// }
			// }
			//
			// }
			response.getWriter().println("Path----free att  pass)))-");
			if (node.hasNode("sup")) {
			} else {
				supNode = node.addNode("sup");
				chsupNode = supNode.addNode(request.getParameter("companyId"));
				chsupNode.setProperty("supid",
						request.getParameter("companyId"));
			}
			if (prod != null && !prod.equals("new") && prod1.equals("edit")) {
				compNode = session.getRootNode().getNode("content")
						.getNode("company")
						.getNode(request.getParameter("companyId"));
				if (compNode.hasNode("product")) {
					compProNode = compNode.getNode("product");
					if (compProNode.hasNode(Long.toString(a))) {

					} else {
						compProNode.addNode(Long.toString(a));
					}

				} else {
					response.getWriter().print("----))))-start");
					compProNode = compNode.addNode("product");
					compProNode.addNode(Long.toString(a));
					response.getWriter().print("----))))-end");

				}
			} else if (prod != null && prod.equals("new")) {
				compNode = session.getRootNode().getNode("content")
						.getNode("company")
						.getNode(request.getParameter("companyId"));
				if (compNode.hasNode("product")) {
					compProNode = compNode.getNode("product");
					if (compProNode.hasNode(Long.toString(a))) {

					} else {
						compProNode.addNode(Long.toString(a));
					}

				} else {
					// response.getWriter().print("----))))-start");
					compProNode = compNode.addNode("product");
					compProNode.addNode(Long.toString(a));
					// response.getWriter().print("----))))-end");

				}
			}
			// response.getWriter().print("----))))-"+prod);
			response.getWriter().println("Path----allla pass)))-");
			session.save();

		} catch (PathNotFoundException e) {
			response.getWriter().print("Path----)))-" + e.getMessage());
			e.printStackTrace();
		} catch (RepositoryException e) {
			response.getWriter().print("Rrespo----)))-" + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			response.getWriter().print("Rrespo1----)))-" + e.getMessage());
			e.printStackTrace();
		}
		return a;
	}

	public Long saveProductOtherInfo(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		Session session = null;
		String prod = request.getParameter("id");
		String prod1 = request.getParameter("param");
		String id = "";
		Node node = null, compNode = null, compProNode = null, supNode = null, chsupNode = null, catUnspscNode = null, catNode = null, specNode = null, prodNode = null, mediaNode = null, docNode = null, dcNode = null, videoNode = null, vidNode = null, imgNode = null, picNode = null, jcrNode = null, indNode = null, tgeoNode = null, indNodeP = null, tgeoNodeP = null;
		// Session session;
		long a = 0;
		DateFormat dateFormat = new SimpleDateFormat("MMM d,yyyy");
		Date date = new Date();
		String f = "";
		try {

			session = repo.login(new SimpleCredentials("admin", "admin"
					.toCharArray()));

			if (session.getRootNode().getNode("content").getNode("product")
					.hasNode("products")) {
				prodNode = session.getRootNode().getNode("content")
						.getNode("product").getNode("products");
			} else {
				prodNode = session.getRootNode().getNode("content")
						.getNode("product").addNode("products");
				prodNode.setProperty("prodCount", 0);
			}

			if (prod != null && !prod.equals("new") && prod1.equals("edit")) {
				node = prodNode.getNode(prod);
				a = Long.parseLong(prod);
				f = String.valueOf(a);
			} else {
				// response.getWriter().print("Enter--");
				Long s = prodNode.getProperty("prodCount").getLong();
				f = Long.toString(s + 1);
				node = prodNode.addNode(f);
				// node.setProperty("prodDate", dateFormat.format(date));
				a = s + 1;
				node.setProperty("prodid", s + 1);
				node.setProperty("prd_code", s + 1);
				prodNode.setProperty("prodCount", s + 1);
				// response.getWriter().print("Exit----))-"+s);
			}

			
			if (node.hasNode("media")) {
				mediaNode = node.getNode("media");
				if (mediaNode.hasNode("images")) {
					picNode = mediaNode.getNode("images");
				} else {
					picNode = mediaNode.addNode("images");
				}
				if (mediaNode.hasNode("videoes")) {
					videoNode = node.getNode("media").getNode("videoes");
				} else {
					videoNode = mediaNode.addNode("videoes");
				}
			} else {
				mediaNode = node.addNode("media");
				picNode = mediaNode.addNode("images");
				videoNode = mediaNode.addNode("videoes");
			}

			try {
				if (request.getParameterMap().get("upimg") != null) {
					RequestParameter[] ap = request.getRequestParameterMap()
							.get("upimg");

					Date c = new Date();
					String s = c.getTime() + "" + c.getYear() + ""
							+ c.getMonth() + "" + c.getDate();

					String picDesc = null;
					if (request.getParameter("upimg") != null) {

						String fileType = "";
						if (mediaNode.hasNode("images")
								&& mediaNode.getNode("images").hasNodes()) {
							NodeIterator d = mediaNode.getNode("images")
									.getNodes();
							while (d.hasNext()) {
								Node an = d.nextNode();
								an.remove();
							}
							mediaNode = mediaNode.addNode("images");
							// }
						}
						for (int i = 0; i < ap.length; i++) {
							if (ap[i].getFileName() != null
									&& ap[i].getFileName().trim() != "") {
								response.getWriter().print("inside image " + i);
								picDesc = c.getDate() + "" + c.getMonth() + ""
										+ c.getYear() + "" + c.getTime();
								String filenam = ap[i].getFileName();

								fileType = "";

								if (ap[i] != null && ap[i].getSize() != 0) {
									for (int j = 0; j < VIDEOEXTENSION.length; j++) {
										if (filenam.indexOf(VIDEOEXTENSION[j]) != -1) {
											fileType = "video";
										}
									}
									// propNode = node.addNode(s + i);

									Node propNode = picNode
											.addNode(picDesc + i);

									Node fileNode = propNode.addNode(picDesc,
											"nt:file");

									jcrNode = fileNode.addNode("jcr:content",
											"nt:resource");

									jcrNode.setProperty("jcr:data",
											ap[i].getInputStream());

									jcrNode.setProperty("jcr:mimeType",
											"image/jpeg");

									propNode.setProperty("fileType", fileType);
									propNode.setProperty("imgpath",
											"/content/product/products/" + f
													+ "/media/images/"
													+ picDesc + i + "/"
													+ picDesc);
								}

							}
						}

					}
				}

			} catch (Exception ex) {
				response.getWriter().println("message----" + ex.getMessage());
			}


			// industry

			if (node.hasNode("industry")) {
				if (request.getParameter("selIndustryId") != null
						&& !request.getParameter("selIndustryId").equals("")) {
					if (node.getNode("industry").hasNodes()) {
						node.getNode("industry").remove();
						indNode = node.addNode("industry");
					}
					if (request.getParameter("selIndustryId").indexOf(",") != -1) {
						String selPrdIn[] = request.getParameter(
								"selIndustryId").split(",");
						for (int i = 0; i < selPrdIn.length; i++) {
							indNodeP = indNode.addNode(selPrdIn[i]);
							indNodeP.setProperty("industryid", selPrdIn[i]);

						}
					} else {
						indNodeP = indNode.addNode(request
								.getParameter("selIndustryId"));
						indNodeP.setProperty("industryid",
								request.getParameter("selIndustryId"));
					}

				}
			} else {
				indNode = node.addNode("industry");
				if (request.getParameter("selIndustryId") != null
						&& !request.getParameter("selIndustryId").equals("")) {
					if (request.getParameter("selIndustryId").indexOf(",") != -1) {
						String selPrdIn[] = request.getParameter(
								"selIndustryId").split(",");
						for (int i = 0; i < selPrdIn.length; i++) {
							indNodeP = indNode.addNode(selPrdIn[i]);
							indNodeP.setProperty("industryid", selPrdIn[i]);

						}
					} else {
						indNodeP = indNode.addNode(request
								.getParameter("selIndustryId"));
						indNodeP.setProperty("industryid",
								request.getParameter("selIndustryId"));
					}
				}
			}

			// target geography

			if (node.hasNode("targetgeo")) {
				if (request.getParameter("seltgeoId") != null
						&& !request.getParameter("seltgeoId").equals("")) {
					if (node.getNode("targetgeo").hasNodes()) {
						node.getNode("targetgeo").remove();
						tgeoNode = node.addNode("targetgeo");
					}
					if (request.getParameter("seltgeoId").indexOf(",") != -1) {
						String selPrdTG[] = request.getParameter("seltgeoId")
								.split(",");
						for (int i = 0; i < selPrdTG.length; i++) {
							tgeoNodeP = tgeoNode.addNode(selPrdTG[i]);
							tgeoNodeP.setProperty("targetgeoid", selPrdTG[i]);

						}
					} else {
						tgeoNodeP = tgeoNode.addNode(request
								.getParameter("seltgeoId"));
						tgeoNodeP.setProperty("targetgeoid",
								request.getParameter("seltgeoId"));
					}

				}
			} else {
				tgeoNode = node.addNode("targetgeo");
				if (request.getParameter("seltgeoId") != null
						&& !request.getParameter("seltgeoId").equals("")) {
					if (request.getParameter("seltgeoId").indexOf(",") != -1) {
						String selPrdTG[] = request.getParameter("seltgeoId")
								.split(",");
						for (int i = 0; i < selPrdTG.length; i++) {
							tgeoNodeP = tgeoNode.addNode(selPrdTG[i]);
							tgeoNodeP.setProperty("targetgeoid", selPrdTG[i]);

						}
					} else {
						tgeoNodeP = tgeoNode.addNode(request
								.getParameter("seltgeoId"));
						tgeoNodeP.setProperty("targetgeoid",
								request.getParameter("seltgeoId"));
					}
				}
			}
			// response.getWriter().print("----))))-"+prod);
			response.getWriter().println("Path----allla pass)))-");
			session.save();

		} catch (PathNotFoundException e) {
			response.getWriter().print("Path----)))-" + e.getMessage());
			e.printStackTrace();
		} catch (RepositoryException e) {
			response.getWriter().print("Rrespo----)))-" + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			response.getWriter().print("Rrespo1----)))-" + e.getMessage());
			e.printStackTrace();
		}
		return a;
	}

	
	public Long saveProductCatalogInfo(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		Session session = null;
		String prod = request.getParameter("id");
//		String prod1 = request.getParameter("param");
		String id = "";
		Node node = null, compNode = null, compProNode = null, supNode = null, chsupNode = null, catUnspscNode = null, catNode = null, specNode = null, prodNode = null, mediaNode = null, docNode = null, dcNode = null, videoNode = null, vidNode = null, imgNode = null, picNode = null;
		// Session session;
		long a = 0;
		DateFormat dateFormat = new SimpleDateFormat("MMM d,yyyy");
		Date date = new Date();
		String f = "";
		try {

			session = repo.login(new SimpleCredentials("admin", "admin"
					.toCharArray()));

			if (session.getRootNode().getNode("content").getNode("product")
					.hasNode("products")) {
				prodNode = session.getRootNode().getNode("content")
						.getNode("product").getNode("products");
			} else {
				prodNode = session.getRootNode().getNode("content")
						.getNode("product").addNode("products");
				prodNode.setProperty("prodCount", 0);
			}
String path = request.getParameter("pathToAdd");
			String flowPath[] = path.split("/");
			for (int i = 0; i < flowPath.length; i++) {
				prodNode = prodNode.getNode(flowPath[i]);

			}
			if (prodNode.hasNode("child")) {
								
				node=prodNode;
				prodNode.getNode("child").remove();
				prodNode=prodNode.addNode("child");
				prodNode.setProperty("prodCount",0);
			}else{
									node=prodNode;
			prodNode=prodNode.addNode("child");
			prodNode.setProperty("prodCount",0);
								}
				if (request.getParameterValues("labelId") != null
						&& !request.getParameterValues("labelId").equals("")) {
					String[] attrLabel = request.getParameterValues("labelId");
					String[] attrType = request.getParameterValues("type");

					String[] attrUom = request.getParameterValues("uom");
					ArrayList<String> al=new ArrayList<String>();
					al.add("catalog");
					Node template,attr,comm=null;
					if(node.hasNode("template")){
						node.getNode("template").remove();
						template=node.addNode("template");
						attr=template.addNode("attr");
					}else{
						template=node.addNode("template");
						attr=template.addNode("attr");
					}
					
					for (int i = 0; i < attrLabel.length; i++) {
						Node labelNodeTech = session.getRootNode()
										.getNode("content")
										.getNode("attribute").getNode("label")
										.getNode(attrLabel[i]);
											Node at=attr.addNode(String.valueOf(i));
											at.setProperty("label",attrLabel[i]);
											at.setProperty("type",attrType[i]);
											at.setProperty("uom",attrUom[i]);
								NodeIterator typeNodeTech = session
										.getRootNode().getNode("content")
										.getNode("attribute").getNode("type")
										.getNode(attrType[i]).getNodes();

								while (typeNodeTech.hasNext()) {
									Node n = typeNodeTech.nextNode();
									 al.add(labelNodeTech.getProperty("name").getString()+"_"+n.getProperty("placeholder").getString());
								}

							}
						
						int o=0;
						if(al.size()>0){
							 o=request.getParameterValues(al.get(0)).length;
							
						}
						ArrayList<String>code=new ArrayList<String>();
						for(int k=0;k<o;k++){
							long l=prodNode.getProperty("prodCount").getLong();
							prodNode.addNode(String.valueOf(l+1));
							prodNode.setProperty("prodCount",l+1);
							code.add(String.valueOf(l+1));
						}
						
						for(int j=0;j<al.size();j++){
						String []para=request.getParameterValues(al.get(j));
								
							for(int m=0;m<code.size();m++){
								Node actulprocut=prodNode.getNode(code.get(m));
								if(actulprocut.hasNode("attr")){
									actulprocut=actulprocut.getNode("attr");
									actulprocut.setProperty(al.get(j),para[m]);
								}else{
									actulprocut=actulprocut.addNode("attr");
									actulprocut.setProperty(al.get(j),para[m]);	
									}
								
								}	
							}
						
				
						
				////////// Upload Images
						
										RequestParameter[] ap = request.getRequestParameterMap().get(
												"image");

										for (int m = 0; m < code.size(); m++) {
											Node actProd = prodNode.getNode(code.get(m));

											response.getWriter().print(
													code.get(m) + "----File name --------"
															+ ap[m].getFileName());

											if (ap[m].getFileName() != null
													&& ap[m].getInputStream() != null
													&& !ap[m].getFileName().trim().equals("")) {
												if (actProd.hasNode("media")) {
													actProd = actProd.getNode("media");
												} else {
													actProd = actProd.addNode("media");
												}

												if (actProd.hasNode("images")) {
													actProd.getNode("images").remove();
													actProd = actProd.addNode("images");

												} else {
													actProd = actProd.addNode("images");

												}
												// a=a.addNode(propList.get(y).split("_")[0]);

												actProd = actProd.addNode("0");

												Node fileNode = actProd.addNode("0", "nt:file");

												Node jcrNode = fileNode.addNode("jcr:content",
														"nt:resource");

												jcrNode.setProperty("jcr:data", ap[m].getInputStream());

												jcrNode.setProperty("jcr:mimeType", "image/jpeg");
												actProd.setProperty("imgpath",
														"/content/product/products/" + code.get(m)
																+ "/media/images/0/0");

											}

										}
										
								////////// Upload Videos
										
										RequestParameter[] ap1 = request.getRequestParameterMap().get(
												"video");

										for (int m = 0; m < code.size(); m++) {
											Node actProd = prodNode.getNode(code.get(m));

											response.getWriter().print(
													"----File name --------" + ap1[m].getFileName());
											if (ap1[m].getFileName() != null
													&& ap1[m].getInputStream() != null
													&& ap1[m].getFileName() != ""
													&& !ap1[m].getFileName().trim().equals("")) {

												if (actProd.hasNode("media")) {
													actProd = actProd.getNode("media");
												} else {
													actProd = actProd.addNode("media");
												}

												if (actProd.hasNode("videoes")) {
													actProd.getNode("videoes").remove();

													actProd = actProd.addNode("videoes");

												} else {

													actProd = actProd.addNode("videoes");

												}
												// a=a.addNode(propList.get(y).split("_")[0]);

												actProd = actProd.addNode("0");

												Node fileNode = actProd.addNode("0", "nt:file");

												Node jcrNode = fileNode.addNode("jcr:content",
														"nt:resource");

												jcrNode.setProperty("jcr:data", ap1[m].getInputStream());

												jcrNode.setProperty("jcr:mimeType", "video");
												actProd.setProperty("imgpath",
														"/content/product/products/" + code.get(m)
																+ "/media/videoes/0/0");

											}

										}
										
										////////// Upload Documents
										
										RequestParameter[] ap2 = request.getRequestParameterMap().get(
												"atta");

										for (int m = 0; m < code.size(); m++) {
											Node actProd = prodNode.getNode(code.get(m));

											response.getWriter().print(
													"----File name --------" + ap[m].getFileName());
											if (ap2[m].getFileName() != null
													&& ap2[m].getInputStream() != null
													&& ap2[m].getFileName() != ""
													&& !ap2[m].getFileName().trim().equals("")) {

												if (actProd.hasNode("media")) {
													actProd = actProd.getNode("media");
												} else {
													actProd = actProd.addNode("media");
												}
												if (actProd.hasNode("attachment")) {
													actProd.getNode("attachment").remove();
													actProd = actProd.addNode("attachment");

												} else {

													actProd = actProd.addNode("attachment");

												}
												actProd = actProd.addNode("0");
												actProd.setProperty("imgpath",
														"/content/product/products/" + code.get(m)
																+ "/media/attachment/0/0");
												Node fileNode = actProd.addNode("0", "nt:file");

												Node jcrNode = fileNode.addNode("jcr:content",
														"nt:resource");

												jcrNode.setProperty("jcr:data", ap2[m].getInputStream());

												jcrNode.setProperty("jcr:mimeType", "attach");

											}

										}

						
						
						
					}
//						}
//					}
//				}


			
			// response.getWriter().print("----))))-"+prod);
			response.getWriter().println("Path----allla pass)))-");
			session.save();

		} catch (PathNotFoundException e) {
			response.getWriter().print("Path----)))-" + e.getMessage());
			e.printStackTrace();
		} catch (RepositoryException e) {
			response.getWriter().print("Rrespo----)))-" + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			response.getWriter().print("Rrespo1----)))-" + e.getMessage());
			e.printStackTrace();
		}
		return a;
	}

	public String responseQuoteRfq(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		Node rootNode = null;
			Node currentNode=null;
			Session session = null;
		try{	
  			
			PrintWriter out=response.getWriter();
			session = repo.login(new SimpleCredentials("admin", "admin"
					.toCharArray()));
			//out.print(request.getRemoteUser());
			Node userNode=null;Node userNode1=null;
			 String contactNo="",userName="";
			Node node1= session.getRootNode().getNode("content")
						.getNode("user");
			Node currency= session.getRootNode().getNode("content")
					.getNode("currency");
			//out.print(node1);
			//out.print("------");
			
			 if(node1.hasNode(request.getRemoteUser().replace("@", "_"))){
				// out.print("1");
				userNode=node1.getNode(request.getRemoteUser().replace("@", "_"));
				
				//out.print(userNode);
				userName=userNode.getProperty("name").getString();
				//contactNo=userNode.getProperty("number").getString();
			 }
			    String compid= request.getParameter("rfq_compid");
			String rfqid= request.getParameter("rfq_noid");
				String[] pid = request.getParameterValues("rfq_prodid");
				String[] pname = request.getParameterValues("rfq_prodname");
				String[] pquantity = request.getParameterValues("rfq_prodquantity");
				String[] pdescription = request.getParameterValues("rfq_proddesc");
				String[] pcurrency = request.getParameterValues("rfq_currency");
				String[] prate = request.getParameterValues("rate");
				String sellerDesc = request.getParameter("rfq_seller_desc");
				//add date time of creation of each node in rfq
				
				rootNode=session.getRootNode().getNode("content")
						.getNode("company");
				Node rfqNode=null;
				Node workNode = null;
			  Node pNode= null;
			  Node attachNode= null;
			  if(rootNode.hasNode(compid)){
  				     currentNode=rootNode.getNode(compid);
  						 if(currentNode.hasNode("Rfq")){
  							  rfqNode=currentNode.getNode("Rfq");
  							  
  							  workNode = rfqNode.getNode(rfqid);
  							
  							String userName1="";
  							 if(node1.hasNode(workNode.getProperty("userid").getString().replace("@", "_"))){
  									// out.print("1");
  									userNode1=node1.getNode(workNode.getProperty("userid").getString().replace("@", "_"));
  									
  									//out.print(userNode);
  									userName1=userNode1.getProperty("name").getString();
  									//contactNo=userNode.getProperty("number").getString();
  								 }else{
  									 userName1="User";
  								 }			
  							String currencyStr = "";
  							String htmlText="";
  							String htm="";
				for (int i=0; i<pid.length; i++){
					int count=1;
							//workNode.setProperty("sellerdesc", sellerDesc);
							  pNode = workNode.getNode(pid[i]);
//							  pNode.setProperty("productid", pid[i]);
//							  pNode.setProperty("productName", pname[i]);
//							  pNode.setProperty("productImage", pimage[i]);
//							  pNode.setProperty("productpQuantity", pquantity[i]);
							pNode.setProperty("productCurrency", pcurrency[i]);
							pNode.setProperty("productRate", prate[i]);
							if(currency.hasNode(pcurrency[i]) && currency.getNode(pcurrency[i]).hasProperty("code")){
								currencyStr = currency.getNode(pcurrency[i]).getProperty("code").getString();
							}else{
								currencyStr = "";
							}
							String path = "";
							if (request.getParameterMap().get("rfqfile") != null) {
								RequestParameter[] ap2 = request.getRequestParameterMap().get("rfqfile");
							response.getWriter().print(
									"----File name --------" + ap2[i].getFileName());
							//for(int j=0;j<ap2[i].getSize();j++){
							if (ap2[i].getFileName() != null
										&& ap2[i].getInputStream() != null
										&& ap2[i].getFileName() != ""
										&& !ap2[i].getFileName().trim().equals("")) {

				  					response.getWriter().print("inside if");
									if (pNode.hasNode("attachment")) {
										//pNode.getNode("attachment").remove();
										attachNode = pNode.getNode("attachment");

									} else {

										attachNode = pNode.addNode("attachment");

									}
									
									attachNode = attachNode.addNode(ap2[i].getFileName());
									response.getWriter().print("inside if 1 "+attachNode);
									attachNode.setProperty("docpath",
											"/content/company/" + compid+"/Rfq/"+rfqid+"/"+pid[i]+ "/attachment/"+ap2[i].getFileName()+"/"+ap2[i].getFileName());
									Node fileNode = attachNode.addNode(ap2[i].getFileName(), "nt:file");

									Node jcrNode = fileNode.addNode("jcr:content",
											"nt:resource");

									jcrNode.setProperty("jcr:data", ap2[i].getInputStream());

									jcrNode.setProperty("jcr:mimeType", "attach");
									path = "<a href='"+request.getContextPath()+"/content/company/" + compid+"/Rfq/"+rfqid+"/"+pid[i]+ "/attachment/"+ap2[i].getFileName()+"/"+ap2[i].getFileName()+"' download>"+ap2[i].getFileName()+"</a>";

								}else{
									response.getWriter().print("inside else");
									path = "NA";
								}
						//	}
				  				
							}else{
								response.getWriter().print("inside else out");
								path = "NA";
							}
							count++;
							htm =htm+"<p>Product Name : <strong>"+pname[i]+"</strong></p><p>Quantity : "+pquantity[i]+"</p><p>Buyer Description : "+pdescription[i]+"</p><p>Rate : "+prate[i] + " /" +currencyStr +"</p><p>Attachment : "+ path+"</p>";
					 //htmlText = "<html><body><table width='100%' border='0' cellspacing='0' cellpadding='0' style='border-radius:0px '0px  10px 10px; border:1px solid #4096EE; background-color:#ffffff;'><tr><td align='center' valign='top'><table width='100%' border='0' align='center' cellpadding='5' cellspacing='5' style='border-top:6px solid #4096EE;'><tr><td align='left' valign='middle' style='font-family:Arial, Helvetica, sans-serif; color:#4e4e4e; font-size:13px; padding-right:10px;'><div style='font-size:24px; color:#4096EE;'>Dear "+userName1+", </div><p><strong>"+sellerDesc+"</strong></p><p>Product Name : <strong>"+pname[i]+"</strong></p><p>Quantity : "+pquantity[i]+"</p><p>Buyer Description : "+pdescription[i]+"</p><p>Rate : "+prate[i] + " /" +currencyStr +"</p><p>Attachment : "+ path+"</p><tr><td align='left' valign='middle' style='font-family:Arial, Helvetica, sans-serif; color:#4e4e4e; font-size:13px;'></tr><tr><td align='left' valign='middle' style='font-family:Arial, Helvetica, sans-serif; color:#333; font-size:13px;'><span style='color:#333; font-size:12px; font-family:Arial, Helvetica, sans-serif;'>With Regards,<br /><strong>Administrator</strong></span></tr></table></td></tr></table></body></html>";
					//String[] checkparamKey = {"email","msg"};
                   // String[] checkparamValue = {workNode.getProperty("userid").getString(),htmlText};
                    //String res = callPostService("http://prod.bizlem.io:8082/portal/servlet/service/productselection.sendMailRfqSeller", checkparamKey, checkparamValue);
				}
				htmlText="<html><body><table width='100%' border='0' cellspacing='0' cellpadding='0' style='border-radius:0px '0px  10px 10px; border:1px solid #4096EE; background-color:#ffffff;'><tr><td align='center' valign='top'><table width='100%' border='0' align='center' cellpadding='5' cellspacing='5' style='border-top:6px solid #4096EE;'><tr><td align='left' valign='middle' style='font-family:Arial, Helvetica, sans-serif; color:#4e4e4e; font-size:13px; padding-right:10px;'><div style='font-size:24px; color:#4096EE;'>Dear "+userName1+", </div><p><strong>"+sellerDesc+"</strong></p><br/>"+htm+"<tr><td align='left' valign='middle' style='font-family:Arial, Helvetica, sans-serif; color:#4e4e4e; font-size:13px;'></tr><tr><td align='left' valign='middle' style='font-family:Arial, Helvetica, sans-serif; color:#333; font-size:13px;'><span style='color:#333; font-size:12px; font-family:Arial, Helvetica, sans-serif;'>With Regards,<br /><strong>Administrator</strong></span></tr></table></td></tr></table></body></html>";
						String[] checkparamKey = {"email","msg"};
                String[] checkparamValue = {workNode.getProperty("userid").getString(),htmlText};
                String res = callPostService("http://prod.bizlem.io:8082/portal/servlet/service/productselection.sendMailRfqSeller", checkparamKey, checkparamValue);
				 
				
//						String[] checkparamKey = {"email","supId","buymsg","name","selmsg","productname","productquantity","price","attach"};
//	                    String[] checkparamValue = {rfqid.getProperty("userid").getString(),compid, 
//	                    		pdescription[i].replaceAll(" ", "%20"),
//	                    		userName.replaceAll(" ", "%20"),sellerDesc,pname[i].replaceAll(" ", "%20"),
//	                    		pquantity[i].replaceAll(" ", "%20")};
	                  // out.print(checkparamKey+"~~~~~~~~~~"+checkparamValue);									
//	            		String res = product.callGetService("http://prod.bizlem.io:8082/portal/servlet/service/event.sendMailRfqSeller", checkparamKey, checkparamValue);
						 
	            		//out.print(res);
						 
						 //add code to send mail to company creator to  for eachcompany
						 }
					

				}
				workNode.setProperty("status", "responded");
				workNode.setProperty("sellerdesc", sellerDesc);
				session.save();
				String[] checkparamKey = {"type","userId","productCode","quantity","serviceId"};
                String[] checkparamValue = {"setConsumption",request.getRemoteUser(),"RFQ_Service","1",request.getParameter("serviceId")};
                String res = callGetService("http://prod.bizlem.io:8078/ServiceLogging/consumption", checkparamKey, checkparamValue);
//		 		response.sendRedirect("http://prod.bizlem.io:8082/portal/servlet/service/globalsearch.rfqdata?Companyid="+compid+"&rfqNo="+rfqid);

			}catch(Exception e){
				 PrintWriter out=response.getWriter();
       		out.print(e.getMessage());
       		e.printStackTrace();
			
			}
		

		return "true";
	}
	
	public String checkRfqResponseLimit(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		Node rootNode = null;
			Node currentNode=null;
			Session session = null;
		try{	
  			
			PrintWriter out=response.getWriter();
			session = repo.login(new SimpleCredentials("admin", "admin"
					.toCharArray()));
			Node compNode=null;Node compNode1=null;
			Node node1= session.getRootNode().getNode("content")
						.getNode("company");
			  String compid= request.getParameter("rfq_compid");
			  int respondedCount = 0;
			if(node1.hasNode(compid)){
				compNode=node1.getNode(compid);
				if(compNode.hasNode("Rfq") && compNode.getNode("Rfq").hasNodes()){
					NodeIterator rfNodes = compNode.getNode("Rfq").getNodes();
					while (rfNodes.hasNext()) {
						compNode1 = rfNodes.nextNode();
						if(compNode1.hasProperty("status")){
							if(compNode1.getProperty("status").getString().equals("responded")){
								respondedCount = respondedCount + 1;
							}
						}
					}
				
				}
			}
			String[] checkparamKey = {"type","userId","productCode","serviceId"};
            String[] checkparamValue = {"getConsumptionCustom",request.getRemoteUser(),"RFQ_Service",request.getParameter("serviceId")};
            String res = callGetService("http://prod.bizlem.io:8078/ServiceLogging/consumption", checkparamKey, checkparamValue);
			 JSONObject obj = new JSONObject(res); 
			 int limitRes = Integer.parseInt(obj.getString("limit"));
			 if(respondedCount > limitRes){
				 return "false";
			 }
		}catch(Exception e){
			 PrintWriter out=response.getWriter();
  		out.print(e.getMessage());
  		e.printStackTrace();
		return "false";
		}
	

	return "true";
}
	
	
	public Long saveProductStdCatalogInfo(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		Session session = null;
		String prod = request.getParameter("id");
//		String prod1 = request.getParameter("param");
		String id = "";
		Node node = null, compNode = null, compProNode = null, supNode = null, chsupNode = null, catUnspscNode = null, catNode = null, specNode = null, prodNode = null, mediaNode = null, docNode = null, dcNode = null, videoNode = null, vidNode = null, imgNode = null, picNode = null;
		// Session session;
		long a = 0;
		DateFormat dateFormat = new SimpleDateFormat("MMM d,yyyy");
		Date date = new Date();
		String f = "";
		try {

			session = repo.login(new SimpleCredentials("admin", "admin"
					.toCharArray()));

			if (session.getRootNode().getNode("content").getNode("product")
					.hasNode("products")) {
				prodNode = session.getRootNode().getNode("content")
						.getNode("product").getNode("products");
			} else {
				prodNode = session.getRootNode().getNode("content")
						.getNode("product").addNode("products");
				prodNode.setProperty("prodCount", 0);
			}
String path = request.getParameter("pathToAdd");
			String flowPath[] = path.split("/");
			for (int i = 0; i < flowPath.length; i++) {
				prodNode = prodNode.getNode(flowPath[i]);

			}
			if (prodNode.hasNode("child")) {
								
				node=prodNode;
				prodNode.getNode("child").remove();
				prodNode=prodNode.addNode("child");
				prodNode.setProperty("prodCount",0);
			}else{
									node=prodNode;
			prodNode=prodNode.addNode("child");
			prodNode.setProperty("prodCount",0);
								}
				if (request.getParameterValues("labelIdstd") != null
						&& !request.getParameterValues("labelIdstd").equals("")) {
					String[] attrLabel = request.getParameterValues("labelIdstd");
					String[] attrType = request.getParameterValues("typestd");

					String[] attrUom = request.getParameterValues("uomstd");
					ArrayList<String> al=new ArrayList<String>();
					al.add("catalog");
					Node template,attr,comm=null;
					if(node.hasNode("template")){
						node.getNode("template").remove();
						template=node.addNode("template");
						attr=template.addNode("attr");
					}else{
						template=node.addNode("template");
						attr=template.addNode("attr");
					}
					
					for (int i = 0; i < attrLabel.length; i++) {
						Node labelNodeTech = session.getRootNode()
										.getNode("content")
										.getNode("attribute").getNode("label")
										.getNode(attrLabel[i]);
											Node at=attr.addNode(String.valueOf(i));
											at.setProperty("label",attrLabel[i]);
											at.setProperty("type",attrType[i]);
											at.setProperty("uom",attrUom[i]);
								NodeIterator typeNodeTech = session
										.getRootNode().getNode("content")
										.getNode("attribute").getNode("type")
										.getNode(attrType[i]).getNodes();

								while (typeNodeTech.hasNext()) {
									Node n = typeNodeTech.nextNode();
									 al.add(labelNodeTech.getProperty("name").getString()+"_"+n.getProperty("placeholder").getString());
								}

							}
						
						int o=0;
						if(al.size()>0){
							 o=request.getParameterValues(al.get(0)).length;
							
						}
						ArrayList<String>code=new ArrayList<String>();
						for(int k=0;k<o;k++){
							long l=prodNode.getProperty("prodCount").getLong();
							prodNode.addNode(String.valueOf(l+1));
							prodNode.setProperty("prodCount",l+1);
							code.add(String.valueOf(l+1));
						}
						
						for(int j=0;j<al.size();j++){
						String []para=request.getParameterValues(al.get(j));
								
							for(int m=0;m<code.size();m++){
								Node actulprocut=prodNode.getNode(code.get(m));
								if(actulprocut.hasNode("attr")){
									actulprocut=actulprocut.getNode("attr");
									actulprocut.setProperty(al.get(j),para[m]);
								}else{
									actulprocut=actulprocut.addNode("attr");
									actulprocut.setProperty(al.get(j),para[m]);	
									}
								
								}	
							}
						
				
						
				////////// Upload Images
						
										RequestParameter[] ap = request.getRequestParameterMap().get(
												"image");

										for (int m = 0; m < code.size(); m++) {
											Node actProd = prodNode.getNode(code.get(m));

											response.getWriter().print(
													code.get(m) + "----File name --------"
															+ ap[m].getFileName());

											if (ap[m].getFileName() != null
													&& ap[m].getInputStream() != null
													&& !ap[m].getFileName().trim().equals("")) {
												if (actProd.hasNode("media")) {
													actProd = actProd.getNode("media");
												} else {
													actProd = actProd.addNode("media");
												}

												if (actProd.hasNode("images")) {
													actProd.getNode("images").remove();
													actProd = actProd.addNode("images");

												} else {
													actProd = actProd.addNode("images");

												}
												// a=a.addNode(propList.get(y).split("_")[0]);

												actProd = actProd.addNode("0");

												Node fileNode = actProd.addNode("0", "nt:file");

												Node jcrNode = fileNode.addNode("jcr:content",
														"nt:resource");

												jcrNode.setProperty("jcr:data", ap[m].getInputStream());

												jcrNode.setProperty("jcr:mimeType", "image/jpeg");
												actProd.setProperty("imgpath",
														"/content/product/products/" + code.get(m)
																+ "/media/images/0/0");

											}

										}
										
								////////// Upload Videos
										
										RequestParameter[] ap1 = request.getRequestParameterMap().get(
												"video");

										for (int m = 0; m < code.size(); m++) {
											Node actProd = prodNode.getNode(code.get(m));

											response.getWriter().print(
													"----File name --------" + ap1[m].getFileName());
											if (ap1[m].getFileName() != null
													&& ap1[m].getInputStream() != null
													&& ap1[m].getFileName() != ""
													&& !ap1[m].getFileName().trim().equals("")) {

												if (actProd.hasNode("media")) {
													actProd = actProd.getNode("media");
												} else {
													actProd = actProd.addNode("media");
												}

												if (actProd.hasNode("videoes")) {
													actProd.getNode("videoes").remove();

													actProd = actProd.addNode("videoes");

												} else {

													actProd = actProd.addNode("videoes");

												}
												// a=a.addNode(propList.get(y).split("_")[0]);

												actProd = actProd.addNode("0");

												Node fileNode = actProd.addNode("0", "nt:file");

												Node jcrNode = fileNode.addNode("jcr:content",
														"nt:resource");

												jcrNode.setProperty("jcr:data", ap1[m].getInputStream());

												jcrNode.setProperty("jcr:mimeType", "video");
												actProd.setProperty("imgpath",
														"/content/product/products/" + code.get(m)
																+ "/media/videoes/0/0");

											}

										}
										
										////////// Upload Documents
										
										RequestParameter[] ap2 = request.getRequestParameterMap().get(
												"atta");

										for (int m = 0; m < code.size(); m++) {
											Node actProd = prodNode.getNode(code.get(m));

											response.getWriter().print(
													"----File name --------" + ap[m].getFileName());
											if (ap2[m].getFileName() != null
													&& ap2[m].getInputStream() != null
													&& ap2[m].getFileName() != ""
													&& !ap2[m].getFileName().trim().equals("")) {

												if (actProd.hasNode("media")) {
													actProd = actProd.getNode("media");
												} else {
													actProd = actProd.addNode("media");
												}
												if (actProd.hasNode("attachment")) {
													actProd.getNode("attachment").remove();
													actProd = actProd.addNode("attachment");

												} else {

													actProd = actProd.addNode("attachment");

												}
												actProd = actProd.addNode("0");
												actProd.setProperty("imgpath",
														"/content/product/products/" + code.get(m)
																+ "/media/attachment/0/0");
												Node fileNode = actProd.addNode("0", "nt:file");

												Node jcrNode = fileNode.addNode("jcr:content",
														"nt:resource");

												jcrNode.setProperty("jcr:data", ap2[m].getInputStream());

												jcrNode.setProperty("jcr:mimeType", "attach");

											}

										}

						
						
						
					}
//						}
//					}
//				}


			
			// response.getWriter().print("----))))-"+prod);
			response.getWriter().println("Path----allla pass)))-");
			session.save();

		} catch (PathNotFoundException e) {
			response.getWriter().print("Path----)))-" + e.getMessage());
			e.printStackTrace();
		} catch (RepositoryException e) {
			response.getWriter().print("Rrespo----)))-" + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			response.getWriter().print("Rrespo1----)))-" + e.getMessage());
			e.printStackTrace();
		}
		return a;
	}

	
	
	public String saveChildProduct(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws IOException {
		try {
			Session session = repo.login(new SimpleCredentials("admin", "admin"
					.toCharArray()));
			Node rootNode = session.getRootNode().getNode("content")
					.getNode("product");
			if (rootNode.hasNode("products")) {
				rootNode = rootNode.getNode("products");
			} else {
				rootNode = rootNode.addNode("products");
			}
			String id = request.getParameter("id");
			if (id != "new") {
				String path = request.getParameter("pathToAdd");
				String flowPath[] = path.split("/");
				for (int i = 0; i < flowPath.length; i++) {
					rootNode = rootNode.getNode(flowPath[i]);

				}

				Node templateNode = rootNode.getNode("template");
				NodeIterator attr = null, comm = null;

				if (templateNode.hasNode("attr")) {
					attr = templateNode.getNode("attr").getNodes();
				}

				if (templateNode.hasNode("comm")) {
					comm = templateNode.getNode("comm").getNodes();
				}
				ArrayList<String> propList = new ArrayList<String>();
				ArrayList<String> propListcomm = new ArrayList<String>();
				ArrayList<String> childNodename = new ArrayList<String>();

				if (attr != null) {
					while (attr.hasNext()) {
						Node a = attr.nextNode();
						response.getWriter().print(
								"----I an in 1--------" + a.getPath());
						String type = a.getProperty("type").getString();
						String label = a.getProperty("label").getString();

						response.getWriter().print("----I an in 2--------");
						NodeIterator typelist = session.getRootNode()
								.getNode("content").getNode("attribute")
								.getNode("type").getNode(type).getNodes();
						Node list1 = session.getRootNode().getNode("content")
								.getNode("attribute").getNode("label")
								.getNode(label);
						response.getWriter().print("----I an in3--------");

						while (typelist.hasNext()) {
							response.getWriter().print(
									"----I an in3 -whle llo[ --------");
							Node list = typelist.nextNode();

							propList.add(list1.getProperty("name").getString()
									+ "_"
									+ list.getProperty("placeholder")
											.getString());
							childNodename.add(list1.getProperty("id")
									.getString());
						}
					}
				}
				propList.add("catalog");
				propList.add("childProid");
				// propList.add("image");
				// propList.add("video");
				// propList.add("image");
				if (comm != null) {
					while (comm.hasNext()) {
						Node a = comm.nextNode();
						response.getWriter().print(
								"----I an in 1--------" + a.getPath());
						String type = a.getProperty("typeCom").getString();
						String label = a.getProperty("id").getString();

						response.getWriter().print("----I an in 2--------");
						NodeIterator typelist = session.getRootNode()
								.getNode("content").getNode("attribute")
								.getNode("type").getNode(type).getNodes();
						Node list1 = session.getRootNode().getNode("content")
								.getNode("attribute").getNode("commercial")
								.getNode(label);
						response.getWriter().print("----I an in3--------");

						while (typelist.hasNext()) {
							response.getWriter().print(
									"----I an in3 -whle llo[ --------");
							Node list = typelist.nextNode();

							propListcomm.add(list1.getProperty("name")
									.getString()
									+ "_"
									+ list.getProperty("placeholder")
											.getString());
							childNodename.add(list1.getProperty("id")
									.getString());
						}
					}
				}
				response.getWriter().print("----I out alll atribte --------");
				long prodcount = 0;
				HashMap<String, String[]> hm = new HashMap<String, String[]>();

				for (int p = 0; p < propList.size(); p++) {

					hm.put(propList.get(p),
							request.getParameterValues(propList.get(p)));
					prodcount = request.getParameterValues(propList.get(p)).length;
				}
				response.getWriter().print("----I an inrotaign --------");
				for (int p = 0; p < propListcomm.size(); p++) {

					hm.put(propListcomm.get(p),
							request.getParameterValues(propListcomm.get(p)));

				}
				response.getWriter().print("----I an inrotaign 233 --------");
				Node parentNode = rootNode;
				if (rootNode.hasNode("child")) {
					rootNode = rootNode.getNode("child");

				} else {
					rootNode = rootNode.addNode("child");
					rootNode.setProperty("ccount", Long.valueOf("0"));
				}

				// if(rootNode.getNodes().getSize()!=prodcount){
				String value1[] = hm.get("childProid");
				for (int o = 0; o < value1.length; o++) {
					if (rootNode.hasNode(value1[o])) {

					} else {
						Node chi = rootNode.addNode(value1[o]);
						Node childatt = chi.addNode("attr");
						Node childcomm = chi.addNode("comm");
						rootNode.setProperty("ccount", value1[o]);
					}
				}
				rootNode.setProperty("ccount", value1.length);
				// }
				for (int y = 0; y < propList.size() - 1; y++) {

					String value[] = hm.get(propList.get(y));
					int h = 0;
					for (int m = 0; m < value1.length; m++) {
						Node a = rootNode.getNode(value1[m]).getNode("attr");
						a.setProperty(propList.get(y), value[h]);
						h++;
					}
				}

				RequestParameter[] ap = request.getRequestParameterMap().get(
						"image");

				for (int m = 0; m < value1.length; m++) {
					Node node = rootNode.getNode(value1[m]);

					response.getWriter().print(
							value1[m] + "----File name --------"
									+ ap[m].getFileName());

					if (ap[m].getFileName() != null
							&& ap[m].getInputStream() != null
							&& !ap[m].getFileName().trim().equals("")) {
						if (node.hasNode("media")) {
							node = node.getNode("media");
						} else {
							node = node.addNode("media");
						}

						if (node.hasNode("images")) {
							node.getNode("images").remove();
							node = node.addNode("images");

						} else {
							node = node.addNode("images");

						}
						// a=a.addNode(propList.get(y).split("_")[0]);

						node = node.addNode("0");

						Node fileNode = node.addNode("0", "nt:file");

						Node jcrNode = fileNode.addNode("jcr:content",
								"nt:resource");

						jcrNode.setProperty("jcr:data", ap[m].getInputStream());

						jcrNode.setProperty("jcr:mimeType", "image/jpeg");
						node.setProperty("imgpath",
								"/content/product/products/" + value1[m]
										+ "/media/images/0/0");

					}

				}
				RequestParameter[] ap1 = request.getRequestParameterMap().get(
						"video");

				for (int m = 0; m < value1.length; m++) {
					Node node = rootNode.getNode(value1[m]);

					response.getWriter().print(
							"----File name --------" + ap1[m].getFileName());
					if (ap1[m].getFileName() != null
							&& ap1[m].getInputStream() != null
							&& ap1[m].getFileName() != ""
							&& !ap1[m].getFileName().trim().equals("")) {

						if (node.hasNode("media")) {
							node = node.getNode("media");
						} else {
							node = node.addNode("media");
						}

						if (node.hasNode("videoes")) {
							node.getNode("videoes").remove();

							node = node.addNode("videoes");

						} else {

							node = node.addNode("videoes");

						}
						// a=a.addNode(propList.get(y).split("_")[0]);

						node = node.addNode("0");

						Node fileNode = node.addNode("0", "nt:file");

						Node jcrNode = fileNode.addNode("jcr:content",
								"nt:resource");

						jcrNode.setProperty("jcr:data", ap1[m].getInputStream());

						jcrNode.setProperty("jcr:mimeType", "video");
						node.setProperty("imgpath",
								"/content/product/products/" + value1[m]
										+ "/media/videoes/0/0");

					}

				}
				RequestParameter[] ap2 = request.getRequestParameterMap().get(
						"atta");

				for (int m = 0; m < value1.length; m++) {
					Node node = rootNode.getNode(value1[m]);

					response.getWriter().print(
							"----File name --------" + ap[m].getFileName());
					if (ap2[m].getFileName() != null
							&& ap2[m].getInputStream() != null
							&& ap2[m].getFileName() != ""
							&& !ap2[m].getFileName().trim().equals("")) {

						if (node.hasNode("media")) {
							node = node.getNode("media");
						} else {
							node = node.addNode("media");
						}
						if (node.hasNode("attachment")) {
							node.getNode("attachment").remove();
							node = node.addNode("attachment");

						} else {

							node = node.addNode("attachment");

						}
						node = node.addNode("0");
						node.setProperty("imgpath",
								"/content/product/products/" + value1[m]
										+ "/media/attachment/0/0");
						Node fileNode = node.addNode("0", "nt:file");

						Node jcrNode = fileNode.addNode("jcr:content",
								"nt:resource");

						jcrNode.setProperty("jcr:data", ap2[m].getInputStream());

						jcrNode.setProperty("jcr:mimeType", "attach");

					}

				}
				for (int y = 0; y < propListcomm.size(); y++) {

					String value[] = hm.get(propListcomm.get(y));
					int h = 0;
					for (int m = 0; m < value1.length; m++) {

						Node a = rootNode.getNode(value1[m]).getNode("comm");
						// a=a.addNode(propList.get(y).split("_")[0]);
						a.setProperty(propListcomm.get(y), value[h]);
						h++;
					}
				}
				for(int k=0;k<Integer.parseInt(value1[value1.length-1]);k++){
					
					if(rootNode.hasNode(String.valueOf(k))){
						boolean isdelete=true;
						for(int f=0;f<value1.length;f++){
							if(k==Integer.valueOf(value1[f])){
								 isdelete=false;
								 break;
								 
							}
						}
						if(isdelete){							
							rootNode.getNode(String.valueOf(k)).remove();
						}
					}
				}
			}

			response.getWriter().print("----ggggg--------");
			session.save();
		} catch (Exception e) {
			response.getWriter().print("------------" + e.getMessage());

		}
		return null;
	}

	public HashMap getCategoryByNode(String dNodeStr, String qsrchparam,
			SlingHttpServletRequest request, SlingHttpServletResponse response) {

		HashMap resultList = new HashMap();
		String querryStr = "";
		Session session = null;
		Node tempPrdNode, childNode;

		qsrchparam = qsrchparam.toLowerCase();

		// querryStr =
		// "select * from [nt:unstructured] where (contains('name','*"+qsrchparam+"*'))  and ISDESCENDANTNODE('"+dNodeStr+"')";
		querryStr = "select * from [nt:unstructured] where  ISDESCENDANTNODE('"
				+ dNodeStr + "') and lower(name)  like '" + qsrchparam + "%'";
		try {
			session = repo.login(new SimpleCredentials("admin", "admin"
					.toCharArray()));
			Workspace workspace = session.getWorkspace();
			Query query = workspace.getQueryManager().createQuery(querryStr,
					Query.JCR_SQL2);
			QueryResult result = query.execute();
			NodeIterator iterator = result.getNodes();
			while (iterator.hasNext()) {
				tempPrdNode = iterator.nextNode();
				resultList.put(tempPrdNode.getProperty("unspsc").getString(),
						tempPrdNode.getProperty("name").getString());

			}

		}

		catch (PathNotFoundException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} finally {

		}
		return resultList;
	}

	public HashMap getCompanyByNode(String dNodeStr, String qsrchparam,
			SlingHttpServletRequest request, SlingHttpServletResponse response) {

		HashMap resultList = new HashMap();
		String querryStr = "";
		Session session = null;
		Node tempPrdNode, childNode;

		qsrchparam = qsrchparam.toLowerCase();
		// querryStr="select * from [nt:unstructured] where  ISDESCENDANTNODE('"+dNodeStr+"') and lower(name)  like '"+qsrchparam+"%'";
		querryStr = "select * from [nt:unstructured] where  (contains('creatorEmailId', '*"
				+ qsrchparam
				+ "*')) "
				+ "and ISDESCENDANTNODE('"
				+ dNodeStr
				+ "')";
		try {
			session = repo.login(new SimpleCredentials("admin", "admin"
					.toCharArray()));
			Workspace workspace = session.getWorkspace();
			Query query = workspace.getQueryManager().createQuery(querryStr,
					Query.JCR_SQL2);
			QueryResult result = query.execute();
			NodeIterator iterator = result.getNodes();
			while (iterator.hasNext()) {
				tempPrdNode = iterator.nextNode();
				tempPrdNode = tempPrdNode.getParent();
				resultList.put(tempPrdNode.getProperty("companyNumber")
						.getString(), tempPrdNode.getProperty("title")
						.getString());

			}

		}

		catch (PathNotFoundException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} finally {

		}
		return resultList;
	}

	public String uploadTemplateProducts(ArrayList<String> t,
			ArrayList<String> tech, Node toadd)
			throws IOException {
		String status = "";

		try {
			if (toadd.hasNode("child")) {
				toadd = toadd.getNode("child");
//				prodCount
//				Long g = toadd.getProperty("ccount").getLong();
//				toadd.setProperty("ccount", g + 1);
				Long g = toadd.getProperty("prodCount").getLong();
				toadd.setProperty("prodCount", g + 1);
				toadd = toadd.addNode(String.valueOf(g+1));

			} else {
				toadd = toadd.addNode("child");
//				toadd.setProperty("ccount", Long.valueOf("0"));
//				Long g = toadd.getProperty("ccount").getLong();
//				toadd = toadd.addNode(String.valueOf(g));
//				toadd.setProperty("ccount", g + 1);
				toadd.setProperty("prodCount", Long.valueOf("0"));
				Long g = toadd.getProperty("prodCount").getLong();
				toadd = toadd.addNode(String.valueOf(g));
				toadd.setProperty("prodCount", g + 1);
				
			}
			if (tech.size() > 0) {
				Node attr = toadd.addNode("attr");
				for (int i = 0; i < tech.size(); i++) {
					attr.setProperty(tech.get(i), t.get(i));
				}

			}
			

			status = "success";

		} catch (Exception e) {
			status = "i am in error "+e.getMessage();

		}
		return status;
	}

	public String uploadTemplateProductsBackup(ArrayList<String> t,
			ArrayList<String> tech, ArrayList<String> comm, Node toadd)
			throws IOException {
		String status = "";

		try {
			if (toadd.hasNode("child")) {
				toadd = toadd.getNode("child");
				Long g = toadd.getProperty("ccount").getLong();
				toadd.setProperty("ccount", g + 1);
				toadd = toadd.addNode(String.valueOf(g+1));

			} else {
				toadd = toadd.addNode("child");
				toadd.setProperty("ccount", Long.valueOf("0"));
				Long g = toadd.getProperty("ccount").getLong();
				toadd = toadd.addNode(String.valueOf(g));
				toadd.setProperty("ccount", g + 1);
				
			}
			if (tech.size() > 0) {
				Node attr = toadd.addNode("attr");
				for (int i = 0; i < tech.size(); i++) {
					attr.setProperty(tech.get(i), t.get(i));
				}

			}
			if (comm.size() > 0) {
				Node com = toadd.addNode("comm");
				int y=0;
				for (int j =comm.size()+1; j < t.size(); j++) {
					com.setProperty(comm.get(y++), t.get(j));
				}
			}

			status = "success";

		} catch (Exception e) {
			status = "i am in error "+e.getMessage();

		}
		return status;
	}

	
	public String readXLSTemplateFile(SlingHttpServletRequest request,
			SlingHttpServletResponse response, String pid) throws IOException {
		Session session = null;
		Node dcNode, jcrNode, docNode = null;

		String status = "";
		ArrayList<String> technical = new ArrayList<String>();
		ArrayList<String> commercial = new ArrayList<String>();
		try {

			if (request.getParameter("uploadtemplate") != null
					&& !request.getParameter("uploadtemplate").equals("")) {
				RequestParameter[] apDoc = request.getRequestParameterMap()
						.get("uploadtemplate");
				session = repo.login(new SimpleCredentials("admin", "admin"
						.toCharArray()));

				Node rootNode = session.getRootNode().getNode("content")
						.getNode("product").getNode("products");

				String id = request.getParameter("id");
				if (id != "new") {
					if (request.getParameter("catalogpath") != null) {
						String path = request.getParameter("catalogpath");
						String flowPath[] = path.split("/");
						for (int i = 0; i < flowPath.length; i++) {
							rootNode = rootNode.getNode(flowPath[i]);
						}
						
						// CHanges
						
						if (rootNode.hasNode("template")
								&& rootNode.getNode("template").hasNode("attr")) {
							Node techNode = rootNode.getNode("template")
									.getNode("attr");
							NodeIterator prop = techNode.getNodes();
							technical.add("catalog");
							while (prop.hasNext()) {
								Node p = prop.nextNode();
								Node labelNodeTech = session
										.getRootNode()
										.getNode("content")
										.getNode("attribute")
										.getNode("label")
										.getNode(
												p.getProperty("label")
														.getString());
								NodeIterator typeNodeTech = session
										.getRootNode()
										.getNode("content")
										.getNode("attribute")
										.getNode("type")
										.getNode(
												p.getProperty("type")
														.getString())
										.getNodes();

								while (typeNodeTech.hasNext()) {
									Node n = typeNodeTech.nextNode();
									technical.add(labelNodeTech.getProperty(
											"name").getString()
											+ "_"
											+ n.getProperty("placeholder")
													.getString());
								}

							}

						}
						
// Comented Backup COde						

//						if (rootNode.hasNode("template")
//								&& rootNode.getNode("template").hasNode("attr")
//								&& rootNode.getNode("template").hasNode("comm")) {
//							Node techNode = rootNode.getNode("template")
//									.getNode("attr");
//							Node comNode = rootNode.getNode("template")
//									.getNode("comm");
//							NodeIterator prop = techNode.getNodes();
//							NodeIterator fprop = comNode.getNodes();
//							technical.add("catalog");
//							while (prop.hasNext()) {
//								Node p = prop.nextNode();
//								Node labelNodeTech = session
//										.getRootNode()
//										.getNode("content")
//										.getNode("attribute")
//										.getNode("label")
//										.getNode(
//												p.getProperty("label")
//														.getString());
//								NodeIterator typeNodeTech = session
//										.getRootNode()
//										.getNode("content")
//										.getNode("attribute")
//										.getNode("type")
//										.getNode(
//												p.getProperty("type")
//														.getString())
//										.getNodes();
//
//								while (typeNodeTech.hasNext()) {
//									Node n = typeNodeTech.nextNode();
//									technical.add(labelNodeTech.getProperty(
//											"name").getString()
//											+ "_"
//											+ n.getProperty("placeholder")
//													.getString());
//								}
//
//							}
//
//							
//							while (fprop.hasNext()) {
//								Node f = fprop.nextNode();
//								Node labelNodeCom = session
//										.getRootNode()
//										.getNode("content")
//										.getNode("attribute")
//										.getNode("commercial")
//										.getNode(
//												f.getProperty("id").getString());
//								NodeIterator typeNodeCom = session
//										.getRootNode()
//										.getNode("content")
//										.getNode("attribute")
//										.getNode("type")
//										.getNode(
//												f.getProperty("typeCom")
//														.getString())
//										.getNodes();
//
//								while (typeNodeCom.hasNext()) {
//									Node n = typeNodeCom.nextNode();
//									commercial.add(labelNodeCom.getProperty(
//											"name").getString()
//											+ "_"
//											+ n.getProperty("placeholder")
//													.getString());
//								}
//
//							}
//
//						}

					}
				}

				for (int i = 0; i < 1; i++) {
					int a = 0;

					String docfilenam = apDoc[i].getFileName();
					if (docfilenam != null && !docfilenam.equals("")) {
						InputStream isp = apDoc[i].getInputStream();
						HSSFWorkbook workbook = new HSSFWorkbook(isp);
						HSSFSheet sheet = workbook.getSheetAt(0);
						Iterator rows = sheet.rowIterator();
						int x = 0;
						boolean firstRowIgnore = false;
						while (rows.hasNext()) {
							HSSFRow row = (HSSFRow) rows.next();

							if (firstRowIgnore) {

								firstRowIgnore = true;
								
								DataFormatter formatter = new DataFormatter();
								Iterator cells = row.cellIterator();
								ArrayList<String> data = new ArrayList<String>();
								while (cells.hasNext()) {
									// creating formatter using the default
									// locale
									HSSFCell cell = (HSSFCell) cells.next();
									data.add(formatter.formatCellValue(cell));
								}

								
								status = this.uploadTemplateProducts(data,
										technical,rootNode);
								response.getWriter().print(status+"----"+technical.size()+"---"+data.size()+
										"size is " + commercial.size());
							} else {
								if (x == 1) {
									firstRowIgnore = true;
								}
								x++;
							}
						}
					}

				}

				session.save();
				status = "success";

			}
		} catch (Exception e) {
			// response.getWriter().print(e.getMessage()+"geet");
			status = e.getMessage() + "sssssssssssss";
			response.getWriter().print(e.getMessage() + status);
		}
		return status;
	}

	@SuppressWarnings("deprecation")
	public String uploadAttachements(SlingHttpServletRequest request,
			SlingHttpServletResponse response, String pid) {
		String status = "";
		Session session = null;
		try {
			session = repo.login(new SimpleCredentials("admin", "admin"
					.toCharArray()));
			Node mediaNode = null;

			Node productNode = session.getRootNode().getNode("content")
					.getNode("product").getNode("products").getNode(pid);
			mediaNode = productNode.getNode("media");
			if (request.getParameter("img" + pid) != null
					&& !request.getParameter("img" + pid).equals("")) {
				Node imgNode, picNode, jcrNode = null;
				RequestParameter[] ap = request.getRequestParameterMap().get(
						"img" + pid);
				for (int i = 0; i < ap.length; i++) {

					String imgfilenam = ap[i].getFileName();
					// response.getWriter().print("inside file----)))-"+group);
					if (imgfilenam != null && !imgfilenam.equals("")) {
						picNode = mediaNode.addNode("images");
						// picNode = picNode.addNode(imgfilenam);

						imgNode = picNode.addNode(imgfilenam);
						imgNode.setProperty("imgpath",
								"/content/product/products/" + pid
										+ "/media/images/" + imgfilenam + "/"
										+ imgfilenam);
						imgNode = imgNode.addNode(imgfilenam, "nt:file");
						jcrNode = imgNode.addNode("jcr:content", "nt:resource");

						jcrNode.setProperty("jcr:data", ap[i].getInputStream());

						jcrNode.setProperty("jcr:mimeType", "image/jpeg");

					}
				}
			}

			if (request.getParameter("vid" + pid) != null
					&& !request.getParameter("vid" + pid).equals("")) {
				Node videoNode, vidNode, jcrNode = null;

				RequestParameter[] apVid = request.getRequestParameterMap()
						.get("vid" + pid);
				for (int i = 0; i < apVid.length; i++) {
					String vidfilenam = apVid[i].getFileName();
					// response.getWriter().print("inside file----)))-"+group);
					if (vidfilenam != null && !vidfilenam.equals("")) {
						videoNode = mediaNode.addNode("videoes");
						vidNode = videoNode.addNode(vidfilenam);
						vidNode.setProperty("imgpath",
								"/content/product/products/" + pid
										+ "/media/videoes/" + vidfilenam + "/"
										+ vidfilenam);
						vidNode = vidNode.addNode(vidfilenam, "nt:file");
						jcrNode = vidNode.addNode("jcr:content", "nt:resource");

						jcrNode.setProperty("jcr:data",
								apVid[i].getInputStream());
					}
				}
			}
			if (request.getParameter("att" + pid) != null
					&& !request.getParameter("att" + pid).equals("")) {
				Node videoNode, vidNode, jcrNode = null;
				Node docNode, dcNode = null;

				RequestParameter[] apDoc = request.getRequestParameterMap()
						.get("att" + pid);
				for (int i = 0; i < apDoc.length; i++) {

					String docfilenam = apDoc[i].getFileName();

					if (docfilenam != null && !docfilenam.equals("")) {
						docNode = mediaNode.addNode("attachments");

						dcNode = docNode.addNode(docfilenam);
						dcNode.setProperty("imgpath",
								"/content/product/products/" + pid
										+ "/media/attachments/" + docfilenam
										+ "/" + docfilenam);
						dcNode = dcNode.addNode(docfilenam, "nt:file");
						jcrNode = dcNode.addNode("jcr:content", "nt:resource");

						jcrNode.setProperty("jcr:data",
								apDoc[i].getInputStream());

					}
				}
			}
			session.save();
			status = "success";
		} catch (Exception e) {
			status = e.getMessage();

		}
		return status;
	}

	// /////////////

	public String readCountryMaster(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws IOException {
		Session session = null;
		Node dcNode, jcrNode, docNode, node = null;
		ArrayList<String> productCodes = new ArrayList<String>();
		String status = "failure";
		try {

			RequestParameter[] apDoc = request.getRequestParameterMap().get(
					"uploadtemplate");
			session = repo.login(new SimpleCredentials("admin", "admin"
					.toCharArray()));

			Node pr = session.getRootNode().getNode("content")
					.getNode("countrymaster");

			for (int i = 0; i < 1; i++) {
				int a = 0;

				String docfilenam = apDoc[i].getFileName();
				if (docfilenam != null && !docfilenam.equals("")) {
					InputStream isp = apDoc[i].getInputStream();
					HSSFWorkbook workbook = new HSSFWorkbook(isp);
					HSSFSheet sheet = workbook.getSheetAt(0);
					Iterator rows = sheet.rowIterator();
					int x = 0;
					boolean firstRowIgnore = false;
					while (rows.hasNext()) {
						HSSFRow row = (HSSFRow) rows.next();
						if (firstRowIgnore) {
							x = 0;
							a = 0;
							firstRowIgnore = true;
							TemplateDataBean t = new TemplateDataBean();
							ArrayList<String> at = new ArrayList<String>();
							ArrayList<String> fat = new ArrayList<String>();
							DataFormatter formatter = new DataFormatter();
							Iterator cells = row.cellIterator();
							ArrayList<String> data = new ArrayList<String>();
							String nodeName = "";
							while (cells.hasNext()) {
								// creating formatter using the default
								// locale
								HSSFCell cell = (HSSFCell) cells.next();

								String countryCode = formatter
										.formatCellValue(cell);
								response.getWriter().println(
										"Value" + countryCode);

								String no = countryCode.trim().split("/")[0];
								response.getWriter().println(
										"Value after splir " + no);
								if (nodeName == "" && !pr.hasNode(no.trim())) {
									response.getWriter().println(
											" goingt to Add" + no
													+ "------------"
													+ countryCode);
									node = pr.addNode(no.trim());
									response.getWriter().println(
											" Added" + countryCode);
								} else {
									response.getWriter().println(
											"Not Added" + countryCode);
								}

								if (x < 3) {

									switch (x) {
									case 0:
										node.setProperty("countrycode",
												countryCode.split("/")[0]);
										nodeName = countryCode;
										// node.setProperty("prd_code", s + 1);

										break;
									case 1:
										node.setProperty("dialingcode",
												formatter.formatCellValue(cell));
										break;
									case 2:
										node.setProperty("country",
												formatter.formatCellValue(cell));
										nodeName = "";
										break;
									}
									x++;
								}
							}

						} else {
							firstRowIgnore = true;
						}
					}
				}

			}

			session.save();
			status = "success";

		} catch (Exception e) {
			// response.getWriter().print(e.getMessage()+"geet");
			status = e.getMessage() + "sssssssssssss";
			response.getWriter().print(e.getMessage());
		}
		return status;
	}

	// /////////////

	public String readStateView(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws IOException {
		Session session = null;
		Node dcNode, jcrNode, docNode, node = null;
		ArrayList<String> productCodes = new ArrayList<String>();
		String status = "failure";
		try {

			RequestParameter[] apDoc = request.getRequestParameterMap().get(
					"uploadtemplate");
			session = repo.login(new SimpleCredentials("admin", "admin"
					.toCharArray()));

			Node pr1 = session.getRootNode().getNode("content")
					.getNode("address");
			Node pr = pr1.addNode("IN");
			for (int i = 0; i < 1; i++) {
				int a = 0;

				String docfilenam = apDoc[i].getFileName();
				if (docfilenam != null && !docfilenam.equals("")) {
					InputStream isp = apDoc[i].getInputStream();
					HSSFWorkbook workbook = new HSSFWorkbook(isp);
					HSSFSheet sheet = workbook.getSheetAt(0);
					Iterator rows = sheet.rowIterator();
					int x = 0;
					boolean firstRowIgnore = false;
					String nodeName = "";
					while (rows.hasNext()) {
						HSSFRow row = (HSSFRow) rows.next();
						if (firstRowIgnore) {
							x = 0;
							a = 0;
							firstRowIgnore = true;
							TemplateDataBean t = new TemplateDataBean();
							ArrayList<String> at = new ArrayList<String>();
							ArrayList<String> fat = new ArrayList<String>();
							DataFormatter formatter = new DataFormatter();
							Iterator cells = row.cellIterator();
							ArrayList<String> data = new ArrayList<String>();

							while (cells.hasNext()) {
								// creating formatter using the default
								// locale
								HSSFCell cell = (HSSFCell) cells.next();
								response.getWriter().println(
										"start------------"
												+ formatter
														.formatCellValue(cell));

								// String no=countryCode.trim().split("/")[0];
								// response.getWriter().println("Value after splir "+no);
								if (nodeName == ""
										&& !pr.hasNode(formatter
												.formatCellValue(cell))) {
									response.getWriter()
											.println(
													" goingt to Add------------"
															+ formatter
																	.formatCellValue(cell));
									node = pr.addNode(formatter
											.formatCellValue(cell));
									// response.getWriter().println(" Added"+countryCode);
								} else {
									response.getWriter()
											.println(
													"Not Added"
															+ formatter
																	.formatCellValue(cell));
								}

								response.getWriter().println(
										"end------------"
												+ formatter
														.formatCellValue(cell));

								if (x < 7) {

									switch (x) {
									case 0:
										response.getWriter()
												.println(
														"statecode"
																+ formatter
																		.formatCellValue(cell));
										node.setProperty("statecode",
												formatter.formatCellValue(cell));
										nodeName = formatter
												.formatCellValue(cell);
										// node.setProperty("prd_code", s + 1);

										break;
									case 1:
										response.getWriter()
												.println(
														"statename"
																+ formatter
																		.formatCellValue(cell));
										node.setProperty("statename",
												formatter.formatCellValue(cell));
										break;
									case 2:
										node.setProperty("stpopulation",
												formatter.formatCellValue(cell));
										// nodeName="";
										break;
									case 3:
										node.setProperty("stareakm",
												formatter.formatCellValue(cell));
										// nodeName="";
										break;
									case 4:
										node.setProperty("stcapital",
												formatter.formatCellValue(cell));
										// nodeName="";
										break;
									case 5:
										node.setProperty("stlatitude",
												formatter.formatCellValue(cell));
										// nodeName="";
										break;
									case 6:
										response.getWriter()
												.println(
														"stlongitude"
																+ formatter
																		.formatCellValue(cell));
										node.setProperty("stlongitude",
												formatter.formatCellValue(cell));
										nodeName = "";
										break;
									}
									x++;
								}
							}

						} else {
							firstRowIgnore = true;
						}
					}
				}

			}

			session.save();
			status = "success";

		} catch (Exception e) {
			// response.getWriter().print(e.getMessage()+"geet");
			status = e.getMessage() + "sssssssssssss";
			response.getWriter().print(e.getMessage());
		}
		return status;
	}

	// /////////////

	public String readDistrictView(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws IOException {
		Session session = null;
		Node dcNode, jcrNode, docNode, node = null;
		ArrayList<String> productCodes = new ArrayList<String>();
		String status = "failure";
		try {

			RequestParameter[] apDoc = request.getRequestParameterMap().get(
					"uploadtemplate");
			session = repo.login(new SimpleCredentials("admin", "admin"
					.toCharArray()));

			Node pr = session.getRootNode().getNode("content")
					.getNode("address").getNode("IN").getNode("MH");
			// Node pr = pr1.addNode("IN");
			for (int i = 0; i < 1; i++) {
				int a = 0;

				String docfilenam = apDoc[i].getFileName();
				if (docfilenam != null && !docfilenam.equals("")) {
					InputStream isp = apDoc[i].getInputStream();
					HSSFWorkbook workbook = new HSSFWorkbook(isp);
					HSSFSheet sheet = workbook.getSheetAt(0);
					Iterator rows = sheet.rowIterator();
					int x = 0;
					boolean firstRowIgnore = false;
					String nodeName = "";
					while (rows.hasNext()) {
						HSSFRow row = (HSSFRow) rows.next();
						if (firstRowIgnore) {
							x = 0;
							a = 0;
							firstRowIgnore = true;
							TemplateDataBean t = new TemplateDataBean();
							ArrayList<String> at = new ArrayList<String>();
							ArrayList<String> fat = new ArrayList<String>();
							DataFormatter formatter = new DataFormatter();
							Iterator cells = row.cellIterator();
							ArrayList<String> data = new ArrayList<String>();

							while (cells.hasNext()) {
								// creating formatter using the default
								// locale
								HSSFCell cell = (HSSFCell) cells.next();

								// String no=countryCode.trim().split("/")[0];
								// response.getWriter().println("Value after splir "+no);
								if (nodeName == ""
										&& !pr.hasNode(formatter
												.formatCellValue(cell))) {
									response.getWriter()
											.println(
													" goingt to Add------------"
															+ formatter
																	.formatCellValue(cell));
									node = pr.addNode(formatter
											.formatCellValue(cell));
									// response.getWriter().println(" Added"+countryCode);
								} else {
									response.getWriter()
											.println(
													"Not Added"
															+ formatter
																	.formatCellValue(cell));
								}

								if (x < 6) {

									switch (x) {
									case 0:

										node.setProperty("distcode",
												formatter.formatCellValue(cell));

										nodeName = formatter
												.formatCellValue(cell);
										// node.setProperty("prd_code", s + 1);

										break;
									case 1:

										node.setProperty("distatename",
												formatter.formatCellValue(cell));
										break;
									case 2:
										node.setProperty("distpopulation",
												formatter.formatCellValue(cell));
										// nodeName="";
										break;
									case 3:
										node.setProperty("distareakm",
												formatter.formatCellValue(cell));
										// nodeName="";
										break;
									case 4:
										node.setProperty("distlatitude",
												formatter.formatCellValue(cell));
										// nodeName="";
										break;
									case 5:

										node.setProperty("distlongitude",
												formatter.formatCellValue(cell));
										nodeName = "";
										break;
									}
									x++;
								}
							}

						} else {
							firstRowIgnore = true;
						}
					}
				}

			}

			session.save();
			status = "success";

		} catch (Exception e) {
			// response.getWriter().print(e.getMessage()+"geet");
			status = e.getMessage() + "sssssssssssss";
			response.getWriter().print(e.getMessage());
		}
		return status;
	}

	// /////////////

	public String readCityView(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws IOException {
		Session session = null;
		Node dcNode, jcrNode, docNode, node = null;
		ArrayList<String> productCodes = new ArrayList<String>();
		String status = "failure";
		try {

			RequestParameter[] apDoc = request.getRequestParameterMap().get(
					"uploadtemplate");
			session = repo.login(new SimpleCredentials("admin", "admin"
					.toCharArray()));

			Node pr = session.getRootNode().getNode("content")
					.getNode("address").getNode("IN").getNode("MH")
					.getNode("TH");
			// Node pr = pr1.addNode("IN");
			for (int i = 0; i < 1; i++) {
				int a = 0;

				String docfilenam = apDoc[i].getFileName();
				if (docfilenam != null && !docfilenam.equals("")) {
					InputStream isp = apDoc[i].getInputStream();
					HSSFWorkbook workbook = new HSSFWorkbook(isp);
					HSSFSheet sheet = workbook.getSheetAt(0);
					Iterator rows = sheet.rowIterator();
					int x = 0;
					boolean firstRowIgnore = false;
					String nodeName = "";
					int nodeNum = 10;
					while (rows.hasNext()) {
						HSSFRow row = (HSSFRow) rows.next();
						if (firstRowIgnore) {
							x = 0;
							a = 0;
							firstRowIgnore = true;
							TemplateDataBean t = new TemplateDataBean();
							ArrayList<String> at = new ArrayList<String>();
							ArrayList<String> fat = new ArrayList<String>();
							DataFormatter formatter = new DataFormatter();
							Iterator cells = row.cellIterator();
							ArrayList<String> data = new ArrayList<String>();

							while (cells.hasNext()) {
								// creating formatter using the default
								// locale
								HSSFCell cell = (HSSFCell) cells.next();

								// String no=countryCode.trim().split("/")[0];
								// response.getWriter().println("Value after splir "+no);
								if (nodeName == ""
										&& !pr.hasNode(String.valueOf(nodeNum))) {

									node = pr.addNode(String.valueOf(nodeNum));
									response.getWriter().println(
											"mode no if==="
													+ String.valueOf(nodeNum));
									node.setProperty("citycode",
											String.valueOf(nodeNum));

									nodeName = String.valueOf(nodeNum);
									// response.getWriter().println(" Added"+countryCode);
								} else {
									response.getWriter().println(
											"mode no if else==="
													+ String.valueOf(nodeNum));
								}

								if (x < 6) {

									switch (x) {
									case 0:

										node.setProperty("cityname",
												formatter.formatCellValue(cell));

										// node.setProperty("prd_code", s + 1);

										break;
									case 1:

										node.setProperty("citypincode",
												formatter.formatCellValue(cell));
										break;
									case 2:
										node.setProperty("citypopulation",
												formatter.formatCellValue(cell));
										// nodeName="";
										break;
									case 3:
										node.setProperty("cityareakm",
												formatter.formatCellValue(cell));
										// nodeName="";
										break;
									case 4:
										node.setProperty("citylatitude",
												formatter.formatCellValue(cell));
										// nodeName="";
										break;
									case 5:

										node.setProperty("citylongitude",
												formatter.formatCellValue(cell));
										nodeName = "";
										break;
									}
									x++;
								}
							}
							nodeNum++;
						} else {
							firstRowIgnore = true;
						}
					}
				}

			}

			session.save();
			status = "success";

		} catch (Exception e) {
			// response.getWriter().print(e.getMessage()+"geet");
			status = e.getMessage() + "sssssssssssss";
			response.getWriter().print(e.getMessage());
		}
		return status;
	}

	// /////////////

	public String readPincodeView(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws IOException {
		Session session = null;
		Node dcNode, jcrNode, docNode, node = null;
		ArrayList<String> productCodes = new ArrayList<String>();
		String status = "failure";
		try {

			RequestParameter[] apDoc = request.getRequestParameterMap().get(
					"uploadtemplate");
			session = repo.login(new SimpleCredentials("admin", "admin"
					.toCharArray()));

			Node pr = session.getRootNode().getNode("content")
					.getNode("pincode");
			// Node pr = pr1.addNode("IN");
			for (int i = 0; i < 1; i++) {
				int a = 0;

				String docfilenam = apDoc[i].getFileName();
				if (docfilenam != null && !docfilenam.equals("")) {
					InputStream isp = apDoc[i].getInputStream();
					HSSFWorkbook workbook = new HSSFWorkbook(isp);
					HSSFSheet sheet = workbook.getSheetAt(0);
					Iterator rows = sheet.rowIterator();
					int x = 0;
					boolean firstRowIgnore = false;
					String nodeName = "";
					while (rows.hasNext()) {
						HSSFRow row = (HSSFRow) rows.next();
						if (firstRowIgnore) {
							x = 0;
							a = 0;
							firstRowIgnore = true;
							TemplateDataBean t = new TemplateDataBean();
							ArrayList<String> at = new ArrayList<String>();
							ArrayList<String> fat = new ArrayList<String>();
							DataFormatter formatter = new DataFormatter();
							Iterator cells = row.cellIterator();
							ArrayList<String> data = new ArrayList<String>();

							while (cells.hasNext()) {
								// creating formatter using the default
								// locale
								HSSFCell cell = (HSSFCell) cells.next();

								// String no=countryCode.trim().split("/")[0];
								// response.getWriter().println("Value after splir "+no);
								if (nodeName == ""
										&& !pr.hasNode(formatter
												.formatCellValue(cell))) {
									response.getWriter()
											.println(
													" goingt to Add------------"
															+ formatter
																	.formatCellValue(cell));
									node = pr.addNode(formatter
											.formatCellValue(cell));
									// response.getWriter().println(" Added"+countryCode);
								} else {
									response.getWriter()
											.println(
													"Not Added"
															+ formatter
																	.formatCellValue(cell));
								}

								if (x < 6) {

									switch (x) {
									case 0:

										node.setProperty("pincode",
												formatter.formatCellValue(cell));

										nodeName = formatter
												.formatCellValue(cell);
										// node.setProperty("prd_code", s + 1);

										break;
									case 1:

										node.setProperty("countrycode",
												formatter.formatCellValue(cell));
										break;
									case 2:
										node.setProperty("statecode",
												formatter.formatCellValue(cell));
										// nodeName="";
										break;
									case 3:
										node.setProperty("districtcode",
												formatter.formatCellValue(cell));
										// nodeName="";
										break;
									case 4:
										node.setProperty("citycode",
												formatter.formatCellValue(cell));
										nodeName = "";
										break;

									}
									x++;
								}
							}

						} else {
							firstRowIgnore = true;
						}
					}
				}

			}

			session.save();
			status = "success";

		} catch (Exception e) {
			// response.getWriter().print(e.getMessage()+"geet");
			status = e.getMessage() + "sssssssssssss";
			response.getWriter().print(e.getMessage());
		}
		return status;
	}

	public ArrayList<String> validateXLSFile(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws IOException {
		ArrayList<String> isFileDataValid = new ArrayList<String>();
		try {
			Session session;
			
			session = repo.login(new SimpleCredentials("admin", "admin"
					.toCharArray()));
			ArrayList<String> technical = new ArrayList<String>();
			ArrayList<String> commercial = new ArrayList<String>();
			ArrayList<String> cellType = new ArrayList<String>();
						
			
			if (request.getParameter("uploadtemplate") != null
					&& !request.getParameter("uploadtemplate").equals("")) {
				RequestParameter[] apDoc = request.getRequestParameterMap()
						.get("uploadtemplate");
				session = repo.login(new SimpleCredentials("admin", "admin"
						.toCharArray()));

				Node rootNode = session.getRootNode().getNode("content")
						.getNode("product").getNode("products");
				cellType.add("String");
				String id = request.getParameter("id");
				if (id != "new") {
					if (request.getParameter("catalogpath") != null) {
						String path = request.getParameter("catalogpath");
						String flowPath[] = path.split("/");
						for (int i = 0; i < flowPath.length; i++) {
							rootNode = rootNode.getNode(flowPath[i]);
						}

						/// Changes 
						
						if (rootNode.hasNode("template")
								&& rootNode.getNode("template").hasNode("attr")) {
							Node techNode = rootNode.getNode("template")
									.getNode("attr");
							NodeIterator prop = techNode.getNodes();
							while (prop.hasNext()) {
								Node p = prop.nextNode();
								Node labelNodeTech = session
										.getRootNode()
										.getNode("content")
										.getNode("attribute")
										.getNode("label")
										.getNode(
												p.getProperty("label")
														.getString());
								NodeIterator typeNodeTech = session
										.getRootNode()
										.getNode("content")
										.getNode("attribute")
										.getNode("type")
										.getNode(
												p.getProperty("type")
														.getString())
										.getNodes();
								Node uomNodeTech = session
										.getRootNode()
										.getNode("content")
										.getNode("uom")
										.getNode(
												p.getProperty("uom").getString());
								
								while (typeNodeTech.hasNext()) {
									Node n = typeNodeTech.nextNode();
									technical.add(labelNodeTech.getProperty(
											"name").getString()
											+ "_"
											+ n.getProperty("placeholder")
													.getString());
									cellType.add(uomNodeTech.getProperty("uomDataType").getString());
								}

							}


						}
						
// Comeneted Backup Code						
						
//						if (rootNode.hasNode("template")
//								&& rootNode.getNode("template").hasNode("attr")
//								&& rootNode.getNode("template").hasNode("comm")) {
//							Node techNode = rootNode.getNode("template")
//									.getNode("attr");
//							Node comNode = rootNode.getNode("template")
//									.getNode("comm");
//							NodeIterator prop = techNode.getNodes();
//							NodeIterator fprop = comNode.getNodes();
//							while (prop.hasNext()) {
//								Node p = prop.nextNode();
//								Node labelNodeTech = session
//										.getRootNode()
//										.getNode("content")
//										.getNode("attribute")
//										.getNode("label")
//										.getNode(
//												p.getProperty("label")
//														.getString());
//								NodeIterator typeNodeTech = session
//										.getRootNode()
//										.getNode("content")
//										.getNode("attribute")
//										.getNode("type")
//										.getNode(
//												p.getProperty("type")
//														.getString())
//										.getNodes();
//								Node uomNodeTech = session
//										.getRootNode()
//										.getNode("content")
//										.getNode("uom")
//										.getNode(
//												p.getProperty("uom").getString());
//								
//								while (typeNodeTech.hasNext()) {
//									Node n = typeNodeTech.nextNode();
//									technical.add(labelNodeTech.getProperty(
//											"name").getString()
//											+ "_"
//											+ n.getProperty("placeholder")
//													.getString());
//									cellType.add(uomNodeTech.getProperty("uomDataType").getString());
//								}
//
//							}
//
//							while (fprop.hasNext()) {
//								Node f = fprop.nextNode();
//								Node labelNodeCom = session
//										.getRootNode()
//										.getNode("content")
//										.getNode("attribute")
//										.getNode("commercial")
//										.getNode(
//												f.getProperty("id").getString());
//								NodeIterator typeNodeCom = session
//										.getRootNode()
//										.getNode("content")
//										.getNode("attribute")
//										.getNode("type")
//										.getNode(
//												f.getProperty("typeCom")
//														.getString())
//										.getNodes();
//								Node uomNodeTech = session
//										.getRootNode()
//										.getNode("content")
//										.getNode("uom")
//										.getNode(
//												f.getProperty("uomCom").getString());
//								
//								while (typeNodeCom.hasNext()) {
//									Node n = typeNodeCom.nextNode();
//									commercial.add(labelNodeCom.getProperty(
//											"name").getString()
//											+ "_"
//											+ n.getProperty("placeholder")
//													.getString());
//									cellType.add(uomNodeTech.getProperty("uomDataType").getString());
//
//								}
//
//							}
//
//						}

					}
				}

				for (int i = 0; i < 1; i++) {
					int a = 0;

					String docfilenam = apDoc[i].getFileName();
					if (docfilenam != null && !docfilenam.equals("")) {
						InputStream isp = apDoc[i].getInputStream();
						HSSFWorkbook workbook = new HSSFWorkbook(isp);
						HSSFSheet sheet = workbook.getSheetAt(0);
						Iterator rows = sheet.rowIterator();
						int x = 0;
						boolean firstRowIgnore = false;
						while (rows.hasNext()) {
							HSSFRow row = (HSSFRow) rows.next();

							if (firstRowIgnore) {

								firstRowIgnore = true;
								TemplateDataBean t = new TemplateDataBean();
								ArrayList<String> at = new ArrayList<String>();
								ArrayList<String> fat = new ArrayList<String>();
								DataFormatter formatter = new DataFormatter();
								Iterator cells = row.cellIterator();
								ArrayList<String> data = new ArrayList<String>();
								int rowid=0;
								while (cells.hasNext()) {
									// creating formatter using the default
									// locale
									HSSFCell cell = (HSSFCell) cells.next();
									//data.add();
									response.getWriter().print("----------------------"+this.getDataValidation(cellType.get(rowid), formatter.formatCellValue(cell)));
									if(!this.getDataValidation(cellType.get(rowid++), formatter.formatCellValue(cell))){
										if(cellType.get(rowid-1).equalsIgnoreCase("Double")){
										isFileDataValid.add((x+1)+"*"+(rowid)+" cell value should be Numeric");
										}else{
											isFileDataValid.add((x+1)+"*"+(rowid)+" cell value should be "+cellType.get(rowid-1));
										}
									}
									
								}

								
								x++;
							} else {
								if (x == 1) {  
									firstRowIgnore =  true;
								}
								x++;
							}
						}
					}

				}

			}
			} catch (Exception e) {
			response.getWriter().print("----------------------"+e.getMessage());
			isFileDataValid.add("There is some problem with file or file data.");
		}

		return isFileDataValid;
	}
	
	public boolean getDataValidation(String dataType,String value){
		boolean isFalse=true;
			if("String".equals(dataType)){
				Pattern objEmailP = Pattern.compile("^[a-zA-Z0-9]*$");
		        Matcher objEmailM = objEmailP.matcher(value);		
		        boolean bemail1 = objEmailM.matches();
	        	if(bemail1 == false){
	        		isFalse=false;
	        	}
				
			}else { 
				String mobpattern = "\\d{0,15}";	
				
	        	if(!value.matches(mobpattern)){
	        		isFalse=false;
	        	}
			}
			return isFalse;
		}
	
	public String callGetService(String urlStr, String[] paramName,
			String[] paramValue) {
		URL url;
		StringBuilder requestString = new StringBuilder(urlStr);
		if (paramName != null && paramName.length > 0) {
			requestString.append("?");
			for (int i = 0; i < paramName.length; i++) {
				requestString.append(paramName[i]);
				requestString.append("=");
				requestString.append(paramValue[i]);
				requestString.append("&");
			}
		}
		StringBuilder sb = new StringBuilder();
		try {
			url = new URL(requestString.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				System.out.println(line);
			}
			br.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();

		}
		return sb.toString();
	}
	
	public String callPostService(String urlStr, String[] paramName,
			String[] paramValue) {

		StringBuilder response = new StringBuilder();
		URL url;
		try {
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
			response = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				response.append(line);
			}
			rd.close();

			conn.disconnect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			response.append(e.getMessage());

		}
		return response.toString();
	}
	
	public String sendEmailToken(String values[]) {
	String message = "";
	String url = "";
	String result = "";
		try {
			
			ResourceBundle bundle = ResourceBundle.getBundle("server");
				
				if (true) {
					message = values[1];
					url = bundle.getString("sendMail.address");
					String[] paramName = { "emailto[]", "emailfrom[]",
							"emailsub[]", "emailmsg[]" };
					String[] paramValue = { values[0],
							bundle.getString("sendMail.from"),
							values[2], message };
					result = callPostService(url, paramName, paramValue);
				} else {
					result = "SE";

				}
			
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return result;
	}

}