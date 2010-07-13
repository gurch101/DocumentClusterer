
public class WordStem {
	private String word;
	private String stem;
	
	public WordStem(String word, String stem){
		this.word = word;
		this.stem = stem;
	}
	
	public String getWord(){
		return word;
	}
	
	public String getStem(){
		return stem;
	}
}
