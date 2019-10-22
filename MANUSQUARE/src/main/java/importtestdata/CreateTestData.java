package importtestdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import owlprocessing.OntologyOperations;
import supplierdata.Resource;
import supplierdata.Supplier;
import utilities.StringUtilities;

public class CreateTestData {
	
	public static void main(String[] args) throws OWLOntologyCreationException, IOException {

		int numSupplierResourceRecordsToPrint = 900;

		File ontologyFile = new File ("./files/ONTOLOGIES/manusquare-consumer.owl");
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology onto = manager.loadOntologyFromOntologyDocument(ontologyFile);		
		String inputCSV = "./files/TESTDATA/CSV2Set_971suppliers.csv";
		String SETOutputCSV = "./files/TESTDATA/SET_ResourceRecords.csv";
		String OWLOutputCSV = "./files/TESTDATA/OWL_ResourceRecords.csv";

		String machineScope = "MachineType";
		String processScope = "SubtractionProcess";
		String materialScope = "Ferrous";
		String productsScope = "MetalProducts";
		String humanCapabilitiyScope = "EngineeringService";
		
		//createSETData (numSupplierResourceRecordsToPrint, ontologyFile, inputCSV, SETOutputCSV, materialScope, processScope, machineScope);
		//createOWLData (numSupplierResourceRecordsToPrint, ontologyFile, inputCSV, OWLOutputCSV, materialScope, processScope, machineScope);
		
		List<String> test =  retrieveProcesses(processScope, onto);
		
		for (String s : test) {
			System.out.println(s);
		}


	}

	/**
	 * Creates a csv format of [supplier id];[supplier name];[supplier city];[supplier country];[RFQ response time];[capacity];[certifications];[material];[process];[machine][available-from];[available-to]
	 * This csv can be parsed by the SemanticMatching.java as to represent SupplierResourceRecords populated in sets (not OWL representation)
	 * @param numResourceRecords number of SupplierResourceRecords. 
	 * @param ontoFile the manusquare ontology
	 * @param inputCSVFile input csv file holding supplier id, supplier name, supplier city, supplier country and RFQ response time.
	 * @param outputCSVFile the csv returned from the createSETData operation
	 * @param materialScope the top node of material concepts to include from the ontology
	 * @param processScope the top node of process concepts to include from the ontology
	 * @param machineScope the top node of machine concepts to include from the ontology
	 * @throws OWLOntologyCreationException
	 * @throws IOException
	   Jul 2, 2019
	 */
	public static void createSETData(int numResourceRecords, File ontoFile, String inputCSVFile, String outputCSVFile, String materialScope, String processScope, String machineScope) throws OWLOntologyCreationException, IOException {

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology onto = manager.loadOntologyFromOntologyDocument(ontoFile);	

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

		List<Supplier> suppliers = createSupplierData(inputCSVFile);

		List<String> materials = retrieveMaterials(materialScope, onto);

		//create the correct process to machine combinations
		List<String> machines = retrieveMachines(machineScope, onto);
		List<String> processes = retrieveProcesses(processScope, onto);
		Map<String, String> combos = createProcessMachineCombination(processes, machines);

		//how many combinations per supplier is determined by random				
		List<Resource> resourceList = new LinkedList<Resource>();

		for (int j = 0;  j < numResourceRecords; j++) {
			// Get a random entry from the HashMap.
			Object[] keys = combos.keySet().toArray();
			Object key = keys[new Random().nextInt(keys.length)];

			Object[] datesKey = dates.keySet().toArray();
			Object dateKey = datesKey[new Random().nextInt(datesKey.length)];
			resourceList.add(new Resource(getRandomSupplierData1(suppliers), StringUtilities.getRandomInt1(capacity), StringUtilities.getRandomString1(materials), (String)key, (String)combos.get(key), (String)dateKey, (String)dates.get(dateKey)));
		}

		PrintWriter writer = new PrintWriter(outputCSVFile, "UTF-8");    

		System.out.println("Printing resources ( " + resourceList.size() + " )");
		for (Resource res : resourceList) {
			writer.println(res.getSupplier().getId() + ";" + res.getSupplier().getSupplierName() + ";" + res.getSupplier().getSupplierCity() + ";" + res.getSupplier().getSupplierNationality() + ";" + res.getSupplier().getRfqResponseTime() + ";" + res.getCapacity() + ";" + printCertifications(res.getSupplier().getCertifications()) + ";" + res.getMaterial() + ";" + res.getProcess() + ";" + res.getMachine() + ";" + res.getAvailableFrom() + ";" + res.getAvailableTo());
		}

		writer.flush();  
		writer.close();  

	}

