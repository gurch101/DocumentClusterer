
import java.util.*;


public class DocumentStemCountBuilder {
	private Stemmer stemmer;
	private Set<String> stopWords;
	private static double TITLE_MULTIPLIER = 1.2;
	private static int NUM_ENTRIES = 20;
	
	public DocumentStemCountBuilder(Set<String> stopWords){
		stemmer = new Stemmer();
		this.stopWords = stopWords;
	}
	
	public Map<WordStem,Double> getFrequencies(Document doc){
		List<WordStem> title = getStems(doc.getTitle());
		List<WordStem> description = getStems(doc.getContent());
		double docLength = title.size() + description.size();
		
		HashMap<WordStem, Double> docCounts = new HashMap<WordStem, Double>();
		addStemCounts(docCounts, title, TITLE_MULTIPLIER);
		addStemCounts(docCounts, description, 1.0);
		
		Map<WordStem, Double> lnDocCounts = new HashMap<WordStem, Double>();
		for(Map.Entry<WordStem, Double> entry : docCounts.entrySet()){
			double lengthNormalizedCount = entry.getValue()/docLength;
			WordStem stem = entry.getKey();
			lnDocCounts.put(stem, lengthNormalizedCount);
		}
		
		return getTopFrequencies(lnDocCounts);
	}
	
	private void addStemCounts(HashMap<WordStem,Double> docCounts, List<WordStem> words, double multiplier){
		for(WordStem stem : words){
			Double ct = docCounts.get(stem);
			if(ct != null){
				docCounts.put(stem, ct+multiplier);
			}
			else{
				docCounts.put(stem, multiplier);
			}
		}
	}
	
	private <K,V extends Comparable<? super V>> Map<K,V> getTopFrequencies(Map<K,V> map) {
	     List<Map.Entry<K, V>> list = new ArrayList<Map.Entry<K,V>>(map.entrySet());
	     Collections.sort(list, new ValueComparator<V>());
	     Map<K,V> result = new LinkedHashMap<K,V>();
	     Iterator<Map.Entry<K, V>> it = list.iterator();
	     while(it.hasNext() && result.size() < NUM_ENTRIES){
	    	 Map.Entry<K, V> entry = it.next();
	    	 result.put(entry.getKey(), entry.getValue());
	     }

		return result;
	}
	
	private List<WordStem> getStems(String str){
		str = str.toLowerCase();
		// remove punctuation
		str = str.replaceAll("\\p{P}+", "");
		String[] words = str.split(" ");
		List<WordStem> wordStems = new ArrayList<WordStem>();
		for(int i = 0; i < words.length; i++){
			String word = words[i];
			String stem = stemmer.stem(words[i]);
			
			if(!stopWords.contains(words[i]) && !words[i].equals(" ") && words[i].length()>=1){
				wordStems.add(new WordStem(word,stem));
			}
		}
		return wordStems;
	}
	
	private static final class ValueComparator<V extends Comparable<? super V>>
    implements Comparator<Map.Entry<?, V>> {
		public int compare(Map.Entry<?, V> o1, Map.Entry<?, V> o2) {
		return o2.getValue().compareTo(o1.getValue());
	}
}
	
}
