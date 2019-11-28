package similarity.SimilarityMethodologies.SimilarityParameters;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.semanticweb.owlapi.model.OWLOntology;

import com.google.common.graph.MutableGraph;

public class JiangConrathSimilarityParameters extends SimilarityParameters{
    public String sourceNode;
    public String targetNode;
    public MutableGraph<String> graph;
    public OWLOntology ontology;
    
    //neo4j
    public Node sourceNodeNeo4J;
    public Node targetNodeNeo4J;
    public Label label;

    public JiangConrathSimilarityParameters(String sourceNode, String targetNode, OWLOntology ontology, MutableGraph<String> graph) {
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;

        this.ontology = ontology;
    }
    
    public JiangConrathSimilarityParameters(Node sourceNode, Node targetNode, Label label, OWLOntology ontology) {
    	  this.sourceNodeNeo4J = sourceNode;
    	  this.targetNodeNeo4J = targetNode;
    	  this.label = label;
    	  this.ontology = ontology;
}
}