	/**
	 * Creates a csv format of [supplier id];[supplier name];[supplier city];[supplier country];[RFQ response time];[capacity];[certifications];[material];[process];[machine][available-from];[available-to]
	 * This csv can be parsed by the CSV2OWL.java as to represent ProcessChains populated in the manusquare OWL ontology.
	 * @param numResourceRecords number of SupplierResourceRecords. 
	 * @param ontoFile the manusquare ontology
	 * @param inputCSVFile input csv file holding supplier id, supplier name, supplier city, supplier country and RFQ response time.
	 * @param outputCSVFile the csv returned from the createSETData operation
	 * @param materialScope the top node of material concepts to include from the ontology
	 * @param processScope the top node of process concepts to include from the ontology
	 * @param machineScope the top node of machine concepts to include from the ontology
	 * @throws OWLOntologyCreationException
	 * @throws IOException
	   Jul 2, 2019
	 */
	public static void createOWLData(int numResourceRecords, File ontoFile, String inputCSVFile, String outputCSVFile, String materialScope, String processScope, String machineScope) throws OWLOntologyCreationException, IOException {

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology onto = manager.loadOntologyFromOntologyDocument(ontoFile);	

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

		List<Supplier> suppliers = createSupplierData(inputCSVFile);

		List<String> materials = retrieveMaterials(materialScope, onto);

		//create the correct process to machine combinations
		List<String> machines = retrieveMachines(machineScope, onto);
		List<String> processes = retrieveProcesses(processScope, onto);
		Map<String, String> combos = createProcessMachineCombination(processes, machines);

		//how many combinations per supplier is determined by random				
		List<Resource> resourceList = new LinkedList<Resource>();

		for (int j = 0;  j < numResourceRecords; j++) {
			// Get a random entry from the HashMap.
			Object[] keys = combos.keySet().toArray();
			Object key = keys[new Random().nextInt(keys.length)];

			Object[] datesKey = dates.keySet().toArray();
			Object dateKey = datesKey[new Random().nextInt(datesKey.length)];
			resourceList.add(new Resource(getRandomSupplierData1(suppliers), StringUtilities.getRandomInt1(capacity), StringUtilities.getRandomString1(materials), (String)key, (String)combos.get(key), (String)dateKey, (String)dates.get(dateKey)));
		}

		PrintWriter writer = new PrintWriter(outputCSVFile, "UTF-8");    

		System.out.println("Printing resources ( " + resourceList.size() + " )");
		int processChain = 0;
		for (Resource res : resourceList) {
			processChain++;
			
			writer.println("PC_" + processChain + ";" + res.getSupplier().getId() + ";" + res.getSupplier().getSupplierName() + ";" + res.getSupplier().getSupplierCity() + ";" + res.getSupplier().getSupplierNationality() + ";" + res.getSupplier().getRfqResponseTime() + ";" + res.getCapacity() + ";" + printCertifications(res.getSupplier().getCertifications()) + ";" + res.getMaterial() + ";" + res.getProcess() + ";" + res.getMachine() + ";" + res.getAvailableFrom() + ";" + res.getAvailableTo());
			System.out.println("PC_" + processChain + ";" + res.getSupplier().getId() + ";" + res.getSupplier().getSupplierName() + ";" + res.getSupplier().getSupplierCity() + ";" + res.getSupplier().getSupplierNationality() + ";" + res.getSupplier().getRfqResponseTime() + ";" + res.getCapacity() + ";" + printCertifications(res.getSupplier().getCertifications()) + ";" + res.getMaterial() + ";" + res.getProcess() + ";" + res.getMachine() + ";" + res.getAvailableFrom() + ";" + res.getAvailableTo());
		}

		writer.flush();  
		writer.close();  
		
	}

	
	/**
	 * Creates a list of "static" supplier data + selects a random number (max 3) of certifications for each supplier.
	 * @param csvFilePath the input csv file holding supplier data (id, supplier name, supplier city, supplier country and RFQ response time (and CAD type which is not used at the moment)
	 * @return
	 * @throws IOException
	   Jul 2, 2019
	 */
	public static List<Supplier> createSupplierData(String csvFilePath) throws IOException {

		List<Supplier> supplierData = new LinkedList<Supplier>();

		Supplier supplier;

		BufferedReader br = new BufferedReader(new FileReader(csvFilePath));

		String line = br.readLine();

		String[] params = null;

		while (line != null) {
			params = line.split(";");

			supplier = new Supplier();

			supplier.setId(params[0]);
			supplier.setSupplierName(params[1]);
			supplier.setSupplierNationality(params[2]);
			supplier.setSupplierCity(params[3]);
			supplier.setRfqResponseTime(Integer.parseInt(params[4]));
			supplier.setCadType(params[5]);


			//get random certification from list (i.e. the ontology concepts representing certifications in the manusquare ontology)
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

			supplier.setCertifications(StringUtilities.getRandomString3(certifications));			

			supplierData.add(supplier);
			line = br.readLine();

		}

		br.close();

		return supplierData;

	}

	

