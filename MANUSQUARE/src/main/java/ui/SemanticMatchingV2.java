package ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import graph.Graph;
import query.ConsumerQuery;
import query.QueryGenerator;
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
public class SemanticMatchingV2 {


	static SimilarityMethods similarityMethod = SimilarityMethods.WU_PALMER;

	//configure the GraphDB knowledge base
	static final String GRAPHDB_SERVER = "http://localhost:7200/";
	static final String REPOSITORY_ID = "MANUSQUARE_TEST";

	static Label label;

	//ontology used for computing semantic similarity, should eventually point to an URI of a persistent MANUSQUARE ontology.
	static File ontologyFile = new File ("./files/ONTOLOGIES/manusquare-consumer.owl");


	//main method
	public static void main(String[] args) throws OWLOntologyCreationException, IOException, ParseException, JSONException {

		//to avoid all logger messages from logback
		Set<String> loggers = new HashSet<>(Arrays.asList("org.apache.http", "org.eclipse.rdf4j"));
		for(String log:loggers) { 
			Logger logger = (Logger)LoggerFactory.getLogger(log);
			logger.setLevel(Level.ERROR);
			logger.setAdditive(false);
		}
		
		//generate a random query to feed to the semantic matching algorithm
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology onto = manager.loadOntologyFromOntologyDocument(ontologyFile);
		ConsumerQuery query = QueryGenerator.generateSimpleQuery(onto);
		
		String jsonFile = "./files/GsonSerialization.json";

		//retrieve and print the results from the semantic matching
		System.out.println(performSemanticMatching(jsonFile, 15));
		

	}
	
