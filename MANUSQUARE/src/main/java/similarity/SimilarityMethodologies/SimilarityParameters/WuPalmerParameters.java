package similarity.SimilarityMethodologies.SimilarityParameters;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

public class WuPalmerParameters extends SimilarityParameters {
    public Node sourceNode;
    public Node targetNode;
    public Label label;

    public WuPalmerParameters(Node sourceNode, Node targetNode, Label label) {
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
        this.label = label;
    }
}
