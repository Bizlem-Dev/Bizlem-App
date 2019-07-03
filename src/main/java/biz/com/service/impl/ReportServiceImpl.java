package biz.com.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

import biz.com.service.ReportService;

/**
 * 
 * @author atul
 */
@Component(configurationFactory = true)
@Service(ReportService.class)
@Properties({ @Property(name = "com", value = "com") })
public class ReportServiceImpl implements ReportService {

	private ResourceBundle bundle;

	public String paidAddReport(HttpServletRequest request,
			HttpServletResponse response, HashMap<String, String> mp) {
		request.setAttribute("url", mp.get("url"));
		Date date = new Date();
		try {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String DateToStr = format.format(date);
		request.setAttribute("todate", DateToStr);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, -1);
		String frodate = format.format(cal.getTime());
		request.setAttribute("fromdate", frodate);
		String[] paramName = {"serviceId"};
		String[] paramValue = {request.getParameter("name")};
		String title = this.callGetService(
				ResourceBundle.getBundle("serverConfig").getString(
						"reportwebservice"), paramName, paramValue);
		if (title != null)
			request.setAttribute("segment", title);

		String[] paramName1 = { "userId" };
		String[] paramValue1 = { request.getRemoteUser() };
		String part = this.callGetService(
				ResourceBundle.getBundle("serverConfig").getString(
						"reportwebservice"), paramName1, paramValue1);
		request.setAttribute("idsite", part.split(",")[0]);
		request.setAttribute("authcode", part.split(",")[1]);
		String[] para = {"email","sdate","edate"};
		String[] paraval = {request.getRemoteUser(),frodate,DateToStr};
		String string = this.callGetService("http://34.193.219.25/bsearch/report.php", para, paraval);
		ArrayList<String> al = new ArrayList<String>();
		
			JSONArray jarry = new JSONArray(string);
			for (int i = 0; i < jarry.length(); i++) {
				if (jarry.getJSONObject(i).get("campaignName").equals(title)) {
					al.add(jarry.getJSONObject(i).getString("campaignName"));
					al.add(jarry.getJSONObject(i).getString("impressions"));
					al.add(jarry.getJSONObject(i).getString("clicks"));
					al.add(jarry.getJSONObject(i).getString("revenue"));
					double click=Double.valueOf((jarry.getJSONObject(i).getString("clicks")));
					double impre=Double.valueOf((jarry.getJSONObject(i).getString("impressions")));
					al.add(String.valueOf((click*100)/impre));
					
					//response.getWriter().print(jarry.getJSONObject(i).getString("impressions"));
				}

			}
			request.setAttribute("openx", al);
		
		} catch (Exception e) {
			
		}
		return "paidsearchreport";
	}

	public String fetchReport(HttpServletRequest request,
			HttpServletResponse response, HashMap<String, String> mp) {
		String[] servicess = ResourceBundle.getBundle("serverConfig")
				.getString("services").split(",");
String bannerServices[]=ResourceBundle.getBundle("serverConfig")
.getString("bannerServices").split(",");
		String redirect="";
		if (mp.get("productCode").equals(servicess[0])) {

		} else if (mp.get("productCode").equals(servicess[1])) {

		} else if (mp.get("productCode").equals(servicess[2])) {

		} else if (mp.get("productCode").equals(servicess[3])) {

		} else if (mp.get("productCode").equals(servicess[4])) {

		} else if (mp.get("productCode").equals(servicess[5])) {

		} else if (mp.get("productCode").equals(servicess[6])) {

		} else if (mp.get("productCode").equals(servicess[7])) {

		} else if (mp.get("productCode").equals(servicess[8])) {

		} else if (mp.get("productCode").equals(servicess[9])) {

		} else if (mp.get("productCode").equals(servicess[10])) {

		} else if (mp.get("productCode").equals(servicess[11])) {

		} else if (mp.get("productCode").equals(servicess[12])) {

		} else if (mp.get("productCode").equals(servicess[13])) {
			redirect=this.paidAddReport(request, response, mp);
		} else if (mp.get("productCode").equals(servicess[14])) {
			redirect=this.paidAddReport(request, response, mp);
		} else if (mp.get("productCode").equals(servicess[15])) {

		} else if (mp.get("productCode").equals(servicess[16])) {

		} else if (mp.get("productCode").equals(servicess[17])) {

		} else if (mp.get("productCode").equals(servicess[18])) {

		}else {	
			for(int i=0;i<bannerServices.length;i++){
				if (mp.get("productCode").equals(bannerServices[i]));
				redirect=this.paidAddReport(request, response, mp);
			 }
			
		}
		return redirect;
	}

	public String getContentName() {

		return null;
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
			System.out.println("caalign callPostService");
			url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setAllowUserInteraction(false);
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
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
				System.out.println("indeside caalign responce"
						+ conn.getResponseCode());
			}
			System.out.println("caalign responce");
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			response = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				response.append(line);
			}
			rd.close();
			System.out.println("closinng conneciotn" + conn.getResponseCode());
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();

		}
		System.out.println("priting responce");
		System.out.println(response.toString() + "~~~~~~~~~~~~~~~~~~~~~~~");
		return response.toString();
	}
}