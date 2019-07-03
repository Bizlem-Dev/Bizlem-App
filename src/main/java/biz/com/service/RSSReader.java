package biz.com.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public interface RSSReader {

    public ArrayList writeFeed1(HttpServletRequest request, HttpServletResponse response, ArrayList al)throws IOException, ParserConfigurationException, SAXException ; 
    public String getValue(Element parent, String nodeName);
    public String getValueMedia(int i, String nodeName,Document doc); 
    public String removeTags(String string);
    public void setTitlestyle(String str);
    public void setDescstyle(String str);
    public void setDateStyle(String str);
    public void setHeaderstyle(String str);
    public void setTextorthum(String str);
    public void setURL(URL url);


}
