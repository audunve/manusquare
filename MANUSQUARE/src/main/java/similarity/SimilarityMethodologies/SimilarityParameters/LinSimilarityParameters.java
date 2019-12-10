package similarity.SimilarityMethodologies.SimilarityParameters;

//import org.neo4j.graphdb.Label;
//import org.neo4j.graphdb.Node;
import org.semanticweb.owlapi.model.OWLOntology;

public class LinSimilarityParameters extends SimilarityParameters{
  /*  public Node sourceNode;
    public Node targetNode;
    public Label label;*/
    public OWLOntology ontology;

    public LinSimilarityParameters(/*Node sourceNode, Node targetNode, Label label,*/ OWLOntology ontology) {
    /*    this.sourceNode = sourceNode;
        this.targetNode = targetNode;
        this.label = label;*/
        this.ontology = ontology;
    }
}