	/**
	 * Retrieves machine concepts from the ontology starting from className as top node
	 * @param className
	 * @param onto
	 * @return
	   Jul 2, 2019
	 */
	public static List<String> retrieveMachines(String className, OWLOntology onto) {

		//get all OWL classes representing machines being subclasses to className
		OWLClass c = OntologyOperations.getClass(className, onto);
		Set<String> subClasses = OntologyOperations.getAllEntitySubclassesFragments(onto, c);

		List<String> machines = new LinkedList<String>();

		for (String s : subClasses) {
			machines.add(s);
		}

		return machines;

	}

	/**
	 * Retrieves process concepts from the ontology starting from className as top node
	 * @param className
	 * @param onto
	 * @return
	   Jul 2, 2019
	 */
	public static List<String> retrieveProcesses(String className, OWLOntology onto) {

		//get all OWL classes representing processes being subclasses to className
		OWLClass c = OntologyOperations.getClass(className, onto);
		Set<String> subClasses = OntologyOperations.getAllEntitySubclassesFragments(onto, c);

		List<String> processes = new LinkedList<String>();

		for (String s : subClasses) {
			processes.add(s);
		}

		return processes;

	}

	/**
	 * Retrieves material concepts from the ontology starting from className as top node
	 * @param className
	 * @param onto
	 * @return
	   Jul 2, 2019
	 */
	public static List<String> retrieveMaterials(String className, OWLOntology onto) {

		//get all OWL classes representing materials being subclasses to className
		OWLClass c = OntologyOperations.getClass(className, onto);
		Set<String> subClasses = OntologyOperations.getAllEntitySubclassesFragments(onto, c);

		List<String> materials = new LinkedList<String>();

		for (String s : subClasses) {
			materials.add(s);
		}

		return materials;

	}
	
	/**
	 * Retrieves output (products) from the ontology starting from className as top node
	 * @param className
	 * @param onto
	 * @return
	   Jul 2, 2019
	 */
	public static List<String> retrieveProducts(String className, OWLOntology onto) {

		//get all OWL classes representing machines being subclasses to className
		OWLClass c = OntologyOperations.getClass(className, onto);
		Set<String> subClasses = OntologyOperations.getAllEntitySubclassesFragments(onto, c);

		List<String> products = new LinkedList<String>();

		for (String s : subClasses) {
			products.add(s);
		}

		return products;

	}
	
	/**
	 * Retrieves human capabilities (Engineering Services) from the ontology starting from className as top node
	 * @param className
	 * @param onto
	 * @return
	   Jul 2, 2019
	 */
	public static List<String> retrieveHumanCapabilities(String className, OWLOntology onto) {

		//get all OWL classes representing machines being subclasses to className
		OWLClass c = OntologyOperations.getClass(className, onto);
		Set<String> subClasses = OntologyOperations.getAllEntitySubclassesFragments(onto, c);

		List<String> products = new LinkedList<String>();

		for (String s : subClasses) {
			products.add(s);
		}

		return products;

	}
	

