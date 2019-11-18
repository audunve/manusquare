package query;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import edm.Certification;
import edm.Material;
import edm.Process;
import exceptions.NoProcessException;
import json.RequestForQuotation;
import json.RequestForQuotation.ProjectAttributeKeys;
import json.RequestForQuotation.SupplierAttributeKeys;
import owlprocessing.OntologyOperations;

public class ConsumerQuery {

	private Set<Process> processes;
	private Set<Certification> certifications;

	//if the consumer specifies both processes (incl. materials) and certifications.
	public ConsumerQuery(Set<Process> processes, Set<Certification> certifications) {
		super();
		this.processes = processes;
		this.certifications = certifications;
	}

	//if only processes (incl. materials) are specified by the consumer
	public ConsumerQuery(Set<Process> processes) {
		super();
		this.processes = processes;
	}

	public ConsumerQuery() {}

	public Set<Process> getProcesses() {
		return processes;
	}

	public void setProcesses(Set<Process> processes) {
		this.processes = processes;
	}

	public Set<Certification> getCertifications() {
		return certifications;
	}

	public void setCertifications(Set<Certification> certifications) {
		this.certifications = certifications;
	}

	/**
	 * Parses a json file and creates a ConsumerQuery object representing the input provided by a consumer in the RFQ establishment process.
	 * @param filename the path to the input json file.
	 * @param onto the MANUSQUARE ontology.
	 * @return a ConsumerQuery object representing processes/materials/certifications requested by a consumer.
	 * @throws JsonSyntaxException
	 * @throws JsonIOException
	 * @throws FileNotFoundException
	   Nov 14, 2019
	 */
	public static ConsumerQuery createConsumerQuery (String filename, OWLOntology onto) throws JsonSyntaxException, JsonIOException, FileNotFoundException {

		Set<Process> processes = new HashSet<Process>();
		Set<Certification> certifications = new HashSet<Certification>();

		Set<String> processNames = new HashSet<String>();

		RequestForQuotation rfq = new Gson().fromJson(new FileReader(filename), RequestForQuotation.class);

		if (rfq.projectAttributes == null || rfq.projectAttributes.isEmpty()) {
			throw new NoProcessException ("Processes must be included!");
		} else {
			for (ProjectAttributeKeys projectAttributes : rfq.projectAttributes) {
				processNames.add(projectAttributes.processName);
			}
		}

		//get materials and map them to process
		Map<String, Set<Material>> materialMap = new HashMap<String, Set<Material>>();

		for (String process : processNames) {
			Set<Material> materialSet = new HashSet<Material>();
			for (ProjectAttributeKeys projectAttributes : rfq.projectAttributes) {

				if (projectAttributes.attributeKey.equals("material") && projectAttributes.processName.equals(process)) {

					materialSet.add(new Material(projectAttributes.attributeValue));
					materialMap.put(process, validateMaterials(materialSet, onto));
				}			
			}			
		}

		//if there are materials specified...
		if (materialMap.size() != 0 || !materialMap.isEmpty()) {
			//create processes
			for (Entry<String, Set<Material>> e : materialMap.entrySet()) {
				processes.add(new Process(e.getKey(), e.getValue()));
			}
		} else {
			for (String processName : processNames) {
				processes.add(new Process(processName));
			}
		}

		//validate processes to ensure that we only operate with concepts in the ontology
		Set<Process> validatedProcesses = validateProcesses(processes, onto);

		ConsumerQuery query = null;

		//get certifications if they are specified by the consumer
		if (rfq.supplierAttributes == null || rfq.supplierAttributes.isEmpty()) {

			//if no certifications, we only add the processes to the ConsumerQuery object
			query = new ConsumerQuery(validatedProcesses);

		} else {

			for (SupplierAttributeKeys supplierAttributes : rfq.supplierAttributes) {
				if (supplierAttributes.attributeKey.equals("certification")) {
					certifications.add(new Certification(supplierAttributes.attributeValue));
				}
			}

			//if there are certifications specified we add those along with processes to the ConsumerQuery object
			query = new ConsumerQuery(validatedProcesses, validateCertifications(certifications, onto));

		}

		return query;
	}

	/**
	 * Checks if all processes specified by the consumer actually exist as concepts in the ontology. If they´re not, find the closest matching concept.
	 * @param initialProcesses set of processes specified by the consumer in the RFQ process
	 * @param ontology the MANUSQUARE ontology
	 * @return set of processes we´re sure exist as concepts in the ontology
	   Nov 13, 2019
	 */
	private static Set<Process> validateProcesses (Set<Process> initialProcesses, OWLOntology ontology) {
		Set<Process> validatedProcesses = new HashSet<Process>();

		//check if all processes in [initialProcesses] are represented as concepts in the ontology
		Set<String> ontologyClassesAsString = OntologyOperations.getClassesAsString(ontology);

		for (Process p : initialProcesses) {
			if (!ontologyClassesAsString.contains(p.getName())) { //if not, get the concept from the ontology with the highest similarity
				p.setName(getMostSimilarConcept(p.getName(), ontologyClassesAsString, "levenshtein"));
				validatedProcesses.add(p);
			} else {
				validatedProcesses.add(p);
			}
		}

		return validatedProcesses;

	}

