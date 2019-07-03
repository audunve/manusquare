package ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import graph.Graph;
import query.ConsumerQuery;
import similarity.SimilarityMeasures;
import similarity.SimilarityMethods;
import supplierdata.Resource;
import supplierdata.Supplier;
import supplierdata.SupplierResourceRecord;
import utilities.MathUtils;
import utilities.StringUtilities;

/**
 * Test class for semantic matching algorithm
 * Note that the query is fixed (but from an initial randomized query) and that the supplier resources are read from a csv file (where facets are also randomly created). 
 * @author audunvennesland
 *
 */
public class SemanticMatching {

	static File ontologyFile = new File ("./files/manusquare-consumer.owl");
	static SimilarityMethods similarityMethod = SimilarityMethods.RESNIK;
	static String csv = "./files/SET_Output_Generated_900records_TestDataFrom971Suppliers.csv";
	static String results_output = "./files/EVALUATION/V3/Results_" + similarityMethod + "_.txt";
	static Label label;

	public static void main(String[] args) throws IOException, OWLOntologyCreationException, ParseException {

		Set<SupplierResourceRecord> records = createSupplierResourceRecords(csv);
		Set<Supplier> suppliers = createSupplier(records);

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology onto = manager.loadOntologyFromOntologyDocument(ontologyFile);

		Label label = DynamicLabel.label(StringUtilities.stripPath(ontologyFile.toString()));

		//creates a new Neo4J db and a new ontology graph
		Graph.createOntologyGraph(ontologyFile);

		//dummy consumer query containing a combination of two sub-queries
		Set<ConsumerQuery> querySet = new HashSet<ConsumerQuery>();

		/* NOTE: THESE ARE TWO QUERIES GENERATED FROM THE "QUERY GENERATOR" WITH THE EXCEPTION OF THE CERTIFICATES WHICH HAVE BEEN ADDED AFTERWARDS */
		ConsumerQuery query1 = new ConsumerQuery();
		query1.setRequiredProcess("WaterJetCutting");
		query1.setRequiredMaterial("AlloySteel");
		query1.setRequiredMachine("WaterJetCuttingMachine");
		Set<String> query1Certificates = new HashSet<String>();
		query1Certificates.add("ISO9000");
		query1Certificates.add("ISO9004");
		query1.setRequiredCertificates(query1Certificates);
		query1.setQuantity(125);
		query1.setRequiredAvailableFromDate("2019-02-01");
		query1.setRequiredAvailableToDate("2019-04-15");

		ConsumerQuery query2 = new ConsumerQuery();
		query2.setRequiredProcess("PrecisionMilling");
		query2.setRequiredMaterial("CarbonSteel");
		query2.setRequiredMachine("ComputerControlledMachineTool");
		Set<String> query2Certificates = new HashSet<String>();
		query2Certificates.add("ISO9000");
		query2Certificates.add("LEED");
		query2Certificates.add("ISO9004");
		query2.setRequiredCertificates(query2Certificates);
		query2.setQuantity(50);
		query2.setRequiredAvailableFromDate("2019-03-01");
		query2.setRequiredAvailableToDate("2019-05-15");

		querySet.add(query1);
		querySet.add(query2);

		double semanticSim = 0;

		Map<Supplier, Double> supplierScores = new HashMap<Supplier, Double>();

		//match each resource offered by a supplier to each query in the querySet. For each supplier assign the highest score from the resource-to-query matching.
		for (Supplier supplier : suppliers) {
			
			LinkedList<Double> localSupplierScores = new LinkedList<Double>();
			for (ConsumerQuery query : querySet) {
				
				for (Resource resource : supplier.getResources()) {

					semanticSim = SimilarityMeasures.computeSemanticSimilarity(query, resource, label, onto, similarityMethod);
					localSupplierScores.add(semanticSim);
				}
				
			}
			
			supplierScores.put(supplier, getHighestScores(localSupplierScores, 1).get(0));

		}
		
		Map<Supplier, Double> rankedResults = sortDescending(supplierScores);
		
		File results = new File(results_output);
		PrintWriter writer = new PrintWriter(results);
		
		writer.println("Consumer query:");
		for (ConsumerQuery query : querySet) {
			writer.println("Process: " + query.getRequiredProcess() + ", Material: " + query.getRequiredMaterial() + ", Machine: " + query.getRequiredMachine() + ", Required Capacity: " + query.getCapacity() + ", Required Certifications(s): " + query.getRequiredCertificates() + ", From-to dates: " + query.getRequiredAvailableFromDate() + " - " + query.getRequiredAvailableToDate());
		}
		
		writer.println("\nRanked results from semantic matching");
		int rank = 0;
		for (Entry<Supplier, Double> e : rankedResults.entrySet()) {
			rank++;
			
			writer.println("\n" + rank + "; Supplier name: " + e.getKey().getSupplierName() + "; Nation: " + e.getKey().getSupplierNationality() + "; City: " + e.getKey().getSupplierCity() + "; Sim score: " + "(" + MathUtils.round(e.getValue(),4) + ")");
			Set<Resource> resources = e.getKey().getResources();
			for (Resource res : resources) {
				writer.println("* ; Process: " + res.getProcess() + "; Material: " + res.getMaterial() + "; Machine: " + res.getMachine() + "; Capacity: " + res.getCapacity() + "; Certifications: " + printCertifications(res.getCertifications()) + "; From date: " + res.getAvailableFrom() + "; To date: " + res.getAvailableTo());
			}
		}		
		
		writer.flush();  
		writer.close();  

	}


