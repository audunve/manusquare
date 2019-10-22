package ui.mvp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.codehaus.jettison.json.JSONException;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import graph.Graph;
import query.ConsumerQuery;
import similarity.MatchingResult;
import similarity.SimilarityMeasures;
import similarity.SimilarityMethods;
import supplierdata.Resource;
import supplierdata.Supplier;
import supplierdata.SupplierResourceRecord;
import utilities.StringUtilities;

/**
 * Test class for semantic matching algorithm
 * Note that the query is fixed (but from an initial randomized query) and that the supplier resources are read from a csv file (where facets are also randomly created). 
 * @author audunvennesland
 *
 */
public class SemanticMatching_MVP {


	static SimilarityMethods similarityMethod = SimilarityMethods.WU_PALMER;

	//configure the GraphDB knowledge base
	//	static final String GRAPHDB_SERVER = "http://localhost:7200/";
	//	static final String REPOSITORY_ID = "Manusquare";

	//configuration of the MANUSQUARE Semantic Infrastructure	
	static final String SPARQL_ENDPOINT = "http://194.183.12.36:8181/semantic-registry/repository/manusquare?infer=true&limit=0&offset=0";
	static final String AUTHORISATION_TOKEN = "c5ec0a8b494a30ed41d4d6fe3107990b";

	static Label label;

	//ontology used for computing semantic similarity, should eventually point to an URI of a persistent MANUSQUARE ontology.
	static File ontologyFile = new File ("./files/ONTOLOGIES/manusquare-consumer.owl");


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
	public static void performSemanticMatching(String inputJSONFile, int numResults, String outputJSONFile, boolean weighted) throws IOException, OWLOntologyCreationException, ParseException, JSONException {

		//create query (set) from input JSON file
		Set<ConsumerQuery> consumerQueries = json.parseJSONInput.createConsumerQuery(inputJSONFile);

		//get process(s) from the query and use them to subset the supplier records in the SPARQL query
		List<String> processes = new ArrayList<String>();
		for (ConsumerQuery query : consumerQueries) {
			processes.add(query.getRequiredProcess());
		}

		Set<SupplierResourceRecord> recordsFromKB = createSupplierResourceRecordsFromKB(processes);
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


		//TODO: Check if a more sensible approach for computing an aggregate score for each supplier makes sense. At the moment only the best matching resource offered
		//by a supplier is used to represent a supplier's score. The rationale is that a supplier can offer a wide range of resources, some relevant and some not relevant, and
		//the non-relevant ones should not penalize the relevant resources a supplier offers.
		//match each resource offered by a supplier to each query in the querySet. For each supplier assign the highest score from the resource-to-query matching.
		for (Supplier supplier : suppliers) {

			LinkedList<Double> localSupplierScores = new LinkedList<Double>();

			for (ConsumerQuery query : consumerQueries) {

				for (Resource resource : supplier.getResources()) {
					semanticSim = SimilarityMeasures.computeSemanticSimilarity(query, resource, label, onto, similarityMethod, weighted);
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

		int rank = 1;
		List<MatchingResult> scores = new LinkedList<MatchingResult>();

		for (Entry<String, Double> e : finalSupplierMap.entrySet()) {
			scores.add(new MatchingResult(rank, e.getKey(), e.getValue()));
			rank++;
		}

		Gson gson = new GsonBuilder().create();

		String json = gson.toJson(scores);

		System.out.println(json);

		FileWriter writer = new FileWriter(outputJSONFile);
		writer.write(json);
		writer.close();

	}

	/**
	 * creates supplier resource records from knowledge base
	 * @return set of supplier resource records 
	 * @throws IOException
	   Oct 12, 2019
	 */
	private static Set<SupplierResourceRecord> createSupplierResourceRecordsFromKB(List<String> processes) throws IOException {

		Set<SupplierResourceRecord> resources = new HashSet<SupplierResourceRecord>();

		SupplierResourceRecord resource;

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", AUTHORISATION_TOKEN);
		headers.put("accept", "application/JSON");

		//connect to GraphDB
		//Repository repository = new HTTPRepository(kb, repositoryID);

		SPARQLRepository repository = new SPARQLRepository(SPARQL_ENDPOINT );

		repository.initialize();
		repository.setAdditionalHttpHeaders(headers);

		String strQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n";
		strQuery += "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n";
		strQuery += "PREFIX core: <http://manusquare.project.eu/core-manusquare#> \n";
		strQuery += "PREFIX ind: <http://manusquare.project.eu/industrial-manusquare#> \n";
		strQuery += "PREFIX owl: <http://www.w3.org/2002/07/owl#> \n";
		strQuery += "SELECT distinct ?processChain ?supplierId ?processType ?certificationType \n";
		strQuery += "WHERE { \n";

		//only retrieve the subsumed process classes of processes included in the consumer query
		strQuery += querySubsumedClasses(processes);

		strQuery += "?subprocess rdf:type ?processType .\n";
		strQuery += "?processChain core:hasProcess ?subprocess .\n";
		strQuery += "?processChain core:hasSupplier ?supplier .\n";	
		strQuery += "?supplier core:hasId ?supplierId .\n";
		strQuery += "?supplier core:hasCertification ?certification . \n";
		strQuery += "?certification rdf:type ?certificationType . \n";
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
					if (!solution.getValue("processType").stringValue().equals("http://www.w3.org/2002/07/owl#NamedIndividual")) {

						resource = new SupplierResourceRecord();
						resource.setId(solution.getValue("processChain").stringValue());
						resource.setSupplierId(solution.getValue("supplierId").stringValue());
						resource.setUsedProcess(solution.getValue("processType").stringValue().replaceAll("http://manusquare.project.eu/industrial-manusquare#", ""));
						resource.setPosessedCertificate(solution.getValue("certificationType").stringValue().replaceAll("http://manusquare.project.eu/industrial-manusquare#", ""));

						resources.add(resource);
					}
				}

			}	

		}

		//close connection to KB repository
		repository.shutDown();

		//ensure no duplicate records
		Set<SupplierResourceRecord> cleanRecords = consolidateSupplierRecords(resources);

		return cleanRecords;

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
					resources.add(new Resource(rec.getUsedProcess(),rec.getPosessedCertificates()));
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
	 * Creates a UNION query (i.e. an OR) from processes included in a consumer query
	 * @param processes
	 * @return
	   Oct 22, 2019
	 */
	private static String querySubsumedClasses (List<String> processes) {

		List<String> query = new ArrayList<String>();

		if (processes.size() > 1) {

			for (String s : processes) {

				query.add("{ ?processType rdfs:subClassOf ind:" + s + " . }");
			}

			return String.join(" UNION ", query);

		} else {

			return "?processType rdfs:subClassOf ind:" + processes.get(0) + " .";
		}


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
