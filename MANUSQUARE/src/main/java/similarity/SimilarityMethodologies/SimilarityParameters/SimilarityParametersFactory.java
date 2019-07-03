package similarity.SimilarityMethodologies.SimilarityParameters;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.semanticweb.owlapi.model.OWLOntology;
import similarity.SimilarityMethods;

//TODO: CONVERT TO BUILDERS (if more params
public class SimilarityParametersFactory {
    public static SimilarityParameters CreateNeo4JParameters(SimilarityMethods methodology, Node sourceNode, Node targetNode, Label label, OWLOntology ontology) {
        switch(methodology) {
            case WU_PALMER:
                return new WuPalmerParameters(sourceNode, targetNode, label);
            case RESNIK:
                    return new ResnikSimilarityParameters(sourceNode, targetNode, label, ontology);
            case LIN:
                return new LinSimilarityParameters(sourceNode, targetNode, label, ontology);
            case JIANG_CONRATH:
                return new JiangConrathSimilarityParameters(sourceNode, targetNode, label, ontology);
            default:
                throw new UnsupportedOperationException("Invalid methodology for creating Neo4J Parameters:: " + methodology);
        }
    }
}
