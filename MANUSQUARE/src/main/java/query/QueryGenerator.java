package query;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import graph.Graph;
import importtestdata.CreateTestData;
import utilities.StringUtilities;

/**
 * Generates a consumer query by randomly selecting data along different facets
 * @author audunvennesland
 *
 */
public class QueryGenerator {

	public static void main(String[] args) throws OWLOntologyCreationException {


		//import and parse the owl file (throws Exception)			
		File ontologyFile = new File ("./files/manusquare-consumer.owl");
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology onto = manager.loadOntologyFromOntologyDocument(ontologyFile);

		Label label = DynamicLabel.label(StringUtilities.stripPath(ontologyFile.toString()));

		//creates a new Neo4J db and a new ontology graph
		Graph.createOntologyGraph(ontologyFile);

		//dummy consumer query containing a combination of two sub-queries
		Set<ConsumerQuery> querySet = new HashSet<ConsumerQuery>();

		ConsumerQuery query1 = generateRandomQuery(onto);
		ConsumerQuery query2 = generateRandomQuery(onto);

		querySet.add(query1);
		querySet.add(query2);

		for (ConsumerQuery q : querySet) {
			
			System.out.println(q.getRequiredProcess() + "; " + q.getRequiredMaterial() + "; " + q.getRequiredMachine() + "; " + CreateTestData.printCertifications(q.getRequiredCertificates()) + "; " + q.getCapacity());
			
		}

	}


	/**
	 * Generates a consumer query by selecting query data randomly from the relevant facets
	 * @param onto manusquare ontology
	 * @return
	   Jul 3, 2019
	 */
	public static ConsumerQuery generateRandomQuery(OWLOntology onto) {

		String machineScope = "MachineType";
		String processScope = "SubtractionProcess";
		String materialScope = "Ferrous";

		List<String> availableFrom = new LinkedList<String>();
		availableFrom.add("2019-01-01");
		availableFrom.add("2019-01-15");
		availableFrom.add("2019-02-01");
		availableFrom.add("2019-02-15");
		availableFrom.add("2019-03-01");

		List<String> availableTo = new LinkedList<String>();
		availableTo.add("2019-03-15");
		availableTo.add("2019-04-01");
		availableTo.add("2019-04-15");
		availableTo.add("2019-05-01");
		availableTo.add("2019-05-15");

		List<String> certifications = new ArrayList<String>();
		certifications.add("LEED");
		certifications.add("AS9000");
		certifications.add("AS9100");
		certifications.add("ISO14000");
		certifications.add("ISO9000");
		certifications.add("ISO9001");
		certifications.add("ISO9002");
		certifications.add("ISO9003");
		certifications.add("ISO9004");
		certifications.add("MIL");
		certifications.add("QS9000");

		//create from and to dates
		Map<String, String> dates = new HashMap<String, String>();
		for (int i = 0; i < availableFrom.size(); i++) {
			dates.put(availableFrom.get(i), availableTo.get(i));
		}

		List<Integer> capacity = new ArrayList<Integer>();
		capacity.add(50);
		capacity.add(75);
		capacity.add(100);
		capacity.add(125);
		capacity.add(150);

		List<String> materials = CreateTestData.retrieveMaterials(materialScope, onto);

		//create the correct process to machine combinations
		List<String> machines = CreateTestData.retrieveMachines(machineScope, onto);
		List<String> processes = CreateTestData.retrieveProcesses(processScope, onto);

		//map to hold correct process-to-machine combinations
		Map<String, String> combos = CreateTestData.createProcessMachineCombination(processes, machines);

		// Get a random entry from the process-to-machine combinations.
		Object[] keys = combos.keySet().toArray();
		Object key = keys[new Random().nextInt(keys.length)];

		Object[] datesKey = dates.keySet().toArray();
		Object dateKey = datesKey[new Random().nextInt(datesKey.length)];	

		ConsumerQuery query = new ConsumerQuery();
		query.setRequiredProcess((String)key);
		query.setRequiredMaterial(StringUtilities.getRandomString1(materials));
		query.setRequiredMachine((String)combos.get(key));
		query.setRequiredCertificates(StringUtilities.getRandomString3(certifications));
		query.setQuantity(StringUtilities.getRandomInt1(capacity));
		query.setRequiredAvailableFromDate((String)dateKey);
		query.setRequiredAvailableToDate((String)dates.get(dateKey));

		return query;

	}

	

}
