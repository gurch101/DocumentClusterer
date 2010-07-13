import java.io.Serializable;

public class Document implements Serializable{

	private static final long serialVersionUID = 431266082485097493L;
	private String title;
	private String description;
	
	public Document(String title, String description){
		this.title = title;
		this.description = description;
	}
	
	public String getTitle(){
		return title;
	}
	
	public String getContent(){
		return description;
	}
	
	@Override
	public String toString(){
		return "title: "+title+"\ndescription: "+description+"\n";
	}
	
	@Override
	public boolean equals(Object o){
		 if ( this == o ) return true;
		 if ( !(o instanceof Document) ) return false;
		 Document item = (Document)o;
		 return item.getTitle().equals(title)&&
		 item.getContent().equals(description);
	}
	
	@Override
	public int hashCode(){
		return (title.hashCode()*7 + description.hashCode())*21;
	}
}
