import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Parses feed items that have a title, link, and description
 * @author Gurch
 *
 */
public class FeedParser {
	private static final int DEFAULT_ENTRIES_PER_FEED = 8;
	private static final String FEED_TABLE = "rss.multi.list";
	private static final String XPATH_ITEM = "//item";
	private static final String XPATH_FEED = "//feed";
	private YQLClient client;
	
	public FeedParser(){
		client = new YQLClient();
	}
	
	public List<RSSItem> getItems(Collection<String> feedUrls) throws Exception{
		return getItemBatch(feedUrls);
	}
	
	
	private List<RSSItem> getItemBatch(Collection<String> feedUrls) throws Exception{
		List<RSSItem> feeds = new ArrayList<RSSItem>(feedUrls.size()*DEFAULT_ENTRIES_PER_FEED);
		String query = buildQuery(feedUrls);
		try {
			Document results = client.execute(query);
			NodeList items = getItems(results);
			for(int i = 0; i < items.getLength(); i++){
				Node itemNode = items.item(i);
				Node parent = itemNode.getParentNode();
				String title = StringUtils.trim(parent.getFirstChild().getTextContent());
				RSSItem item = getItem(items.item(i));
				if(item != null){
					item.setSource(title);
					feeds.add(item);
				}
			}
		} catch (YQLException e) {
			e.printStackTrace();
		}
		return feeds;
	}
	
	private NodeList getItems(Document doc) throws Exception{
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression expr = xpath.compile(XPATH_ITEM);
		return (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
	}
	
	private RSSItem getItem(Node item){
		NodeList children = item.getChildNodes();
		if(children.getLength() >= 3){
			String title = null;
			String url = null;
			String desc = null;
			for(int i = 0; i < children.getLength(); i++){
				String nodeName = children.item(i).getNodeName();
				String nodeValue = children.item(i).getTextContent();
				if(nodeName == null)
					continue;
				if(nodeValue == null || nodeValue.equals(" ")){
					continue;
				}
				if(nodeName.equals("title")){
					title = nodeValue;
				}
				else if(nodeName.equals("link")){
					url = nodeValue;
				}
				else if(nodeName.equals("description")){
					desc = nodeValue;
				}
			}

			if(title == null||url == null||desc == null)
				return null;
			
			Html2Text html = new Html2Text();
			try{
				html.parse(new StringReader(desc));
				desc = html.getText();
				html.parse(new StringReader(title));
				title = html.getText();
			}catch(Exception e){ 
				return null;
			}
			return new RSSItem(title, desc, url);
		}
		return null;
	}
	
	private String buildQuery(Collection<String> FeedUrls){
		String urls = StringUtils.join(FeedUrls, "\',\'");
		urls = String.format("\'%s\'", urls);
		return "select * from "+FEED_TABLE+" where feeds=\""+urls+"\"";
	}
	
	private final class Html2Text extends HTMLEditorKit.ParserCallback {
		 StringBuffer s;

		 public Html2Text() {}

		 public void parse(Reader in) throws IOException {
		   s = new StringBuffer();
		   ParserDelegator delegator = new ParserDelegator();
		   // the third parameter is TRUE to ignore charset directive
		   delegator.parse(in, this, Boolean.TRUE);
		 }

		 public void handleText(char[] text, int pos) {
		   s.append(text);
		 }

		 public String getText() {
		   return s.toString();
		 }
	}
}
