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
        
//        System.out.println("Wu-Palmer: ");
//        System.out.println("Source is " + Graph.getNodeName(params.sourceNode) + ", Target is " + Graph.getNodeName(params.targetNode) + ", LCS is " + Graph.getNodeName(LCS) + ", the depth of " + Graph.getNodeName(params.sourceNode) + " is: " + sourceNodeDepth + ", the depth of " + Graph.getNodeName(params.targetNode) + " is: " + targetNodeDepth + " and the depth of LCS is " + lcsNodeDepth);

        if (params.sourceNode.equals(params.targetNode)) {
        //	System.out.println("The Wu-Palmer score is " + 1.0);
            return 1.0;
        } else {
        	//System.out.println("The Wu-Palmer score is " + (2 * (double) lcsNodeDepth) / ((double) sourceNodeDepth + (double) targetNodeDepth));
            return (2 * (double) lcsNodeDepth) / ((double) sourceNodeDepth + (double) targetNodeDepth);
        }
        
    }
}
