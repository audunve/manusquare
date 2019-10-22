package similarity;

import java.text.ParseException;
import java.util.Set;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.semanticweb.owlapi.model.OWLOntology;

import graph.Graph;
import query.ConsumerQuery;
import similarity.SimilarityMethodologies.ISimilarity;
import similarity.SimilarityMethodologies.SimilarityFactory;
import similarity.SimilarityMethodologies.SimilarityParameters.SimilarityParameters;
import similarity.SimilarityMethodologies.SimilarityParameters.SimilarityParametersFactory;
import supplierdata.Resource;

public class SimilarityMeasures {

    /**
     * Computes a similarity score along the facets Process and Certificate, and using either Wu-Palmer, Resnik, Lin or Jiang Conrath.
     *
     * @param query            represents a ConsumerQuery.
     * @param resource         represents a SupplierResource.
     * @param label            a Neo4J Label instance that distinguishes this particular ontology in the graph database.
     * @param onto             the ontology from which a graph is created.
     * @param similarityMethod
     * @return a similarity score based on a selection of facets. May 14, 2019
     * @throws ParseException 
     */
	
	 public static double computeSemanticSimilarity(ConsumerQuery query, Resource resource, Label label, OWLOntology onto, SimilarityMethods similarityMethod, boolean weighted) throws ParseException {
	        ISimilarity similarityMethodology = SimilarityFactory.GenerateSimilarityMethod(similarityMethod);

	        // process facet similarity
	        Node consumerQueryProcessNode = Graph.getNode(query.getRequiredProcess(), label);
	        
	        Node supplierResourceProcessNode = Graph.getNode(resource.getProcess(), label);
	        SimilarityParameters parameters = SimilarityParametersFactory.CreateNeo4JParameters(similarityMethod, consumerQueryProcessNode, supplierResourceProcessNode, label, onto);

	        double processSim = similarityMethodology.ComputeSimilarity(parameters);
	        
//	        // material facet similarity
//	        Node consumerQueryMaterialNode = Graph.getNode(query.getRequiredMaterial(), label);	        
//	        Node supplierResourceMaterialNode = Graph.getNode(resource.getMaterial(), label);
//	     
//	        parameters = SimilarityParametersFactory.CreateNeo4JParameters(similarityMethod, consumerQueryMaterialNode, supplierResourceMaterialNode, label, onto);
//	        double materialSim = similarityMethodology.ComputeSimilarity(parameters);
//	        
//	        // machine facet similarity
//	        Node consumerQueryMachineNode = Graph.getNode(query.getRequiredMachine(), label);	        
//	        Node supplierResourceMachineNode = Graph.getNode(resource.getMachine(), label);
//	        
//	        parameters = SimilarityParametersFactory.CreateNeo4JParameters(similarityMethod, consumerQueryMachineNode, supplierResourceMachineNode, label, onto);
//	        double machineSim = similarityMethodology.ComputeSimilarity(parameters);
	        
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
			
			if (weighted) {
				
				//weighted variant (processSim 75%, certificateSim 25%)
				return ( processSim * 0.8 ) + ( certificateSim * 0.2 );
				
			} else {
				
				//non-weighted variant
				return (processSim +  certificateSim) / 2;
			}
			
	    }
	 
//	 /**
//	     * Computes a similarity score along the facets Process, Material, Machine and Certificate, and using either Wu-Palmer, Resnik, Lin or Jiang Conrath.
//	     *
//	     * @param query            represents a ConsumerQuery.
//	     * @param resource         represents a SupplierResource.
//	     * @param label            a Neo4J Label instance that distinguishes this particular ontology in the graph database.
//	     * @param onto             the ontology from which a graph is created.
//	     * @param similarityMethod
//	     * @return a similarity score based on a selection of facets. May 14, 2019
//	     * @throws ParseException 
//	     */
//		
//		 public static double computeSemanticSimilarity(ConsumerQuery query, Resource resource, Label label, OWLOntology onto, SimilarityMethods similarityMethod) throws ParseException {
//		        ISimilarity similarityMethodology = SimilarityFactory.GenerateSimilarityMethod(similarityMethod);
//
//		        // process facet similarity
//		        Node consumerQueryProcessNode = Graph.getNode(query.getRequiredProcess(), label);
//		        
//		        Node supplierResourceProcessNode = Graph.getNode(resource.getProcess(), label);
//		        SimilarityParameters parameters = SimilarityParametersFactory.CreateNeo4JParameters(similarityMethod, consumerQueryProcessNode, supplierResourceProcessNode, label, onto);
//
//		        double processSim = similarityMethodology.ComputeSimilarity(parameters);
//		        
//		        // material facet similarity
//		        Node consumerQueryMaterialNode = Graph.getNode(query.getRequiredMaterial(), label);	        
//		        Node supplierResourceMaterialNode = Graph.getNode(resource.getMaterial(), label);
//		     
//		        parameters = SimilarityParametersFactory.CreateNeo4JParameters(similarityMethod, consumerQueryMaterialNode, supplierResourceMaterialNode, label, onto);
//		        double materialSim = similarityMethodology.ComputeSimilarity(parameters);
//		        
//		        // machine facet similarity
//		        Node consumerQueryMachineNode = Graph.getNode(query.getRequiredMachine(), label);	        
//		        Node supplierResourceMachineNode = Graph.getNode(resource.getMachine(), label);
//		        
//		        parameters = SimilarityParametersFactory.CreateNeo4JParameters(similarityMethod, consumerQueryMachineNode, supplierResourceMachineNode, label, onto);
//		        double machineSim = similarityMethodology.ComputeSimilarity(parameters);
//		        
//		        //NEW SIM COMPUTATION FOR CERTIFICATES, USE JACCARD SET SIMILARITY INSTEAD OF OWL PROCESSING
//		        //certificate facet similarity
//		        Set<String> requiredCertificates= query.getRequiredCertificates();	
//		        
//				Set<String> possessedCertificates = resource.getCertifications();
//
//				double certificateSim = 0;
//
//				if (possessedCertificates.containsAll(requiredCertificates)) {
//					certificateSim = 1.0;
//				} else {
//					certificateSim = Jaccard.jaccardSetSim(requiredCertificates, possessedCertificates);
//				} 
//		        
//		        return (processSim + materialSim + machineSim + certificateSim) / 4;
//		    }
}
