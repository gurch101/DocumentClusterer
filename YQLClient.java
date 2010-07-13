import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


public class YQLClient {
	public final static String YQL_API_URL = "http://query.yahooapis.com/v1/public/yql?q=";
	public final static String TABLE_ENV = "env=http://datatables.org/alltables.env";
	private HttpClient client;
	
	public YQLClient(){
		client = new DefaultHttpClient();
	}
	
	public Document execute(String query) throws YQLException{
		try{
			String url = buildUrl(query);
			HttpGet get = new HttpGet(url);
			HttpResponse response = client.execute(get);
			return parseDocument(response);
		}catch(Exception ex){
			throw new YQLException(ex.getMessage());
		}
	}

	private Document parseDocument(HttpResponse response) throws IOException,
			SAXException, ParserConfigurationException,
			FactoryConfigurationError {
		HttpEntity entity = response.getEntity();
		Document doc = null;
		if(entity != null){
			InputStream instream = null;
			try{
				instream = entity.getContent();
				doc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().parse(instream);
			} finally{
				instream.close();
			}
		}
		return doc;
	}
	
	private String buildUrl(String query) throws UnsupportedEncodingException {
		String encQuery = URLEncoder.encode(query, "utf-8");
		return YQL_API_URL+encQuery+"&format=xml&"+TABLE_ENV;
	}
	
	/*
	public static void main(String[] args) throws Exception{
		YQLClient c = new YQLClient();
		Document doc = c.execute("select * from rss where url=\'http://english.aljazeera.net/Services/Rss/?PostingId=2007731105943979989\'");
	}
	*/
}
