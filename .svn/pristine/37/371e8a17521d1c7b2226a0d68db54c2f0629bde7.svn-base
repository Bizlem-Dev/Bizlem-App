package biz.com.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.jcr.api.SlingRepository;

import biz.com.service.CompanyService;
import org.apache.sling.api.SlingHttpServletResponse;
/**
 * 
 * @author atul
 */
@Component(configurationFactory = true)
@Service(CompanyService.class)
@Properties({ @Property(name = "com", value = "com") })
public class CompanyServiceImpl implements CompanyService {

	/** The repo variable is an object of SlingRepository interface. */

	@Reference
	private SlingRepository repo;

	Session session = null;

	public ArrayList<Node> getPersonList(String searchText) {
		ArrayList<Node> pb = null;

		Node tempPrdNode, childNode;
		try {

			if (searchText!=null && !searchText.trim().equals("")) {

				pb = new ArrayList<Node>();
				session = repo.login(new SimpleCredentials("admin", "admin"
						.toCharArray()));

//				String querryStr = "select * from [nt:unstructured] where  ISDESCENDANTNODE('/content/user/') and name  like '"
//						+ searchText + "%'";
				String querryStr = "select * from [nt:unstructured] where  ISDESCENDANTNODE('/content/user/') and  (contains('name','*"+searchText +"*'))";
				Workspace workspace = session.getWorkspace();
				Query query = workspace.getQueryManager().createQuery(
						querryStr, Query.JCR_SQL2);
				QueryResult result = query.execute();
				NodeIterator iterator = result.getNodes();

				while (iterator.hasNext()) {

					tempPrdNode = iterator.nextNode();
					pb.add(tempPrdNode);
					
				}
			} else {
				NodeIterator iterator = session.getRootNode()
						.getNode("content").getNode("user").getNodes();
				while (iterator.hasNext()) {

					tempPrdNode = iterator.nextNode();
					pb.add(tempPrdNode.getParent());
					

				}

			}
		} catch (LoginException e) {

		} catch (RepositoryException e) {
			// TODO Auto-generated catch block

		} catch (Exception e) {
			// TODO Auto-generated catch block

		}

		return pb;
	}

	public ArrayList<Node> getComapnyList(String searchText) {
		ArrayList<Node> pb = null;

		Node tempPrdNode, childNode;
		pb = new ArrayList<Node>();
		try {
			session = repo.login(new SimpleCredentials("admin", "admin"
					.toCharArray()));

			if (searchText!=null && !searchText.trim().equals("")) {

				//String querryStr = "select * from [nt:unstructured] where  ISDESCENDANTNODE('/content/company/') and companyName   (contains('companyName','*"+searchText+"*')";
				String querryStr = "select [companyName] from [nt:base] where (contains('companyName','*"+searchText +"*'))  and ISDESCENDANTNODE('/content/company/')";
				//response.getOutputStream().println("sql query---  "+querryStr);
				Workspace workspace = session.getWorkspace();
				Query query = workspace.getQueryManager().createQuery(
						querryStr, Query.JCR_SQL2);
				QueryResult result = query.execute();
				NodeIterator iterator = result.getNodes();

				while (iterator.hasNext()) {

					tempPrdNode = iterator.nextNode();
					pb.add(tempPrdNode.getParent());
					

				}
			} else {
				NodeIterator iterator = session.getRootNode()
						.getNode("content").getNode("company").getNodes();
				while (iterator.hasNext()) {

					tempPrdNode = iterator.nextNode();
					pb.add(tempPrdNode.getParent());
					

				}
			}

		} catch (LoginException e) {

		} catch (RepositoryException e) {
			// TODO Auto-generated catch block

		} catch (Exception e) {
			// TODO Auto-generated catch block

		}

		return pb;
	}

	public ArrayList<Node> searchGroup(String keyword) {
		ArrayList<Node> groupList = new ArrayList<Node>();
		Node tempPrdNode, childNode;

		try {
			Session session = repo.login(new SimpleCredentials("admin", "admin"
					.toCharArray()));
			if (keyword!=null && !keyword.equals("")) {

				Workspace workspace = session.getWorkspace();

				Query query = workspace.getQueryManager().createQuery(

						"select * from [nt:unstructured] where  (contains('groupName', '*"
								+ keyword + "*')) "
								+ "and ISDESCENDANTNODE('/content/group')",
						Query.JCR_SQL2);

				QueryResult qr = query.execute();

				NodeIterator iterator = qr.getNodes();

				while (iterator.hasNext()) {

					tempPrdNode = iterator.nextNode();
					groupList.add(tempPrdNode);
					

				}
			} else {

				NodeIterator iterator = session.getRootNode()
						.getNode("content").getNode("group").getNodes();
				while (iterator.hasNext()) {
					tempPrdNode = iterator.nextNode();
					groupList.add(tempPrdNode);
					

				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		return groupList;
	}

}