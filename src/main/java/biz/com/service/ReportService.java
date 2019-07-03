package biz.com.service;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface ReportService {

public String paidAddReport(HttpServletRequest request,
		HttpServletResponse response,HashMap<String, String> mp);
public String fetchReport(HttpServletRequest request,HttpServletResponse response,HashMap<String, String> mp);


}