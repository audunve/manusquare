package similarity.SimilarityMethodologies;

import graph.Graph;
import org.neo4j.graphdb.Node;
import similarity.SimilarityMethodologies.SimilarityParameters.LinSimilarityParameters;
import utilities.MathUtils;

public class  LinSimilarity implements ISimilarity<LinSimilarityParameters> {

    @Override
    public double ComputeSimilaritySimpleGraph(LinSimilarityParameters params) {

        // find the lowest common subsumer
        Node LCS = Graph.findLCS(params.sourceNode, params.targetNode, params.label);

        // classes in ontology = nodes in graph
        int totalConcepts = params.ontology.getClassesInSignature().size();
        int subConceptsSource = Graph.getNumChildNodes(params.sourceNode, params.label);
        int subConceptsTarget = Graph.getNumChildNodes(params.targetNode, params.label);
        int subConceptsLCS = Graph.getNumChildNodes(LCS, params.label);

        double ICSource = MathUtils.computeInformationContent(subConceptsSource, totalConcepts);
        double ICTarget = MathUtils.computeInformationContent(subConceptsTarget, totalConcepts);
        double ICLCS = MathUtils.computeInformationContent(subConceptsLCS, totalConcepts);

        return (2 * ICLCS) / (ICSource + ICTarget);
    }
}
