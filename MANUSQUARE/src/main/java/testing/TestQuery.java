package testing;

import java.io.File;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import graph.Graph;
import similarity.SimilarityMeasures;
import utilities.StringUtilities;

public class TestQuery {

	//test method
	public static void main(String[] args) throws OWLOntologyCreationException {
	
		//import and parse the owl file
		File ontologyFile = new File ("./files/manusquare-consumer.owl");
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
		
		//dummy supplier resource
		//TODO: Create a set of supplier resources using the test-data from Hans de Man. 
		SupplierResource resource = new SupplierResource();
		resource.setUsedProcess("ThermalWelding");
		resource.setUsedMaterial("Metal");
		resource.setUsedMachine("WeldingMachine");
		resource.setPosessedCertificates("ISO9000");
		resource.setCapacity(100);
		
		//find similarity using "Wu-Palmer", "Resnik" or "Lin" as different parameters
		double semanticSim = SimilarityMeasures.computeSemanticSimilarity(query, resource, label, sourceOnto, "WuPalmer");
		System.out.println("\nThe similarity between the consumer query and the supplier resource using Wu-Palmer is " + semanticSim);
		
		semanticSim = SimilarityMeasures.computeSemanticSimilarity(query, resource, label, sourceOnto, "Resnik");
		System.out.println("\nThe similarity between the consumer query and the supplier resource using Resnik is " + semanticSim);
		
		semanticSim = SimilarityMeasures.computeSemanticSimilarity(query, resource, label, sourceOnto, "Lin");
		System.out.println("\nThe similarity between the consumer query and the supplier resource using Lin is " + semanticSim);		

	}

}
