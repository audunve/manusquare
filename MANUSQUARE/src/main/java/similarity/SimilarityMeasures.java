package similarity;

import graph.Graph;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.semanticweb.owlapi.model.OWLOntology;
import similarity.SimilarityMethodologies.ISimilarity;
import similarity.SimilarityMethodologies.SimilarityFactory;
import similarity.SimilarityMethodologies.SimilarityParameters.SimilarityParameters;
import similarity.SimilarityMethodologies.SimilarityParameters.SimilarityParametersFactory;
import testing.ConsumerQuery;
import testing.SupplierResource;

public class SimilarityMeasures {

    /**
     * Computes a similarity score along the facets Process, Material, Machine and Certificate, and using either Wu-Palmer, Resnik or Lin.
     *
     * @param query            represents a ConsumerQuery.
     * @param resource         represents a SupplierResource.
     * @param label            a Neo4J Label instance that distinguishes this particular ontology in the graph database.
     * @param onto             the ontology from which a graph is created.
     * @param similarityMethod
     * @return a similarity score based on a selection of facets. May 14, 2019
     */
    public static double computeSemanticSimilarity(ConsumerQuery query, SupplierResource resource, Label label, OWLOntology onto, SimilarityMethods similarityMethod) {
        ISimilarity similarityMethodology = SimilarityFactory.GenerateSimilarityMethod(similarityMethod);

        // capacity facet similarity
        double capacitySim = 0;
        if (query.getQuantity() <= resource.getCapacity()) {
            capacitySim = 1.0;
        } else {
            capacitySim = 0.0;
        }

        // process facet similarity
        Node consumerQueryProcessNode = Graph.getNode(query.getRequiredProcess(), label);
        Node supplierResourceProcessNode = Graph.getNode(resource.getUsedProcess(), label);
        SimilarityParameters parameters = SimilarityParametersFactory.CreateNeo4JParameters(similarityMethod, consumerQueryProcessNode, supplierResourceProcessNode, label, onto);

        double processSim = similarityMethodology.ComputeSimilarity(parameters);

        // material facet similarity
        Node consumerQueryMaterialNode = Graph.getNode(query.getRequiredMaterial(), label);
        Node supplierResourceMaterialNode = Graph.getNode(resource.getUsedMaterial(), label);

        parameters = SimilarityParametersFactory.CreateNeo4JParameters(similarityMethod, consumerQueryMaterialNode, supplierResourceMaterialNode, label, onto);
        double materialSim = similarityMethodology.ComputeSimilarity(parameters);

        // matchine facet similarity
        Node consumerQueryMachineNode = Graph.getNode(query.getRequiredMachine(), label);
        Node supplierResourceMachineNode = Graph.getNode(resource.getUsedMachine(), label);

        parameters = SimilarityParametersFactory.CreateNeo4JParameters(similarityMethod, consumerQueryMachineNode, supplierResourceMachineNode, label, onto);
        double machineSim = similarityMethodology.ComputeSimilarity(parameters);

        // certificate facet similarity
        Node consumerQueryCertificateNode = Graph.getNode(query.getRequiredCertificates(), label);
        Node supplierResourceCertificateNode = Graph.getNode(resource.getPosessedCertificates(), label);

        double certificateSim = 0;
        if (consumerQueryCertificateNode != null && supplierResourceCertificateNode != null) {
            parameters = SimilarityParametersFactory.CreateNeo4JParameters(similarityMethod, consumerQueryCertificateNode, supplierResourceCertificateNode, label, onto);
            machineSim = similarityMethodology.ComputeSimilarity(parameters);
        } else {
            certificateSim = 0;
        }
        return (capacitySim + processSim + materialSim + machineSim + certificateSim) / 5;
    }
}