	/**
	 * Matches a query against a set of resources offered by suppliers and returns a list of suppliers having the highest semantic similarity
	 * @param jsonFile an input json file holding process(es) and certifications from the RFQ creation process.
	 * @param numResults number of relevant suppliers to be returned from the matching
	 * @throws IOException
	 * @throws OWLOntologyCreationException
	 * @throws ParseException
	   Oct 12, 2019
	 * @throws JSONException 
	 */
	public static String performSemanticMatching(String jsonFile, int numResults) throws IOException, OWLOntologyCreationException, ParseException, JSONException {
		
		//create query (set)
		Set<ConsumerQuery> consumerQueries = json.parseJSONInput.createConsumerQuery(jsonFile);
		
		System.out.println("We have " + consumerQueries.size() + " consumer queries");

		Set<SupplierResourceRecord> recordsFromKB = createSupplierResourceRecordsFromKB(GRAPHDB_SERVER, REPOSITORY_ID);
		Set<Supplier> suppliers = new HashSet<Supplier>();

		suppliers = createSupplier(recordsFromKB);

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology onto = manager.loadOntologyFromOntologyDocument(ontologyFile);

		//TODO: see if the Neo4J should be replaced by OWL API ontology traversal
		//creates a new Neo4J db and a new ontology graph
		Graph.createOntologyGraph(ontologyFile);
		//used by Neo4J to distinguish a particular ontology graph
		Label label = DynamicLabel.label(StringUtilities.stripPath(ontologyFile.toString()));

		double semanticSim = 0;

		Map<Supplier, Double> supplierScores = new HashMap<Supplier, Double>();

		//match each resource offered by a supplier to each query in the querySet. For each supplier assign the highest score from the resource-to-query matching.
		for (Supplier supplier : suppliers) {

			LinkedList<Double> localSupplierScores = new LinkedList<Double>();
			
			for (ConsumerQuery query : consumerQueries) {

			for (Resource resource : supplier.getResources()) {
				semanticSim = SimilarityMeasures.computeSemanticSimilarity(query, resource, label, onto, similarityMethod, false);
				localSupplierScores.add(semanticSim);
			}
			}
			
			//get the highest score for the resources offered by supplier n
			supplierScores.put(supplier, getHighestScores(localSupplierScores, 1).get(0));
		}

		//sort the results from highest to lowest score and return the x highest scores
		Map<Supplier, Double> rankedResults = sortDescending(supplierScores);
		Iterable<Entry<Supplier, Double>> firstEntries =
				Iterables.limit(rankedResults.entrySet(), numResults);
		
		//put the x highest scores in a (sorted) JSON array
		Map<String, Double> finalSupplierMap = new LinkedHashMap<String, Double>();		
		for (Entry<Supplier, Double> e : firstEntries) {
						
			finalSupplierMap.put(e.getKey().getId(), e.getValue());
			
		}

		JSONObject js = new JSONObject(finalSupplierMap);
		JSONArray joArray = new JSONArray();
		joArray.put(js);
				
		return joArray.toString(2);

	}
	
//	/**
//	 * Matches a query against a set of resources offered by suppliers and returns a list of suppliers having the highest semantic similarity
//	 * @param jsonFile an input json file holding process(es) and certifications from the RFQ creation process.
//	 * @param numResults number of relevant suppliers to be returned from the matching
//	 * @throws IOException
//	 * @throws OWLOntologyCreationException
//	 * @throws ParseException
//	   Oct 12, 2019
//	 * @throws JSONException 
//	 */
//	public static JSONArray performSemanticMatching(String jsonFile, int numResults, String output) throws IOException, OWLOntologyCreationException, ParseException, JSONException {
//		
//		//create query (set)
//		Set<ConsumerQuery> consumerQueries = json.parseJSONInput.createConsumerQuery(jsonFile);
//		
//		System.out.println("We have " + consumerQueries.size() + " consumer queries");
//
//		Set<SupplierResourceRecord> recordsFromKB = createSupplierResourceRecordsFromKB(GRAPHDB_SERVER, REPOSITORY_ID);
//		Set<Supplier> suppliers = new HashSet<Supplier>();
//
//		suppliers = createSupplier(recordsFromKB);
//
//		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
//		OWLOntology onto = manager.loadOntologyFromOntologyDocument(ontologyFile);
//
//		//TODO: see if the Neo4J should be replaced by OWL API ontology traversal
//		//creates a new Neo4J db and a new ontology graph
//		Graph.createOntologyGraph(ontologyFile);
//		//used by Neo4J to distinguish a particular ontology graph
//		Label label = DynamicLabel.label(StringUtilities.stripPath(ontologyFile.toString()));
//
//		double semanticSim = 0;
//
//		Map<Supplier, Double> supplierScores = new HashMap<Supplier, Double>();
//
//		//match each resource offered by a supplier to each query in the querySet. For each supplier assign the highest score from the resource-to-query matching.
//		for (Supplier supplier : suppliers) {
//
//			LinkedList<Double> localSupplierScores = new LinkedList<Double>();
//			
//			for (ConsumerQuery query : consumerQueries) {
//
//			for (Resource resource : supplier.getResources()) {
//				semanticSim = SimilarityMeasures.computeSemanticSimilarity(query, resource, label, onto, similarityMethod);
//				localSupplierScores.add(semanticSim);
//			}
//			}
//			
//			//get the highest score for the resources offered by supplier n
//			supplierScores.put(supplier, getHighestScores(localSupplierScores, 1).get(0));
//		}
//
//		//sort the results from highest to lowest score and return the x highest scores
//		Map<Supplier, Double> rankedResults = sortDescending(supplierScores);
//		Iterable<Entry<Supplier, Double>> firstEntries =
//				Iterables.limit(rankedResults.entrySet(), numResults);
//		
//		//put the x highest scores in a (sorted) JSON array
//		Map<String, Double> finalSupplierMap = new LinkedHashMap<String, Double>();		
//		for (Entry<Supplier, Double> e : firstEntries) {
//						
//			finalSupplierMap.put(e.getKey().getId(), e.getValue());
//			
//		}
//
//		JSONObject js = new JSONObject(finalSupplierMap);
//		JSONArray joArray = new JSONArray();
//		joArray.put(js);
//				
//		return joArray;
//
//	}

