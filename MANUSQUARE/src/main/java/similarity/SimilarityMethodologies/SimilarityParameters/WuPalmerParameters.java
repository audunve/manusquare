package similarity.SimilarityMethodologies.SimilarityParameters;

import com.google.common.graph.MutableGraph;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

public class WuPalmerParameters extends SimilarityParameters {
    public String sourceNode;
    public String targetNode;
    public MutableGraph<String> graph;
    
//    //neo4j
//    public Node sourceNodeNeo4J;
//    public Node targetNodeNeo4J;
//    public Label label;

    public WuPalmerParameters(String sourceNode, String targetNode, MutableGraph<String> graph) {
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
        this.graph = graph;
    }
    
//  public WuPalmerParameters(Node sourceNode, Node targetNode, Label label) {
//  this.sourceNodeNeo4J = sourceNode;
//  this.targetNodeNeo4J = targetNode;
//  this.label = label;
//}
}
