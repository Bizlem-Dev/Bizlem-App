package biz.com.service.impl;
import biz.com.service.SendMail;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ResourceBundle;
import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.jcr.api.SlingRepository;



@Component(configurationFactory = true)
@Service(SendMail.class)
@Properties({ @Property(name = "mail", value = "sendmail") })
public class SendMailImpl implements SendMail{

	/** The repo variable is an object of SlingRepository interface. */

	@Reference
	private SlingRepository repos;

	Session session = null;
	private ResourceBundle bundle;

	@SuppressWarnings("unused")
	public String sendMail(String cutomerId,String userId,String subject,String message) {
		 Session session = null;
	        Node userNode = null;
	        try {
	            
	            bundle = ResourceBundle.getBundle("server");
	            if (true) {

	                String response = callGetService(bundle
	                        .getString("service.consumption")
	                        + "&userId="
	                        + userId
	                        + "&quantity=1&productCode=email");
	                boolean accessFlag = false;
	                JSONObject json = new JSONObject(response);
	                accessFlag = json.getBoolean("accessFlag");
	                if (true) {
	                    String url = bundle.getString("sendMail.address");
	                    String[] paramName = { "emailto[]", "emailfrom[]",
	                            "emailsub[]", "emailmsg[]" };
	                    String[] paramValue = { userId,
	                            bundle.getString("sendMail.from"), subject, message };
	                    callService(url, paramName, paramValue);
	                    return "Mail Sent SuccessFully";
	                } else {
	                    return "Limit Exceeded";
	                }
	            } else {
	                return "Not Provisioned";
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            return "Mail Sent Failed";
	        } finally {
	            session.logout();
	        }
		
	}  public String callService(String urlStr, String[] paramName,
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
            e.printStackTrace();

        }
        return response.toString();
    }
 public String callGetService(String urlString) {

        URL url;
        HttpURLConnection urlConn;
        DataOutputStream printout;
        DataInputStream input;
        StringBuilder sb = new StringBuilder();
        try {
            url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            conn.disconnect();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        return sb.toString();
    }
 public void main(){
	 
	 
 }
}
