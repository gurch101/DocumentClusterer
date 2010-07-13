import java.util.*;


public class DocumentCluster {
	private ArrayList<Document> docs;
	private Map<WordStem,Double> stemCounts; 
	
	public DocumentCluster(Document doc, Map<WordStem,Double> counts){
		docs = new ArrayList<Document>();
		docs.add(doc);
		stemCounts = counts;
	}
	
	public void merge(DocumentCluster cluster){
		docs.addAll(cluster.getDocuments());
		Map<WordStem,Double> counts = cluster.getCounts();
		for(WordStem stem : counts.keySet()){
			Double count = stemCounts.get(stem);
			if(count != null){
				stemCounts.put(stem, count+counts.get(stem));
			}
			else{
				WordStem s = getLeastOccuringStem();
				if(counts.get(stem) > stemCounts.get(s)){
					stemCounts.put(stem, counts.get(stem));
					stemCounts.remove(s);
				}
			}
		}
	}
	
	private WordStem getLeastOccuringStem(){
		WordStem stem = stemCounts.keySet().iterator().next();
		double min = stemCounts.get(stem);
		for(Map.Entry<WordStem, Double> entry : stemCounts.entrySet()){
			if(entry.getValue() < min){
				min = entry.getValue();
				stem = entry.getKey();
			}
		}
		return stem;
	}
	
	public List<Document> getDocuments(){
		return Collections.unmodifiableList(docs);
	}
	
	public Map<WordStem,Double> getCounts(){
		return Collections.unmodifiableMap(stemCounts);
	}
	
	public Set<WordStem> getTopWords(){
		return Collections.unmodifiableSet(stemCounts.keySet());
	}	
}
