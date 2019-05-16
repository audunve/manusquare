package similarity;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.semanticweb.owlapi.model.OWLOntology;

import graph.Graph;
import testing.ConsumerQuery;
import testing.SupplierResource;
import utilities.MathUtils;

public class SimilarityMeasures {

	/**
	 * Computes a similarity score along the facets Process, Material, Machine and Certificate, and using either Wu-Palmer, Resnik or Lin.
	 * @param query represents a ConsumerQuery.
	 * @param resource represents a SupplierResource.
	 * @param label a Neo4J Label instance that distinguishes this particular ontology in the graph database. 
	 * @param onto the ontology from which a graph is created.
	 * @param similarityMethod
	 * @return a similarity score based on a selection of facets.
	   May 14, 2019
	 */
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
		
		//TODO: Find another way to use different similarity methods, e.g. using Properties.
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
		
		if (consumerQueryCertificateNode != null && supplierResourceCertificateNode != null) {
		
		if (similarityMethod.equals("WuPalmer")) {
			certificateSim = computeWuPalmer(consumerQueryCertificateNode, supplierResourceCertificateNode, label);
		} else if (similarityMethod.equals("Resnik")) {
			certificateSim = computeResnik(consumerQueryCertificateNode, supplierResourceCertificateNode, label, onto);
		} else if (similarityMethod.equals("Lin")) {
			certificateSim = computeLin(consumerQueryCertificateNode, supplierResourceCertificateNode, label, onto);
		}
		} else {
			certificateSim = 0;
		}
		
		double sim = (capacitySim + processSim + materialSim + machineSim + certificateSim) / 5;
		
		return sim;
		
		
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

		Node LCS = Graph.findLCS (sourceNode, targetNode, label);

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
		Node LCS = Graph.findLCS(sourceNode, targetNode, label);
		
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
		Node LCS = Graph.findLCS(sourceNode, targetNode, label);
		
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
