package similarity;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import graph.Graph;
import testing.ConsumerQuery;
import testing.SupplierResource;
import utilities.MathUtils;
import utilities.StringUtilities;

public class SimilarityMeasures {


	public static void main(String[] args) throws OWLOntologyCreationException {

		File ontologyFile = new File ("./files/manusquare-consumer.owl");
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology sourceOnto = manager.loadOntologyFromOntologyDocument(ontologyFile);

		Label label = DynamicLabel.label(StringUtilities.stripPath(ontologyFile.toString()));

		//creates a new Neo4J db and a new ontology graph
		createOntologyGraph(ontologyFile);
		
		//dummy consumer query
		ConsumerQuery query = new ConsumerQuery();
		query.setRequiredProcess("GasTungstenArcWelding");
		query.setRequiredMaterial("AluminiumAlloy");
		query.setRequiredMachine("WeldingMachine");
		query.setRequiredCertificates("ISO9001");
		query.setQuantity(80);
		
		//dummy supplier resource
		SupplierResource resource = new SupplierResource();
		resource.setUsedProcess("ThermalWelding");
		resource.setUsedMaterial("Metal");
		resource.setUsedMachine("WeldingMachine");
		resource.setPosessedCertificates("ISO9000");
		resource.setCapacity(100);
		
		double semanticSim = computeSemanticSimilarity(query, resource, label, sourceOnto, "WuPalmer");
		System.out.println("\nThe similarity between the consumer query and the supplier resource using Wu-Palmer is " + semanticSim);
		semanticSim = computeSemanticSimilarity(query, resource, label, sourceOnto, "Resnik");
		System.out.println("\nThe similarity between the consumer query and the supplier resource using Resnik is " + semanticSim);
		semanticSim = computeSemanticSimilarity(query, resource, label, sourceOnto, "Lin");
		System.out.println("\nThe similarity between the consumer query and the supplier resource using Lin is " + semanticSim);		

	}
	
	public static double computeSemanticSimilarity (ConsumerQuery query, SupplierResource resource, Label label, OWLOntology onto, String similarityMethod) {
		
		//capacity facet similarity
		double capacitySim = 0;
		if (query.getQuantity() <= resource.getCapacity()) {
			capacitySim = 1.0;
		} else {
			capacitySim = 0.0;
		}
		
		//process facet similarity
		Node consumerQueryProcessNode = Graph.getNode(query.getRequiredProcess(), label);
		Node supplierResourceProcessNode = Graph.getNode(resource.getUsedProcess(), label);
		
		double processSim = 0;
		if (similarityMethod.equals("WuPalmer")) {
			processSim = computeWuPalmer(consumerQueryProcessNode, supplierResourceProcessNode, label);
		} else if (similarityMethod.equals("Resnik")) {
			processSim = computeResnik(consumerQueryProcessNode, supplierResourceProcessNode, label, onto);
		} else if (similarityMethod.equals("Lin")) {
			processSim = computeLin(consumerQueryProcessNode, supplierResourceProcessNode, label, onto);
		}
		
		//material facet similarity
		Node consumerQueryMaterialNode = Graph.getNode(query.getRequiredMaterial(), label);
		Node supplierResourceMaterialNode = Graph.getNode(resource.getUsedMaterial(), label);		
		
		double materialSim = 0;
		if (similarityMethod.equals("WuPalmer")) {
			materialSim = computeWuPalmer(consumerQueryMaterialNode, supplierResourceMaterialNode, label);
		} else if (similarityMethod.equals("Resnik")) {
			materialSim = computeResnik(consumerQueryMaterialNode, supplierResourceMaterialNode, label, onto);
		} else if (similarityMethod.equals("Lin")) {
			materialSim = computeLin(consumerQueryMaterialNode, supplierResourceMaterialNode, label, onto);
		}
		
		//matchine facet similarity
		Node consumerQueryMachineNode = Graph.getNode(query.getRequiredMachine(), label);
		Node supplierResourceMachineNode = Graph.getNode(resource.getUsedMachine(), label);
		
		double machineSim = 0;
		if (similarityMethod.equals("WuPalmer")) {
			machineSim = computeWuPalmer(consumerQueryMachineNode, supplierResourceMachineNode, label);
		} else if (similarityMethod.equals("Resnik")) {
			machineSim = computeResnik(consumerQueryMachineNode, supplierResourceMachineNode, label, onto);
		} else if (similarityMethod.equals("Lin")) {
			machineSim = computeLin(consumerQueryMachineNode, supplierResourceMachineNode, label, onto);
		}
		
		//certificate facet similarity
		Node consumerQueryCertificateNode = Graph.getNode(query.getRequiredCertificates(), label);
		Node supplierResourceCertificateNode = Graph.getNode(resource.getPosessedCertificates(), label);
		
		double certificateSim = 0;
		if (similarityMethod.equals("WuPalmer")) {
			certificateSim = computeWuPalmer(consumerQueryCertificateNode, supplierResourceCertificateNode, label);
		} else if (similarityMethod.equals("Resnik")) {
			certificateSim = computeResnik(consumerQueryCertificateNode, supplierResourceCertificateNode, label, onto);
		} else if (similarityMethod.equals("Lin")) {
			certificateSim = computeLin(consumerQueryCertificateNode, supplierResourceCertificateNode, label, onto);
		}
		
		double sim = (capacitySim + processSim + materialSim + machineSim + certificateSim) / 5;
		
		return sim;
		
		
	}

