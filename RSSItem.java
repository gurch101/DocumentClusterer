import java.io.Serializable;

public class RSSItem extends Document implements Serializable{
	private static final long serialVersionUID = 4419209933563095607L;
	private String url;
	private String src;
	
	public RSSItem(String title, String description, String url){
		super(title, description);
		this.src = "";
		this.url = url;
	}
	
	public RSSItem(String src, String title, String description, String url){
		super(title, description);
		this.src = src;
		this.url = url;
	}
	
	public String getUrl(){
		return url;
	}
	
	public void setSource(String src){
		this.src = src;
	}
	
	public String getSource(){
		return src;
	}
	
	@Override
	public String toString(){
		return super.toString()+"url: "+url;
	}
	
	@Override
	public boolean equals(Object o){
		 if ( this == o ) return true;
		 if ( !(o instanceof RSSItem) ) return false;
		 RSSItem item = (RSSItem)o;
		 return super.equals(item) &&
		 item.getUrl().equals(url);
	}
	
	@Override
	public int hashCode(){
		return super.hashCode() + url.hashCode();
	}
}
