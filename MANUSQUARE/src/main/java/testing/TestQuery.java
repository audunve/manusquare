package testing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.semanticweb.owl.align.Cell;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import graph.Graph;
import importtestdata.CSV2Set;
import similarity.SimilarityMeasures;
import utilities.StringUtilities;

public class TestQuery {

	static File ontologyFile = new File ("./files/manusquare-consumer.owl");
	static String similarityMethod = "WuPalmer";
	static String csv = "./files/CSV2Set3.csv";

	//test method
	public static void main(String[] args) throws OWLOntologyCreationException, IOException {

		//import and parse the owl file (throws Exception)		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology sourceOnto = manager.loadOntologyFromOntologyDocument(ontologyFile);

		Label label = DynamicLabel.label(StringUtilities.stripPath(ontologyFile.toString()));

		//creates a new Neo4J db and a new ontology graph
		Graph.createOntologyGraph(ontologyFile);

		//dummy consumer query
		ConsumerQuery query = new ConsumerQuery();
		query.setRequiredProcess("GasTungstenArcWelding");
		query.setRequiredMaterial("AluminiumAlloy");
		query.setRequiredMachine("WeldingMachine");
		query.setRequiredCertificates("ISO9001");
		query.setQuantity(80);

		//dummy supplier resources from testdata from Hans de Man		
		Set<SupplierResource> resources = CSV2Set.createSupplierResourceRecords(csv);

		double semanticSim = 0;

		Map<SupplierResource, Double> ranking = new HashMap<SupplierResource, Double>();

		for (SupplierResource resource : resources) {

			semanticSim = SimilarityMeasures.computeSemanticSimilarity(query, resource, label, sourceOnto, similarityMethod);
			//System.out.println("\nThe similarity between the consumer query and the supplier resource using Wu-Palmer is " + semanticSim);	
			
			ranking.put(resource, semanticSim);
		}
		
		//sort
		//Map<Cell, Double> sortedAlignmentMap = sortByValues(alignmentMap);
		Map<SupplierResource, Double> sortedSuppliers = sortByValues(ranking);
		
		System.out.println("Ranked list of supplier given the consumer query: ");
		System.out.println("\n");
		
		for (Entry<SupplierResource, Double> e : sortedSuppliers.entrySet()) {
			System.out.println("Supplier-Id:" + e.getKey().getId() + " (" + e.getKey().getSupplierName() + ") :" + e.getValue());
		}
		
	}
	

	public static <K, V extends Comparable<V>> Map<K, V> sortByValues(final Map<K, V> map) {
	    Comparator<K> valueComparator =  new Comparator<K>() {
	        public int compare(K k1, K k2) {
	            int compare = map.get(k2).compareTo(map.get(k1));
	            if (compare == 0) return 1;
	            else return compare;
	        }
	    };
	    Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
	    sortedByValues.putAll(map);
	    return sortedByValues;
	}
	
}