	public static void createOntologyGraph (File sourceOntology) throws OWLOntologyCreationException {

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology sourceOnto = manager.loadOntologyFromOntologyDocument(sourceOntology);

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String dbName = String.valueOf(timestamp.getTime());
		File dbFile = new File("/Users/audunvennesland/Documents/phd/development/Neo4J_new/" + dbName);	
		System.err.println("Creating a new NEO4J database...");
		GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase(dbFile);
		//System.out.println("Database created");

		String ontologyParameter1 = StringUtilities.stripPath(sourceOntology.toString());

		//create new graphs
		Label labelO1 = DynamicLabel.label( ontologyParameter1 );

		System.err.println("Creating ontology graph of ontology " + ontologyParameter1);
		Graph creator = new Graph(db);

		creator.createOntologyGraph(sourceOnto, labelO1);

	}

	/**
	 * Finds the lowest common subsumer of the source- and target node
	 * @param sourceNode
	 * @param targetNode
	 * @param label
	 * @return
	   May 14, 2019
	 */
	public static Node findLCS (Node sourceNode, Node targetNode, Label label) {

		ArrayList<Node> parentsToSource = Graph.getAllParentNodes(sourceNode, label);
		ArrayList<Node> parentsToTarget = Graph.getAllParentNodes(targetNode, label);
		ArrayList<Node> commonParentsList = new ArrayList<Node>();

		//TODO: If sourceNode is a parent to target or vice versa I suppose this should be considered as LCS?
		//Or, if they are the same node
		for (Node s : parentsToSource) {
			for (Node t : parentsToTarget) {
				if (s.equals(t)) {
					commonParentsList.add(s);
				}
			}
		}

		//find the common parent with the highest depth (i.e. closest to source and target nodes)
		int maxDepth = 0;
		int depth = 0;
		Node LCS = null;
		for (Node o : commonParentsList) {
			depth = Graph.findDistanceToRoot(o);
			if (depth >= maxDepth) {
				LCS = (Node)o;
				maxDepth = depth;
			}
		}

		return LCS;
	}

	/**
	 * The Wu & Palmer measure calculates relatedness by considering the depths of the source- and target nodes, along with the depth of their LCS (Lowest Common Subsumer) node.
	 * @param sourceNode
	 * @param targetNode
	 * @param label
	 * @return
	   May 14, 2019
	 */
	public static double computeWuPalmer(Node sourceNode, Node targetNode, Label label) {

		Node LCS = findLCS (sourceNode, targetNode, label);

		int sourceNodeDepth = Graph.findDistanceToRoot(sourceNode);
		int targetNodeDepth = Graph.findDistanceToRoot(targetNode);
		int lcsNodeDepth = Graph.findDistanceToRoot(LCS);

		//if the sourceNode and the targetNode are the same (which can be the case in Manusquare) we return 1.0
		if (sourceNode.equals(targetNode)) {

			return 1.0;

		} else {

			return (2 * (double)lcsNodeDepth) / ((double)sourceNodeDepth + (double)targetNodeDepth);
		}

	}

	/**
	 * Resnik is a similarity score that is based on the information content of the LCS (Lowest Common Subsumer) of the two concept for which similarity is measured.
	 * @param sourceNode
	 * @param targetNode
	 * @param label
	 * @return
	   May 14, 2019
	 */
	public static double computeResnik (Node sourceNode, Node targetNode, Label label, OWLOntology onto) {
		
		//find the lowest common subsumer
		Node LCS = findLCS(sourceNode, targetNode, label);
		
		int subConcepts = Graph.getNumChildNodes(LCS, label);
		int totalConcepts = onto.getClassesInSignature().size();
		
		return MathUtils.computeInformationContent(subConcepts, totalConcepts);

		
	}
	
	/**
	 * Lin is based on Resnik's similarity with the extension that it considers the information content of LCS (Lowest Common Subsumer) as well as the two compared concepts.
	 * @param sourceNode
	 * @param targetNode
	 * @param label
	 * @param onto
	 * @return
	   May 14, 2019
	 */
	public static double computeLin (Node sourceNode, Node targetNode, Label label, OWLOntology onto) {
		
		//find the lowest common subsumer
		Node LCS = findLCS(sourceNode, targetNode, label);
		
		//classes in ontology = nodes in graph
		int totalConcepts = onto.getClassesInSignature().size();
		int subConceptsSource = Graph.getNumChildNodes(sourceNode, label);
		int subConceptsTarget = Graph.getNumChildNodes(targetNode, label);
		int subConceptsLCS = Graph.getNumChildNodes(LCS, label);
		
		double ICSource = MathUtils.computeInformationContent(subConceptsSource, totalConcepts);
		double ICTarget = MathUtils.computeInformationContent(subConceptsTarget, totalConcepts);
		double ICLCS = MathUtils.computeInformationContent(subConceptsLCS, totalConcepts);
		
		
		return (2 * ICLCS) / (ICSource + ICTarget);
		
	}


}