	/**
	 * Checks if all materials specified by the consumer actually exist as concepts in the ontology. If they´re not, find the closest matching concept.
	 * @param initialMaterials set of materials specified by the consumer in the RFQ process
	 * @param ontology the MANUSQUARE ontology
	 * @return set of materials we´re sure exist as concepts in the ontology
	   Nov 13, 2019
	 */
	private static Set<Material> validateMaterials (Set<Material> initialMaterials, OWLOntology ontology) {
		Set<Material> validatedMaterials = new HashSet<Material>();

		//check if all processes in [initialProcesses] are represented as concepts in the ontology
		Set<String> ontologyClassesAsString = OntologyOperations.getClassesAsString(ontology);

		for (Material m : initialMaterials) {
			if (!ontologyClassesAsString.contains(m.getName())) { //if not, get the concept from the ontology with the highest similarity
				m.setName(getMostSimilarConcept(m.getName(), ontologyClassesAsString, "levenshtein"));
				validatedMaterials.add(m);
			} else {
				validatedMaterials.add(m);
			}
		}

		return validatedMaterials;

	}

	/**
	 * Checks if all certifications specified by the consumer actually exist as concepts in the ontology. If they´re not, find the closest matching concept.
	 * @param initialCertifications set of certifications specified by the consumer in the RFQ process
	 * @param ontology the MANUSQUARE ontology
	 * @return set of certifications we´re sure exist as concepts in the ontology
	   Nov 13, 2019
	 */
	private static Set<Certification> validateCertifications (Set<Certification> initialCertifications, OWLOntology ontology) {
		Set<Certification> validatedMaterials = new HashSet<Certification>();

		//check if all processes in [initialProcesses] are represented as concepts in the ontology
		Set<String> ontologyClassesAsString = OntologyOperations.getClassesAsString(ontology);

		for (Certification c : initialCertifications) {
			if (!ontologyClassesAsString.contains(c.getId())) { //if not, get the concept from the ontology with the highest similarity
				System.out.println("The ontology does not contain " + c.getId());
				c.setId(getMostSimilarConcept(c.getId(), ontologyClassesAsString, "levenshtein"));
				validatedMaterials.add(c);
			} else {
				validatedMaterials.add(c);
			}
		}

		return validatedMaterials;

	}

	/**
	 * Uses (string) similarity techniques to find most similar ontology concept to a consumer-specified process/material/certification
	 * @param input the input process/material/certification specified by the consumer
	 * @param ontologyClassesAsString set of ontology concepts represented as strings
	 * @param method the similarity method applied
	 * @return the best matching concept from the MANUSQUARE ontology
	   Nov 13, 2019
	 */
	private static String getMostSimilarConcept(String input, Set<String> ontologyClassesAsString, String method) {

		Map<String, Double> similarityMap = new HashMap<String, Double>();
		String mostSimilarConcept = null;

		//remove whitespace in input string
		input.replaceAll("\\s+","");

		//levenshtein distance
		if (method.equals("levenshtein")) {

			for (String s : ontologyClassesAsString) {
				similarityMap.put(s, 1-fr.inrialpes.exmo.ontosim.string.StringDistances.levenshteinDistance(input, s));
			}

			mostSimilarConcept = getConceptWithHighestSim(similarityMap);
			//System.out.println("The ontology concept with highest sim to " + input + " is " + mostSimilarConcept + " with sim: " + similarityMap.get(mostSimilarConcept));
		}
		//n-gram distance
		else if (method.equals("ngram")) {

			for (String s : ontologyClassesAsString) {
				similarityMap.put(s, fr.inrialpes.exmo.ontosim.string.StringDistances.ngramDistance(input, s));
			}

			mostSimilarConcept = getConceptWithHighestSim(similarityMap);
		}

		return mostSimilarConcept;

	}

	/**
	 * Returns the concept (name) with the highest (similarity) score from a map of concepts
	 * @param similarityMap a map of concepts along with their similarity scores
	 * @return single concept (name) with highest similarity score
	   Nov 13, 2019
	 */
	private static String getConceptWithHighestSim (Map<String, Double> similarityMap) {

		Map<String, Double> rankedResults = sortDescending(similarityMap);

		Map.Entry<String,Double> entry = rankedResults.entrySet().iterator().next();
		String conceptWithHighestSim = entry.getKey();


		return conceptWithHighestSim;

	}

	/** 
	 * Sorts a map based on similarity scores (values in the map)
	 * @param map the input map to be sorted
	 * @return map with sorted values
	   May 16, 2019
	 */
	private static <K, V extends Comparable<V>> Map<K, V> sortDescending(final Map<K, V> map) {
		Comparator<K> valueComparator =  new Comparator<K>() {
			public int compare(K k1, K k2) {
				int compare = map.get(k2).compareTo(map.get(k1));
				if (compare == 0) return 1;
				else return compare;
			}
		};
		Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);

		sortedByValues.putAll(map);

		return sortedByValues;
	}

	//test method
	public static void main(String[] args) throws JsonSyntaxException, JsonIOException, FileNotFoundException, OWLOntologyCreationException {

		String filename = "./files/rfq.json";
		String ontology = "./files/ONTOLOGIES/updatedOntology.owl";

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology onto = manager.loadOntologyFromOntologyDocument(new File(ontology));

		ConsumerQuery query = createConsumerQuery (filename, onto);

		System.out.println("Printing query:");

		for (Process p : query.getProcesses()) {
			System.out.println(p.getName());
			for (Material m : p.getMaterials()) {
				System.out.println(m.getName());
			}
		}

		for (Certification cert : query.getCertifications()) {
			System.out.println(cert.getId());
		}
	}

}
