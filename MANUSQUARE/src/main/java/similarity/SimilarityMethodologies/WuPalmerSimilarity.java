package similarity.SimilarityMethodologies;

import graph.Graph;
import org.neo4j.graphdb.Node;
import similarity.SimilarityMethodologies.SimilarityParameters.WuPalmerParameters;

import graph.SimpleGraph;

public class WuPalmerSimilarity implements ISimilarity<WuPalmerParameters> {

//    @Override
//    public double ComputeSimilarity(WuPalmerParameters params) {
//        Node LCS = Graph.findLCS(params.sourceNodeNeo4J, params.targetNodeNeo4J, params.label);
//        
//        
//        int sourceNodeDepth = Graph.findDistanceToRoot(params.sourceNodeNeo4J);
//        int targetNodeDepth = Graph.findDistanceToRoot(params.targetNodeNeo4J);
//        int lcsNodeDepth = Graph.findDistanceToRoot(LCS);
//        
//        System.out.println("Wu-Palmer: ");
//        System.out.println("Source is " + Graph.getNodeName(params.sourceNodeNeo4J) + ", Target is " + Graph.getNodeName(params.targetNodeNeo4J) + ", LCS is " + Graph.getNodeName(LCS) + ", the depth of " + Graph.getNodeName(params.sourceNodeNeo4J) + " is: " + sourceNodeDepth + ", the depth of " + Graph.getNodeName(params.targetNodeNeo4J) + " is: " + targetNodeDepth + " and the depth of LCS is " + lcsNodeDepth);
//
//        if (params.sourceNodeNeo4J.equals(params.targetNodeNeo4J)) {
//        //	System.out.println("The Wu-Palmer score is " + 1.0);
//            return 1.0;
//        } else {
//        	//System.out.println("The Wu-Palmer score is " + (2 * (double) lcsNodeDepth) / ((double) sourceNodeDepth + (double) targetNodeDepth));
//            return (2 * (double) lcsNodeDepth) / ((double) sourceNodeDepth + (double) targetNodeDepth);
//        }
//        
//    }
    
    @Override
    public double ComputeSimilaritySimpleGraph(WuPalmerParameters params) {
                
        String LCS = SimpleGraph.getLCS(params.sourceNode, params.targetNode, params.graph);

        int sourceNodeDepth = SimpleGraph.getNodeDepth(params.sourceNode, params.graph);
        int targetNodeDepth = SimpleGraph.getNodeDepth(params.targetNode, params.graph);
        int lcsNodeDepth = SimpleGraph.getNodeDepth(LCS, params.graph);
        
        //System.out.println("Wu-Palmer: ");
        //System.out.println("Source is " + params.sourceNode + ", Target is " + params.targetNode + ", LCS is " + LCS + ", the depth of " + params.sourceNode + " is: " + sourceNodeDepth + ", the depth of " + params.targetNode + " is: " + targetNodeDepth + " and the depth of LCS is " + lcsNodeDepth);

        if (params.sourceNode.equals(params.targetNode)) {
        //	System.out.println("The Wu-Palmer score is " + 1.0);
            return 1.0;
        } else {
        	//System.out.println("The Wu-Palmer score is " + (2 * (double) lcsNodeDepth) / ((double) sourceNodeDepth + (double) targetNodeDepth));
            return (2 * (double) lcsNodeDepth) / ((double) sourceNodeDepth + (double) targetNodeDepth);
        }
        
    }
}