	/**
	 * NOTE: THIS IS USED FOR TESTING USING A DUMMY QUERY AGAINST A LOCAL GRAPHDB KB WITH DUMMY RESOURCES
	 * Matches a query against a set of resources offered by suppliers and returns a list of suppliers having the highest semantic similarity
	 * @param query specifies the parameters from the user searching for relevant suppliers
	 * @param numResults number of relevant suppliers to be returned from the matching
	 * @throws IOException
	 * @throws OWLOntologyCreationException
	 * @throws ParseException
	   Oct 12, 2019
	 * @throws JSONException 
	 */
	public static JSONArray performDummySemanticMatching(ConsumerQuery query, int numResults) throws IOException, OWLOntologyCreationException, ParseException, JSONException {

		Set<SupplierResourceRecord> recordsFromKB = createSupplierResourceRecordsFromKB(GRAPHDB_SERVER, REPOSITORY_ID);
		Set<Supplier> suppliers = new HashSet<Supplier>();

		suppliers = createSupplier(recordsFromKB);

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology onto = manager.loadOntologyFromOntologyDocument(ontologyFile);

		//TODO: see if the Neo4J should be replaced by OWL API ontology traversal
		//creates a new Neo4J db and a new ontology graph
		Graph.createOntologyGraph(ontologyFile);
		//used by Neo4J to distinguish a particular ontology graph
		Label label = DynamicLabel.label(StringUtilities.stripPath(ontologyFile.toString()));

		double semanticSim = 0;

		Map<Supplier, Double> supplierScores = new HashMap<Supplier, Double>();

		//match each resource offered by a supplier to each query in the querySet. For each supplier assign the highest score from the resource-to-query matching.
		for (Supplier supplier : suppliers) {

			LinkedList<Double> localSupplierScores = new LinkedList<Double>();

			for (Resource resource : supplier.getResources()) {
				semanticSim = SimilarityMeasures.computeSemanticSimilarity(query, resource, label, onto, similarityMethod, false);
				localSupplierScores.add(semanticSim);
			}
			//get the highest score for the resources offered by supplier n
			supplierScores.put(supplier, getHighestScores(localSupplierScores, 1).get(0));
		}

		//sort the results from highest to lowest score and return the x highest scores
		Map<Supplier, Double> rankedResults = sortDescending(supplierScores);
		Iterable<Entry<Supplier, Double>> firstEntries =
				Iterables.limit(rankedResults.entrySet(), numResults);
		
		//put the x highest scores in a (sorted) JSON array
		Map<String, Double> finalSupplierMap = new LinkedHashMap<String, Double>();		
		for (Entry<Supplier, Double> e : firstEntries) {
						
			finalSupplierMap.put(e.getKey().getId(), e.getValue());
			
		}

		JSONObject js = new JSONObject(finalSupplierMap);
		JSONArray joArray = new JSONArray();
		joArray.put(js);
				
		return joArray;

	}


	/**
	 * creates a set of suppliers (a supplier object contains both supplier data and a set of resources offered by this particular supplier)
	 * @param records a set of suppliers and their resources
	 * @return
	 * @throws FileNotFoundException
	   Oct 12, 2019
	 */
	private static Set<Supplier> createSupplier(Set<SupplierResourceRecord> records) throws FileNotFoundException {

		Set<String> ids = new HashSet<String>();

		//get all unique supplier id's
		for (SupplierResourceRecord rec : records) {
			ids.add(rec.getSupplierId());
		}

		//create a set of suppliers
		Set<Supplier> suppliers = new HashSet<Supplier>();
		for (String supplier_id : ids) {
			Set<Resource> resources = new HashSet<Resource>();
			String supplierName = null;
			String supplierNation = null;
			String supplierCity = null;
			for (SupplierResourceRecord rec : records) {
				if (rec.getSupplierId().equals(supplier_id)) {
					resources.add(new Resource(rec.getUsedMaterial(), rec.getUsedProcess(), rec.getUsedMachine(), rec.getPosessedCertificates()));
					supplierName = rec.getSupplierName();
					supplierNation = rec.getNation();
					supplierCity = rec.getCity();
				}
			}
			suppliers.add(new Supplier(supplier_id, supplierName, supplierNation, supplierCity, resources));
		}

		return suppliers;
	}


