package similarity.SimilarityMethodologies;

import graph.SimpleGraph;
import similarity.SimilarityMethodologies.SimilarityParameters.WuPalmerParameters;

public class WuPalmerSimilarity implements ISimilarity<WuPalmerParameters> {

    
    @Override
    public double ComputeSimilaritySimpleGraph(WuPalmerParameters params) {
                
        String LCS = SimpleGraph.getLCS(params.sourceNode, params.targetNode, params.graph);

        int sourceNodeDepth = SimpleGraph.getNodeDepth(params.sourceNode, params.graph);
        int targetNodeDepth = SimpleGraph.getNodeDepth(params.targetNode, params.graph);
        int lcsNodeDepth = SimpleGraph.getNodeDepth(LCS, params.graph);

        if (params.sourceNode.equals(params.targetNode)) {
            return 1.0;
        } else {
            return (2 * (double) lcsNodeDepth) / ((double) sourceNodeDepth + (double) targetNodeDepth);
        }
        
    }
}
