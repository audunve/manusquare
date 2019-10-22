package testing;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import supplierdata.SupplierResourceRecord;

/**
 * Retrieves supplier resources from query controller service of the MANUSQUARE Semantic Infrastructure service.
 * @author audunvennesland
 *
 */
public class AccessSemanticInfrastructure {

	public static void main(String[] args) throws FileNotFoundException {

		//to avoid all logger messages from logback
		Set<String> loggers = new HashSet<>(Arrays.asList("org.apache.http", "org.eclipse.rdf4j"));

		for(String log:loggers) { 
			Logger logger = (Logger)LoggerFactory.getLogger(log);
			logger.setLevel(Level.ERROR);
			logger.setAdditive(false);
		}

		Set<SupplierResourceRecord> resources = new HashSet<SupplierResourceRecord>();

		SupplierResourceRecord resource;

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", "c5ec0a8b494a30ed41d4d6fe3107990b");
		headers.put("accept", "application/JSON");

		String sparqlEndpoint = "http://194.183.12.36:8181/semantic-registry/repository/manusquare?infer=true&limit=0&offset=0";
		SPARQLRepository repo = new SPARQLRepository(sparqlEndpoint);

		repo.initialize();
		repo.setAdditionalHttpHeaders(headers);

		String strQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n";
		strQuery += "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n";
		strQuery += "PREFIX core: <http://manusquare.project.eu/core-manusquare#> \n";
		strQuery += "PREFIX ind: <http://manusquare.project.eu/industrial-manusquare#> \n";
		strQuery += "PREFIX owl: <http://www.w3.org/2002/07/owl#> \n";
		strQuery += "SELECT distinct ?supplier ?supplierName \n";
		strQuery += "WHERE { \n";
		strQuery += "?processChain core:hasSupplier ?supplier .\n";	
		strQuery += "?supplier core:hasName ?supplierName \n";
		strQuery += "}";


		//open connection to GraphDB and run SPARQL query
		try(RepositoryConnection conn = repo.getConnection()) {

			TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, strQuery);		

			//do not include inferred statements from the KB
			tupleQuery.setIncludeInferred(true);

			try (TupleQueryResult result = tupleQuery.evaluate()) {

				while (result.hasNext()) {

					BindingSet solution = result.next();
					
					System.out.println("Supplier individual is " + solution.getValue("supplier").stringValue());
					System.out.println("Supplier name is " + solution.getValue("supplierName").stringValue());

				}
			}	
		}


		//close connection to the KB
		repo.shutDown();

		//ensure no duplicate records
		Set<SupplierResourceRecord> cleanRecords = consolidateSupplierRecords(resources);

		for (SupplierResourceRecord sr : cleanRecords) {
			System.out.println(sr.getSupplierId() + " " + sr.getSupplierName());
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

}