	public static Set<Supplier> createSupplier(Set<SupplierResourceRecord> records) throws FileNotFoundException {

		Set<String> ids = new HashSet<String>();

		//get all unique ids
		for (SupplierResourceRecord rec : records) {
			ids.add(rec.getId());
		}

		//create a set of suppliers
		Set<Supplier> suppliers = new HashSet<Supplier>();
		for (String id : ids) {
			Set<Resource> resources = new HashSet<Resource>();
			String supplierName = null;
			String supplierNation = null;
			String supplierCity = null;
			for (SupplierResourceRecord rec : records) {
				if (rec.getId().equals(id)) {
					resources.add(new Resource(rec.getCapacity(), rec.getUsedMaterial(), rec.getUsedProcess(), rec.getUsedMachine(), rec.getPosessedCertificates(), rec.getAvailableFrom(), rec.getAvailableTo()));
					supplierName = rec.getSupplierName();
					supplierNation = rec.getNation();
					supplierCity = rec.getCity();
				}
			}
			suppliers.add(new Supplier(id, supplierName, supplierNation, supplierCity, resources));
		}


		return suppliers;

	}



	public static Set<SupplierResourceRecord> createSupplierResourceRecords(String csvFilePath) throws IOException {

		Set<SupplierResourceRecord> resources = new HashSet<SupplierResourceRecord>();

		SupplierResourceRecord resource;

		BufferedReader br = new BufferedReader(new FileReader(csvFilePath));

		String line = br.readLine();

		String[] params = null;

		while (line != null) {
			params = line.split(";");

			resource = new SupplierResourceRecord();

			resource.setId(params[0]);
			resource.setSupplierName(params[1]);
			resource.setCity(params[2]);
			resource.setNation(params[3]);
			resource.setPromisedRFQResponseTime(Integer.parseInt(params[4]));
			resource.setCapacity(Integer.parseInt(params[5]));

			String[] certificates = params[6].split(",");
			Set<String> certificatesList = new HashSet<String>();
			for (String s : certificates) {
				certificatesList.add(s);
			}

			resource.setPosessedCertificates(certificatesList);
			resource.setUsedMaterial(params[7]);
			resource.setUsedProcess(params[8]);
			resource.setUsedMachine(params[9]);
			resource.setAvailableFrom(params[10]);
			resource.setAvailableTo(params[11]);

			resources.add(resource);
			line = br.readLine();

		}

		br.close();

		return resources;

	}

	public static String printCertifications(Set<String> certifications) {
		StringBuffer sb = new StringBuffer();
		for (String s : certifications) {
			sb.append(s + ",");
		}

		String certificationsString = sb.deleteCharAt(sb.lastIndexOf(",")).toString();

		return certificationsString;

	}

	public static LinkedList<Double> getHighestScores (LinkedList<Double> inputScores, int n) {

		LinkedList<Double> highestScores = new LinkedList<Double>();

		Collections.sort(inputScores, Collections.reverseOrder());

		for (int i = 0; i < n; i++) {
			highestScores.add(inputScores.get(i));
		}

		return highestScores;

	}
	
	/** 
	 * Sorts a map based on similarity scores (values in the map)
	 * @param map
	 * @return
	   May 16, 2019
	 */
	private static <K, V extends Comparable<V>> Map<K, V> sortDescending(final Map<K, V> map) {
		Comparator<K> valueComparator =  new Comparator<K>() {
			public int compare(K k1, K k2) {
				int compare = map.get(k2).compareTo(map.get(k1));
				if (compare == 0) return 1;
				else return compare;
			}
		};
		Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
			
		sortedByValues.putAll(map);

		//TODO: include only the n top items

		return sortedByValues;
	}

}
