package similarity;

import graph.Graph;

import java.util.Set;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.semanticweb.owlapi.model.OWLOntology;
import similarity.SimilarityMethodologies.ISimilarity;
import similarity.SimilarityMethodologies.SimilarityFactory;
import similarity.SimilarityMethodologies.SimilarityParameters.SimilarityParameters;
import similarity.SimilarityMethodologies.SimilarityParameters.SimilarityParametersFactory;
import supplierdata.Resource;
import query.ConsumerQuery;

public class SimilarityMeasures {

    /**
     * Computes a similarity score along the facets Process, Material, Machine and Certificate, and using either Wu-Palmer, Resnik, Lin or Jiang Conrath.
     *
     * @param query            represents a ConsumerQuery.
     * @param resource         represents a SupplierResource.
     * @param label            a Neo4J Label instance that distinguishes this particular ontology in the graph database.
     * @param onto             the ontology from which a graph is created.
     * @param similarityMethod
     * @return a similarity score based on a selection of facets. May 14, 2019
     */
	
	 public static double computeSemanticSimilarity(ConsumerQuery query, Resource resource, Label label, OWLOntology onto, SimilarityMethods similarityMethod) {
	        ISimilarity similarityMethodology = SimilarityFactory.GenerateSimilarityMethod(similarityMethod);

	        // capacity facet similarity
	        double capacitySim = 0;
	        if (query.getCapacity() <= resource.getCapacity()) {
	            capacitySim = 1.0;
	        } else {
	            capacitySim = 0.0;
	        }

	        // process facet similarity
	        Node consumerQueryProcessNode = Graph.getNode(query.getRequiredProcess(), label);
	        Node supplierResourceProcessNode = Graph.getNode(resource.getProcess(), label);
	        SimilarityParameters parameters = SimilarityParametersFactory.CreateNeo4JParameters(similarityMethod, consumerQueryProcessNode, supplierResourceProcessNode, label, onto);

	        double processSim = similarityMethodology.ComputeSimilarity(parameters);

	        // material facet similarity
	        Node consumerQueryMaterialNode = Graph.getNode(query.getRequiredMaterial(), label);
	        Node supplierResourceMaterialNode = Graph.getNode(resource.getMaterial(), label);

	        parameters = SimilarityParametersFactory.CreateNeo4JParameters(similarityMethod, consumerQueryMaterialNode, supplierResourceMaterialNode, label, onto);
	        double materialSim = similarityMethodology.ComputeSimilarity(parameters);

	        // matchine facet similarity
	        Node consumerQueryMachineNode = Graph.getNode(query.getRequiredMachine(), label);
	        Node supplierResourceMachineNode = Graph.getNode(resource.getMachine(), label);

	        parameters = SimilarityParametersFactory.CreateNeo4JParameters(similarityMethod, consumerQueryMachineNode, supplierResourceMachineNode, label, onto);
	        double machineSim = similarityMethodology.ComputeSimilarity(parameters);

	        // certificate facet similarity
//	        Node consumerQueryCertificateNode = Graph.getNode(query.getRequiredCertificates(), label);
//	        Node supplierResourceCertificateNode = Graph.getNode(resource.getPosessedCertificates(), label);
//
//	        double certificateSim = 0;
//	        if (consumerQueryCertificateNode != null && supplierResourceCertificateNode != null) {
//	            parameters = SimilarityParametersFactory.CreateNeo4JParameters(similarityMethod, consumerQueryCertificateNode, supplierResourceCertificateNode, label, onto);
//	            machineSim = similarityMethodology.ComputeSimilarity(parameters);
//	        } else {
//	            certificateSim = 0;
//	        }
	        
	        //NEW SIM COMPUTATION FOR CERTIFICATES, USE JACCARD SET SIMILARITY INSTEAD OF OWL PROCESSING
	        //certificate facet similarity
	        Set<String> requiredCertificates= query.getRequiredCertificates();	
	        
			Set<String> possessedCertificates = resource.getCertifications();

			double certificateSim = 0;

			if (possessedCertificates.containsAll(requiredCertificates)) {
				certificateSim = 1.0;
			} else {
				certificateSim = Jaccard.jaccardSetSim(requiredCertificates, possessedCertificates);
			} 
	        
	        
	        
	        return (capacitySim + processSim + materialSim + machineSim + certificateSim) / 5;
	    }
}