	/**
	 * creates supplier resource records from knowledge base
	 * @param kb path to the knowledge base
	 * @param repositoryID identifies the specific repository of the knowledge base
	 * @return
	 * @throws IOException
	   Oct 12, 2019
	 */
	private static Set<SupplierResourceRecord> createSupplierResourceRecordsFromKB(String kb, String repositoryID) throws IOException {

		Set<SupplierResourceRecord> resources = new HashSet<SupplierResourceRecord>();

		SupplierResourceRecord resource;

		//connect to GraphDB
		Repository repository = new HTTPRepository(kb, repositoryID);
		repository.initialize();

		//retrieve relevant supplier resources using a SPARQL query, note that for process, material and machine the rdf:types are retrieved, not the 
		//individuals.
		String strQuery = "PREFIX core:<http://manusquare.project.eu/core-manusquare#> \n";
		strQuery += "PREFIX ind:<http://manusquare.project.eu/industrial-manusquare#> \n";
		strQuery += "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n";
		strQuery += "SELECT distinct ?processChain ?supplierId ?supplierName ?supplierCity ?supplierNation ?certification ?processType ?materialType ?machineType \n";
		strQuery += "WHERE { \n";
		strQuery += "?processChain core:hasSupplier ?supplier .\n";	
		strQuery += "?supplier core:hasId ?supplierId .\n";
		strQuery += "?supplier core:hasName ?supplierName .\n";
		strQuery += "?supplier core:hasCity ?supplierCity .\n";
		strQuery += "?supplier core:hasNation ?supplierNation .\n";
		strQuery += "?supplier core:hasCertification ?certification . \n";
		strQuery += "?processChain core:hasProcess ?process .\n";
		strQuery += "?process rdf:type ?processType .\n";		
		strQuery += "?processChain core:hasInput ?material .\n";
		strQuery += "?material rdf:type ?materialType .\n";
		strQuery += "?processChain core:hasResource ?machine. \n";	
		strQuery += "?machine rdf:type ?machineType \n";

		strQuery += "}";


		//open connection to GraphDB and run SPARQL query
		try(RepositoryConnection conn = repository.getConnection()) {

			TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, strQuery);		
			
			//do not include inferred statements from the KB
			tupleQuery.setIncludeInferred(false);

			try (TupleQueryResult result = tupleQuery.evaluate()) {

				while (result.hasNext()) {

					BindingSet solution = result.next();
					
					//omit the NamedIndividual types from the query result
					if (!solution.getValue("processType").stringValue().equals("http://www.w3.org/2002/07/owl#NamedIndividual")
							&& !solution.getValue("materialType").stringValue().equals("http://www.w3.org/2002/07/owl#NamedIndividual")
							&& !solution.getValue("machineType").stringValue().equals("http://www.w3.org/2002/07/owl#NamedIndividual")) {

					resource = new SupplierResourceRecord();
					resource.setId(solution.getValue("processChain").stringValue());
					resource.setSupplierId(solution.getValue("supplierId").stringValue());
					//resource.setId(solution.getValue("processChain").stringValue());
					//resource.setSupplierName(solution.getValue("supplierName").stringValue());
					//resource.setCity(solution.getValue("supplierCity").stringValue());
					//resource.setNation(solution.getValue("supplierNation").stringValue());		
					resource.setUsedMaterial(solution.getValue("materialType").stringValue().replaceAll("http://manusquare.project.eu/industrial-manusquare#", ""));
					resource.setUsedProcess(solution.getValue("processType").stringValue().replaceAll("http://manusquare.project.eu/industrial-manusquare#", ""));
					resource.setUsedMachine(solution.getValue("machineType").stringValue().replaceAll("http://manusquare.project.eu/industrial-manusquare#", ""));
					resource.setPosessedCertificate(solution.getValue("certification").stringValue().replaceAll("http://manusquare.project.eu/industrial-manusquare#", "").substring(0, 
							solution.getValue("certification").stringValue().replaceAll("http://manusquare.project.eu/industrial-manusquare#", "").indexOf("_")));

					resources.add(resource);
					}
				}

			}	

		}

