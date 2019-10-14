package graphdb;

import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;

/**
 * Testing GraphDB and RDF4J
 * @author audunvennesland
 *
 */
public class QueryGraphDB {

	private static final String GRAPHDB_SERVER = "http://localhost:7200/";
	private static final String REPOSITORY_ID = "Manusquare_1";

	public static void main(String[] args) {

		Repository repository = new HTTPRepository(GRAPHDB_SERVER, REPOSITORY_ID);
		repository.initialize();

		//SPARQL query
		String strQuery = "PREFIX core:<http://manusquare.project.eu/core-manusquare#> \n";
		strQuery += "PREFIX ind:<http://manusquare.project.eu/industrial-manusquare#> \n";
		strQuery += "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n";
		strQuery += "SELECT distinct ?supplierName ?supplierCity ?supplierNation ?process ?material ?quantity ?fromDate ?toDate ?machine ?certification #?capacity \n";
		strQuery += "WHERE { \n";
		strQuery += "?processChain core:hasSupplier ?supplier .\n";	
		strQuery += "?supplier core:hasName ?supplierName .\n";
		strQuery += "?supplier core:hasCity ?supplierCity .\n";
		strQuery += "?supplier core:hasNation ?supplierNation .\n";
		strQuery += "?processChain core:hasProcess ?process .\n";
		strQuery += "?processChain core:hasInput ?material .\n";
		strQuery += "?processChain core:hasQuantity ?quantity .\n";
		strQuery += "?processChain core:hasPeriod ?period .\n";
		strQuery += "?period core:hasFrom ?fromDate .\n";
		strQuery += "?period core:hasTo ?toDate .\n";
		strQuery += "?processChain core:_hasMachine ?machine .\n";
		strQuery += "?processChain core:_hasCertification ?certification \n";
		strQuery += "}";

		//open connection to GraphDB
		try(RepositoryConnection conn = repository.getConnection()) {

			TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, strQuery);

			try (TupleQueryResult result = tupleQuery.evaluate()) {

				int entries = 0;
				
				while (result.hasNext()) {
					entries++;
					BindingSet solution = result.next();

					System.out.println("Entry: " + entries);
					System.out.println("Supplier Name = " + solution.getValue("supplierName").stringValue());
					System.out.println("Supplier City = " + solution.getValue("supplierCity").stringValue());
					System.out.println("Supplier Nation = " + solution.getValue("supplierNation").stringValue());
					System.out.println("Process = " + solution.getValue("process").stringValue().replaceAll("http://manusquare.project.eu/industrial-manusquare#", ""));
					System.out.println("Material = " + solution.getValue("material").stringValue().replaceAll("http://manusquare.project.eu/industrial-manusquare#", ""));
					System.out.println("Quantity = " + solution.getValue("quantity").stringValue());
					System.out.println("From date = " + solution.getValue("fromDate").stringValue());
					System.out.println("To date = " + solution.getValue("toDate").stringValue());
					System.out.println("Machine = " + solution.getValue("machine").stringValue().replaceAll("http://manusquare.project.eu/industrial-manusquare#", ""));
					System.out.println("Certification(s) = " + solution.getValue("certification").stringValue().replaceAll("http://manusquare.project.eu/industrial-manusquare#", "").substring(0, 
							solution.getValue("certification").stringValue().replaceAll("http://manusquare.project.eu/industrial-manusquare#", "").indexOf("_")));
					System.out.println("\n");
				}
				
				System.out.println("There are " + entries + " entries in the knowledge base");

			}

		}

	}


}