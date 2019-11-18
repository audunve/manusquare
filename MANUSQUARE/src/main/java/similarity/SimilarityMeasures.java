package similarity;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.semanticweb.owlapi.model.OWLOntology;

import edm.Certification;
import edm.Material;
import edm.Process;
import graph.Graph;
import query.ConsumerQuery;
import similarity.SimilarityMethodologies.ISimilarity;
import similarity.SimilarityMethodologies.SimilarityFactory;
import similarity.SimilarityMethodologies.SimilarityParameters.SimilarityParameters;
import similarity.SimilarityMethodologies.SimilarityParameters.SimilarityParametersFactory;
import supplierdata.Supplier;

public class SimilarityMeasures {
	
	public static List<Double> computeSemanticSimilarity (ConsumerQuery query, Supplier supplier, Label label, OWLOntology onto, SimilarityMethods similarityMethod, boolean weighted) {
		
		//System.out.println("\nSimilarityMeasures.java: Computing similarity for supplier " + supplier.getId());

		//get the list of processes and certifications for this supplier
		List<Process> processList = supplier.getProcesses();
		List<Certification> certificationList = supplier.getCertifications();

		ISimilarity similarityMethodology = SimilarityFactory.GenerateSimilarityMethod(similarityMethod);

		//for each process in the query, compute the process facet similarity
		Node consumerQueryProcessNode = null;
		Node supplierResourceProcessNode = null;

		SimilarityParameters parameters = null;

		Set<String> consumerMaterials = new HashSet<String>();
		Set<String> supplierMaterials = new HashSet<String>();

		double processAndMaterialSim = 0;
		double processSim = 0;
		double materialSim = 0;
		double certificateSim = 0;
		double allCombinedSim = 0;

		List<Double> similarityList = new LinkedList<Double>();

		for (Process pc : query.getProcesses()) {
			for (Process ps : processList) {		

				//represent processes as graph nodes
				consumerQueryProcessNode = Graph.getNode(pc.getName(), label);
				supplierResourceProcessNode = Graph.getNode(ps.getName(), label);

				//compute similarity for processes
				parameters = SimilarityParametersFactory.CreateNeo4JParameters(similarityMethod, consumerQueryProcessNode, supplierResourceProcessNode, label, onto);
				processSim = similarityMethodology.ComputeSimilarity(parameters);
				
				//System.out.println("SimilarityMeasures.java: processSim is " + processSim);

				//Check if there are materials specified in the query
				if (pc.getMaterials() == null || pc.getMaterials().isEmpty()) {
					processAndMaterialSim = processSim;
				} else {
					//materials related to consumer process
					for (Material m : pc.getMaterials()) {
						consumerMaterials.add(m.getName());
					}

					//materials related to supplier process
					Set<Material> materials = ps.getMaterials();
					for (Material material : materials) {
						supplierMaterials.add(material.getName());
					}

					//if the set of materials in the supplier process contains all materials requested by the consumer --> 1.0
					if (supplierMaterials.containsAll(consumerMaterials)) {
						materialSim = 1.0;
					} else { //if not, localMaterialSim is the Jaccard set similarity between the supplierMaterials and the consumerMaterials
						materialSim = Jaccard.jaccardSetSim(supplierMaterials, consumerMaterials);
					}
					
					//System.out.println("SimilarityMeasures.java: materialSim is " + materialSim);

					//we should probably prioritise processes over materials
					if (weighted) {
						processAndMaterialSim = (processSim * 0.75) + (materialSim * 0.25);
					} else {
						processAndMaterialSim = (processSim + materialSim) / 2;
					}
					//System.out.println("SimilarityMeasures.java: processAndMaterialSim is " + processAndMaterialSim);

				}
				
				//certificate facet similarity
				Set<String> requiredCertificates= new HashSet<String>();

				Set<String> possessedCertificates = new HashSet<String>();
				for (Certification c : certificationList) {
					possessedCertificates.add(c.getId());
				}

				//if the consumer hasn´t specified any required certifications we only compute similarity based on processes (and materials)
				if (query.getCertifications() == null || query.getCertifications().isEmpty()) {
					//System.err.println("SimilarityMeasures.java: There are no certifications specified by the consumer!");

					allCombinedSim = processAndMaterialSim;

				} else { //if the consumer has specified required certifications we compute similarity based on processes (and materials) and certifications

					for (Certification c : query.getCertifications()) {
						requiredCertificates.add(c.getId());
					}

					if (possessedCertificates.containsAll(requiredCertificates)) {
						certificateSim = 1.0;
					} else {
						certificateSim = Jaccard.jaccardSetSim(requiredCertificates, possessedCertificates);
					} 
					
					//System.out.println("SimilarityMeasures.java: certificateSim is " + certificateSim);

					if (weighted) {
						allCombinedSim = (processAndMaterialSim * 0.75)  + (certificateSim * 0.25);
					} else {
						allCombinedSim = (processAndMaterialSim + certificateSim) / 2;
					}
					
					//System.out.println("allCombinedSim is " + allCombinedSim);
				}

				similarityList.add(allCombinedSim);
			}			
		}	
		
		//System.out.println("SimilarityMeasures.java: the similarities for supplier " + supplier.getId() + " are:");		
//		for (Double d : similarityList) {
//			System.out.println(d);
//		}

		return similarityList;

	}

}
