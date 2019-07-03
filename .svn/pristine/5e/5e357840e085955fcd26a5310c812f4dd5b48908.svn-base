package biz.com.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

public interface NetworkExpandService {
public String addContact(SlingHttpServletRequest request);
void importContacts(String userName, String providerId, String emailNode,
        String email, String firstName, String lastName);

String uploadImportedContacts(SlingHttpServletRequest request)
        throws ServletException, IOException;

String addImportedContacts(String userName, String providerId,
        String emailNode, ArrayList<String> importedValues);
String callService(String urlStr, String[] paramName,
		String[] paramValue);

public String processInvite(SlingHttpServletRequest request,SlingHttpServletResponse responce);
public String createUser(String emails) ;
public String inviteEvent(SlingHttpServletRequest request,
		SlingHttpServletResponse response) throws IOException ;
public String inviteGroup(SlingHttpServletRequest request,
		SlingHttpServletResponse response) ;
}