		//close connection to GraphDB
		repository.shutDown();

		//ensure no duplicate records
		Set<SupplierResourceRecord> cleanRecords = consolidateSupplierRecords(resources);

		return cleanRecords;

	}

	/**
	 * ensures that the certificates are properly associated with a supplier and that there are no duplicate process chains.
	 * @param inputSet set of 
	 * @return
	 * @throws FileNotFoundException
	   Oct 12, 2019
	 */
	private static Set<SupplierResourceRecord> consolidateSupplierRecords(Set<SupplierResourceRecord> inputSet) throws FileNotFoundException {

		//create a set of supplier resource record ids (process chain)
		Set<String> id_set = new HashSet<String>();
		for (SupplierResourceRecord sr : inputSet) {
			id_set.add(sr.getId());
		}

		//create a set of supplier names
		Set<String> supplierNames = new HashSet<String>();
		for (SupplierResourceRecord sr : inputSet) {
			supplierNames.add(sr.getSupplierName());
		}

		//associate certifications relevant for each supplier (name) and put these associations in a map ( supplier(1), certifications(*) )
		Map<String, Set<String>> certMap = new HashMap<String, Set<String>>();
		for (String id : id_set) {
			Set<String> certifications = new HashSet<String>();
			for (SupplierResourceRecord sr : inputSet) {

				if (sr.getId().equals(id)) {
					certifications.add(sr.getPosessedCertificate());					
				}				
			}			
			certMap.put(id, certifications);
		}

		//add the set of certifications to each supplier (name) resource
		for (SupplierResourceRecord sr : inputSet) {
			if (certMap.containsKey(sr.getId())) {
				sr.setPosessedCertificates(certMap.get(sr.getId()));
			}

		}

		//Ensure that each id (process chain) is included with only one entry in the inputSet (remove duplicates based on id).
		Set<SupplierResourceRecord> cleanIdSet = new HashSet<SupplierResourceRecord>();
		Map<String, SupplierResourceRecord> map = new HashMap<>();
		for (SupplierResourceRecord sr : inputSet) {
			map.put(sr.getId(), sr);
		}

		for (Entry<String, SupplierResourceRecord> e : map.entrySet()) {
			cleanIdSet.add(e.getValue());
		}

		return cleanIdSet;

	}

	/**
	 * prints a set of certifications as a sequenced string of certifications
	 * @param certifications
	 * @return sequenced string of certifications separated by commas
	   Oct 12, 2019
	 */
	private static String printCertifications(Set<String> certifications) {
		StringBuffer sb = new StringBuffer();
		for (String s : certifications) {
			sb.append(s + ",");
		}

		String certificationsString = sb.deleteCharAt(sb.lastIndexOf(",")).toString();

		return certificationsString;

	}

	/**
	 * Sorts the scores for each resource offered by a supplier (from highest to lowest)
	 * @param inputScores a list of scores for each supplier resource assigned by the semantic matching 
	 * @param n number of (highest) scores to return
	 * @return the n highest scores from a list of input scores
	   Oct 12, 2019
	 */
	private static LinkedList<Double> getHighestScores (LinkedList<Double> inputScores, int n) {

		LinkedList<Double> highestScores = new LinkedList<Double>();

		Collections.sort(inputScores, Collections.reverseOrder());

		for (int i = 0; i < n; i++) {
			highestScores.add(inputScores.get(i));
		}


		return highestScores;

	}

	/** 
	 * Sorts a map based on similarity scores (values in the map)
	 * @param map the input map to be sorted
	 * @return map with sorted values
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

		return sortedByValues;
	}

}
