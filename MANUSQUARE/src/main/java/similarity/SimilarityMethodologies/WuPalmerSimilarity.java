package similarity.SimilarityMethodologies;

import graph.Graph;
import org.neo4j.graphdb.Node;
import similarity.SimilarityMethodologies.SimilarityParameters.WuPalmerParameters;

public class WuPalmerSimilarity implements ISimilarity<WuPalmerParameters> {

    @Override
    public double ComputeSimilarity(WuPalmerParameters params) {
        Node LCS = Graph.findLCS(params.sourceNode, params.targetNode, params.label);
        int sourceNodeDepth = Graph.findDistanceToRoot(params.sourceNode);
        int targetNodeDepth = Graph.findDistanceToRoot(params.targetNode);
        int lcsNodeDepth = Graph.findDistanceToRoot(LCS);

        if (params.sourceNode.equals(params.targetNode)) {
            return 1.0;
        } else {
            return (2 * (double) lcsNodeDepth) / ((double) sourceNodeDepth + (double) targetNodeDepth);
        }
    }
}
