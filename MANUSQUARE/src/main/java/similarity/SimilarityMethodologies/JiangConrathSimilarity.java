package similarity.SimilarityMethodologies;

import graph.Graph;
import graph.SimpleGraph;

import org.neo4j.graphdb.Node;
import similarity.SimilarityMethodologies.SimilarityParameters.JiangConrathSimilarityParameters;
import utilities.MathUtils;

public class JiangConrathSimilarity implements ISimilarity<JiangConrathSimilarityParameters> {
    @Override
    public double ComputeSimilarity(JiangConrathSimilarityParameters params) {
        Node LCS = Graph.findLCS(params.sourceNodeNeo4J, params.targetNodeNeo4J, params.label);
        
        int totalConcepts = params.ontology.getClassesInSignature().size();
        int subConceptsSource = Graph.getNumChildNodes(params.sourceNodeNeo4J, params.label);
  		int subConceptsTarget = Graph.getNumChildNodes(params.targetNodeNeo4J, params.label);
  		int subConceptsLCS = Graph.getNumChildNodes(LCS, params.label);       
        
  		double ICSource = MathUtils.computeInformationContent(subConceptsSource, totalConcepts);
  		double ICTarget = MathUtils.computeInformationContent(subConceptsTarget, totalConcepts);
  		double ICLCS = MathUtils.computeInformationContent(subConceptsLCS, totalConcepts);
        
  	//if all information content values are 0...
  		if (ICSource == ICTarget && ICSource == ICLCS && ICLCS == 0) {
  			return 0;

  			//...or that Source and Target represent the same concept
  		} else if (ICSource + ICTarget == (2 * ICLCS)) {
  			return 1;
  		} else {

  			return (double) 1 + (ICLCS - ((ICSource + ICTarget) / 2));

  		}

}
    
    @Override
    public double ComputeSimilaritySimpleGraph(JiangConrathSimilarityParameters params) {
                
    	 String LCS = SimpleGraph.getLCS(params.sourceNode, params.targetNode, params.graph);
         
        int totalConcepts = params.ontology.getClassesInSignature().size();
        int subConceptsSource = Graph.getNumChildNodes(params.sourceNodeNeo4J, params.label);
   		int subConceptsTarget = Graph.getNumChildNodes(params.targetNodeNeo4J, params.label);
   		int subConceptsLCS = Graph.getNumChildNodes(LCS, params.label);       
         
   		double ICSource = MathUtils.computeInformationContent(subConceptsSource, totalConcepts);
   		double ICTarget = MathUtils.computeInformationContent(subConceptsTarget, totalConcepts);
   		double ICLCS = MathUtils.computeInformationContent(subConceptsLCS, totalConcepts);
         
   	//if all information content values are 0...
   		if (ICSource == ICTarget && ICSource == ICLCS && ICLCS == 0) {
   			return 0;

   			//...or that Source and Target represent the same concept
   		} else if (ICSource + ICTarget == (2 * ICLCS)) {
   			return 1;
   		} else {

   			return (double) 1 + (ICLCS - ((ICSource + ICTarget) / 2));

   		}
        
    }
}
