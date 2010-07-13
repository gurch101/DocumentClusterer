import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;


public class ClusterAlg {
	private final static int MIN_WORDS = 10;
	private final static int MIN_WORDS_MATCH = 5;
	
	public static void main(String[] args) throws Exception{		
		Collection<String> feedUrls = readFileToCollection("feeds.txt");
		
		List<String> stopList = new ArrayList<String>(readFileToCollection("stopList.txt"));
		Set<String> stopWords = new HashSet<String>();
		Stemmer stemmer = new Stemmer();
		for(String word : stopList){
			stopWords.add(stemmer.stem(word));
		}
		
		FeedParser parser = new FeedParser();
		List<RSSItem> items = parser.getItems(feedUrls);
		
		DocumentStemCountBuilder stemBuilder = new DocumentStemCountBuilder(stopWords);
		ArrayList<DocumentCluster> clusters = new ArrayList<DocumentCluster>();

		HashSet<String> titles = new HashSet<String>();
		for(RSSItem item : items){
			if(item.getContent().split(" ").length > MIN_WORDS && !titles.contains(item.getTitle())){
				Map<WordStem,Double> counts = stemBuilder.getFrequencies(item);
				DocumentCluster cluster = new DocumentCluster(item,counts);
				clusters.add(cluster);
				titles.add(item.getTitle());
			}
		}
		List<DocumentCluster> docs = cluster(clusters);
		
		Collections.sort(docs, new Comparator<DocumentCluster>(){
			@Override
			public int compare(DocumentCluster o1, DocumentCluster o2) {
				return o2.getDocuments().size() - o1.getDocuments().size();
			}	
		});
		writeClusters(docs);
	}
	
	
	public static List<DocumentCluster> cluster(List<DocumentCluster> clusters){
		return cluster(clusters, MIN_WORDS_MATCH, 5);
	}
	
	private static List<DocumentCluster> cluster(List<DocumentCluster> clusters, int minMatch, int numIterations){
		if(numIterations < 0){
			return clusters;
		}
		HashSet<Integer> isClustered = new HashSet<Integer>();
		for(int i = 0; i < clusters.size(); i++){
			if(!isClustered.contains(i)){
				DocumentCluster c1 = clusters.get(i);
				for(int j = i + 1; j < clusters.size(); j++){
					if(!isClustered.contains(j)){
						DocumentCluster c2 = clusters.get(j);
						Set<WordStem> topWords2 = new HashSet<WordStem>(c2.getTopWords());
						Set<WordStem> topWords1 = new HashSet<WordStem>(c1.getTopWords());
						topWords1.retainAll(topWords2);
						if(topWords1.size() > minMatch){
							c1.merge(c2);
							isClustered.add(j);
						}
					}
				}
			}
		}
		
		ArrayList<DocumentCluster> clusterList = new ArrayList<DocumentCluster>();
		for(int i = 0; i < clusters.size(); i++){
			if(!isClustered.contains(i))
				clusterList.add(clusters.get(i));
		}
		if(isClustered.size() == 0){
			return clusterList;
		}
		return cluster(clusterList, minMatch, numIterations -1);
	}
	
	public static void writeClusters(List<DocumentCluster> clusters) throws IOException{
		BufferedWriter out = new BufferedWriter(new FileWriter("out.txt"));
		for(int i = 0; i < clusters.size(); i++){
			out.write("cluster "+i+"\n");
			List<Document> items = clusters.get(i).getDocuments();
			int entryNum = 1;
			for(Document item : items){
				out.write(entryNum+"\n");
				out.write(item+"\n");
				entryNum++;
			}
			out.write("\n");
		}
		out.close();
	}
	
	public static Collection<String> readFileToCollection(String filename) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(filename));
		String line = null;
		List<String> lines = new ArrayList<String>();
		while((line = in.readLine()) != null){
			lines.add(line);
		}
		return lines;
	}
}
