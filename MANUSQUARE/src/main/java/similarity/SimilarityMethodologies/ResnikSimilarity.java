package similarity.SimilarityMethodologies;

import graph.Graph;
import org.neo4j.graphdb.Node;
import similarity.SimilarityMethodologies.SimilarityParameters.ResnikSimilarityParameters;
import utilities.MathUtils;

public class ResnikSimilarity implements ISimilarity<ResnikSimilarityParameters> {
    @Override
    public double ComputeSimilaritySimpleGraph(ResnikSimilarityParameters params) {
        Node LCS = Graph.findLCS(params.sourceNode, params.targetNode, params.label);
        int subConcepts = Graph.getNumChildNodes(LCS, params.label);
        int totalConcepts = params.ontology.getClassesInSignature().size();
        return MathUtils.computeInformationContent(subConcepts, totalConcepts);
    }
}