	/**
	 * Creates a combination of process and machines that is relevant (well...)
	 * @param processes
	 * @param machines
	 * @return
	   Jul 2, 2019
	 */
	public static Map<String, String> createProcessMachineCombination (List<String> processes, List<String> machines) {
		

		Map<String, String> processAndMachinesMap = new HashMap<String, String>();

		for (String process : processes) {
			for (String machine : machines) {
				if (process.equalsIgnoreCase("WaterJetCutting")) {
					if (machine.equalsIgnoreCase("WaterJetCuttingMachine")) {

						processAndMachinesMap.put(process, machine);

					}
				} else if (process.equalsIgnoreCase("GearHobbing")) {
					if (machine.equalsIgnoreCase("GearShaperMachine")) {

						processAndMachinesMap.put(process, machine);

					}
				} else if (process.equalsIgnoreCase("HoleMaking")) {
					if (machine.equalsIgnoreCase("EDMDrillingMachine")) {

						processAndMachinesMap.put(process, machine);

					}
				} else if (process.equalsIgnoreCase("Milling")) {
					if (machine.equalsIgnoreCase("MillingMachine")) {

						processAndMachinesMap.put(process, machine);

					}
				} else if (process.equalsIgnoreCase("CNCMilling")) {
					if (machine.equalsIgnoreCase("ComputerControlledMachineTool")) {

						processAndMachinesMap.put(process, machine);

					}
				} else if (process.equalsIgnoreCase("HorizontalMilling")) {
					if (machine.equalsIgnoreCase("HorizontalMillingMachine")) {

						processAndMachinesMap.put(process, machine);

					}
				} else if (process.equalsIgnoreCase("ManualMilling")) {
					if (machine.equalsIgnoreCase("ManualMachineTool")) {

						processAndMachinesMap.put(process, machine);

					}
				} else if (process.equalsIgnoreCase("PrecisionMilling")) {
					if (machine.equalsIgnoreCase("ComputerControlledMachineTool")) {

						processAndMachinesMap.put(process, machine);

					}
				} else if (process.equalsIgnoreCase("VerticalMilling")) {
					if (machine.equalsIgnoreCase("VerticalMillingMachine")) {

						processAndMachinesMap.put(process, machine);

					}
				} else if (process.equalsIgnoreCase("Planing")) {
					if (machine.equalsIgnoreCase("ShaperMachine")) {

						processAndMachinesMap.put(process, machine);

					}
				} else if (process.equalsIgnoreCase("Shaping")) {
					if (machine.equalsIgnoreCase("ShaperMachine")) {

						processAndMachinesMap.put(process, machine);

					}
				} else if (process.equalsIgnoreCase("CNCTurning")) {
					if (machine.equalsIgnoreCase("CNCTurnMachine")) {

						processAndMachinesMap.put(process, machine);

					}
				} else if (process.equalsIgnoreCase("ElectricalDischargeMachining")) {
					if (machine.equalsIgnoreCase("EDMMachine")) {

						processAndMachinesMap.put(process, machine);

					}
				} else if (process.equalsIgnoreCase("SinkerEDM")) {
					if (machine.equalsIgnoreCase("EDMMachine")) {

						processAndMachinesMap.put(process, machine);

					}
				} else if (process.equalsIgnoreCase("WireEDM")) {
					if (machine.equalsIgnoreCase("EDMMachine")) {

						processAndMachinesMap.put(process, machine);

					}
				} else if (process.equalsIgnoreCase("LaserBeamCutting")) {
					if (machine.equalsIgnoreCase("LaserCuttingMachine")) {

						processAndMachinesMap.put(process, machine);

					}
				}

			}
		}

		return processAndMachinesMap;

	}


	/**
	 * Returns random supplier data record from a list of supplier data
	 * @param list
	 * @return
	   Jul 2, 2019
	 */
	private static Supplier getRandomSupplierData1(List<Supplier> list) {
		Random rand = new Random();
		Supplier returnedSupplierData = null;

		int numberOfElements = 1;

		for (int i = 0; i < numberOfElements; i++) {
			int randomIndex = rand.nextInt(list.size());
			returnedSupplierData = list.get(randomIndex);

		}

		return returnedSupplierData;
	}

	/**
	 * Ensures that certifications are represented properly in the csv
	 * @param certifications
	 * @return
	   Jul 2, 2019
	 */
	public static String printCertifications(Set<String> certifications) {
		StringBuffer sb = new StringBuffer();
		for (String s : certifications) {
			sb.append(s + ",");
		}

		String certificationsString = sb.deleteCharAt(sb.lastIndexOf(",")).toString();

		return certificationsString;

	}


}
