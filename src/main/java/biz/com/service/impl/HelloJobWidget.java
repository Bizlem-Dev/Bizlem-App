package biz.com.service.impl;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class HelloJobWidget implements Job
{
	public void execute(JobExecutionContext context)
	throws JobExecutionException {
		JobDetail jobDetail = context.getJobDetail();
		
//		System.out.println("http://prod.bizlem.io:8082/portal/servlet/service/news.schedularcall?urlid=" + jobDetail.getJobDataMap().getString("example"));
			this.callGetService("http://prod.bizlem.io:8082/portal/servlet/service/news.schedularcall?node=widget&urlid="+jobDetail.getJobDataMap().getString("example"), null, null);
		
	}
	public StringBuilder callGetService(String urlStr, String[] paramName,
			String[] paramValue) {
		URL url;
		StringBuilder requestString = new StringBuilder(urlStr);
		StringBuilder sb = new StringBuilder();
		InputStream stream=null;
		try {
			url = new URL(requestString.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			stream=conn.getInputStream();
			 BufferedReader rd = new BufferedReader(new InputStreamReader(
						stream));
				
				String line;
				while ((line = rd.readLine()) != null) {
					sb.append(line);
				}
				rd.close();
	         //conn.disconnect();
			conn.disconnect();
			System.out.println(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();

		}
		return sb;
	}

}
