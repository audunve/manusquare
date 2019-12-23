package testdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.AutoIRIMapper;

import owlprocessing.OntologyOperations;
import utilities.StringUtilities;

/**
 * Reads instance data from CSV and combined with randomly retrieved ontology concepts and adds these data as individuals in the Manusquare Industrial OWL ontology.
 * The data are added in a process chain structure inspired by the example from section 5.2.3 in D2.4.
 * Two different CSV files are used as input: DummyData_10.csv (creating 10 different process chains) and DummyData_200.csv (creating 200 different process chains)
 * The following data are in the CSV: ProcessChainId, ProcessChainName, StakeholderId, StakeholderName.
 * The following data are retrieved randomly from the ontology: Processes, Materials, Machines, Products, Human Capabilities
 * The data can be exported as RDF/XML and imported in a GraphDB / RDF4J knowledge base.
 * @author audunvennesland
 *
 */
public class TestDataGeneratorSimpleCase {

	String processChainId;
	String processChainName;
	String supplierId;
	String supplierName;
	Set<String> certifications;
	String process_1;
	String inputMaterial_1_1;
	String inputMaterial_1_2;
	String outputMaterial_1_1;
	String outputMaterial_1_1_Quantity;
	String outputMaterial_1_1_Measure;
	String outputMaterial_1_2;
	String outputMaterial_1_2_Quantity;
	String outputMaterial_1_2_Measure;
	String resource_1_1;
	String resource_1_1_Capability;
	String resource_1_1_Capability_AttributeType;
	String resource_1_1_Capability_AttributeValue;
	String resource_1_1_Capability_AttributeMeasure;
	String resource_1_2;
	String resource_1_2_Capability;
	String process_2;
	String inputMaterial_2_1;


	public TestDataGeneratorSimpleCase(String processChainId, String processChainName, String supplierId, String supplierName, Set<String> certifications, String process_1,
			String inputMaterial_1_1, String inputMaterial_1_2, 
			String process_2, String inputMaterial_2_1) {
		super();
		this.processChainId = processChainId;
		this.processChainName = processChainName;
		this.supplierId = supplierId;
		this.supplierName = supplierName;
		this.certifications = certifications;
		this.process_1 = process_1;
		this.inputMaterial_1_1 = inputMaterial_1_1;
		this.inputMaterial_1_2 = inputMaterial_1_2;
		this.process_2 = process_2;
		this.inputMaterial_2_1 = inputMaterial_2_1;
	}



	public TestDataGeneratorSimpleCase() {}
	
	public static void main(String[] args) throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {

		TestDataGenerator data;

		BufferedReader br = new BufferedReader(new FileReader("./files/TESTDATA/DummyData_5000.csv"));

		String line = br.readLine();

		String[] params = null;

		Set<TestDataGenerator> dataset = new HashSet<TestDataGenerator>();

		while (line != null) {
			params = line.split(";");

			data = new TestDataGenerator();
			data.setProcessChainId(params[0]);
			data.setProcessChainName(params[1]);
			data.setSupplierId(params[2]);
			data.setSupplierName(params[3]);

			dataset.add(data);
			line = br.readLine();

		}

		br.close();


		//import manusquare ontology
		File ontoFile = new File("./files/ONTOLOGIES/manusquare-industrial.owl");

		String processScope = "SubtractionProcess";
		String materialScope = "Ferrous";

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		//point to a local folder containing local copies of ontologies to sort out the imports
		AutoIRIMapper mapper=new AutoIRIMapper(new File("./files/ONTOLOGIES"), true);
		manager.addIRIMapper(mapper);

		OWLOntology onto = manager.loadOntologyFromOntologyDocument(ontoFile);
		System.out.println("The ontology contains " + onto.getClassesInSignature().size() + " classes");

		OWLClass processChainClass = getClass("ProcessChain", onto);
		OWLClass supplierClass = getClass("Supplier", onto);


		OWLDataFactory df = manager.getOWLDataFactory();
		OWLClass materialTypeClass = null;
		OWLClass processTypeClass = null;
		OWLClass processType2Class = null;
		OWLClass attributeTypeClass = null;

		OWLIndividual processChainInd = null;
		OWLIndividual supplierInd = null;
		OWLIndividual process1Ind = null;
		OWLIndividual process2Ind = null;
		OWLIndividual material_input_1_Ind = null;
		OWLIndividual material_input_1_Ind_attribute = null;
		OWLIndividual material_input_2_Ind = null;
		OWLIndividual material_input_2_Ind_attribute = null;
		OWLIndividual material_input_3_Ind = null;
		OWLIndividual material_input_3_Ind_attribute = null;
		OWLIndividual certificationInd = null;	
		OWLIndividual attributeInd = null;
		
		OWLAxiom classAssertionAxiom = null; 
		OWLAxiom OPAssertionAxiom = null; 
		OWLAxiom DPAssertionAxiom = null; 

		AddAxiom addAxiomChange = null;

		int iterator = 0;
		
		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();

		//adding process chain
		for (TestDataGenerator td : dataset) {
			iterator+=1;	
			
			

			//adding process chain individual
			processChainInd = df.getOWLNamedIndividual(IRI.create(onto.getOntologyID().getOntologyIRI() + "#" + td.getProcessChainId()));
			classAssertionAxiom = df.getOWLClassAssertionAxiom(processChainClass, processChainInd);			
			addAxiomChange = new AddAxiom(onto, classAssertionAxiom);		
			//manager.applyChange(addAxiomChange);
			changes.add(addAxiomChange);

			//adding supplier individual
			supplierInd = df.getOWLNamedIndividual(IRI.create(onto.getOntologyID().getOntologyIRI() + "#" + td.getSupplierId().replaceAll(",", "_").replaceAll(" ", "_")));
			classAssertionAxiom = df.getOWLClassAssertionAxiom(supplierClass, supplierInd);			
			addAxiomChange = new AddAxiom(onto, classAssertionAxiom);
			//manager.applyChange(addAxiomChange);
			changes.add(addAxiomChange);

			//adding process
			List<String> processes = retrieveProcesses(processScope, onto);
			String process = StringUtilities.getRandomString1(processes);
			process1Ind = df.getOWLNamedIndividual(IRI.create(onto.getOntologyID().getOntologyIRI() + "#IND_PROCESS_" + process + "_" + iterator));
			processTypeClass = getClass(process, onto);

			classAssertionAxiom = df.getOWLClassAssertionAxiom(processTypeClass, process1Ind);			
			addAxiomChange = new AddAxiom(onto, classAssertionAxiom);
			//manager.applyChange(addAxiomChange);	
			changes.add(addAxiomChange);

			//add input material 1
			List<String> materials = retrieveMaterials(materialScope, onto);
			String material = StringUtilities.getRandomString1(materials);
			material_input_1_Ind = df.getOWLNamedIndividual(IRI.create(onto.getOntologyID().getOntologyIRI() + "#IND_MATERIAL_" + material + "_" + iterator));
			materialTypeClass = getClass(material, onto);
			classAssertionAxiom = df.getOWLClassAssertionAxiom(materialTypeClass, material_input_1_Ind);			
			addAxiomChange = new AddAxiom(onto, classAssertionAxiom);
			//manager.applyChange(addAxiomChange);
			changes.add(addAxiomChange);
			
			//add input material 1 attribute
			material_input_1_Ind_attribute = df.getOWLNamedIndividual(IRI.create(onto.getOntologyID().getOntologyIRI() + "#IND_MATERIAL_ATTRIBUTE" + material + "_" + iterator));
			attributeTypeClass = getClass("AttributeType", onto);
			classAssertionAxiom = df.getOWLClassAssertionAxiom(attributeTypeClass, material_input_1_Ind_attribute);			
			addAxiomChange = new AddAxiom(onto, classAssertionAxiom);
			//manager.applyChange(addAxiomChange);
			changes.add(addAxiomChange);

			//add input material 2
			materials = retrieveMaterials(materialScope, onto);
			material = StringUtilities.getRandomString1(materials);
			material_input_2_Ind = df.getOWLNamedIndividual(IRI.create(onto.getOntologyID().getOntologyIRI() + "#IND_MATERIAL_" + material + "_" + iterator));
			materialTypeClass = getClass(material, onto);
			classAssertionAxiom = df.getOWLClassAssertionAxiom(materialTypeClass, material_input_2_Ind);			
			addAxiomChange = new AddAxiom(onto, classAssertionAxiom);
			//manager.applyChange(addAxiomChange);
			changes.add(addAxiomChange);

			//add input material 2 attribute
			material_input_2_Ind_attribute = df.getOWLNamedIndividual(IRI.create(onto.getOntologyID().getOntologyIRI() + "#IND_MATERIAL_ATTRIBUTE" + material + "_" + iterator));
			attributeTypeClass = getClass("AttributeType", onto);
			classAssertionAxiom = df.getOWLClassAssertionAxiom(attributeTypeClass, material_input_2_Ind_attribute);			
			addAxiomChange = new AddAxiom(onto, classAssertionAxiom);
			//manager.applyChange(addAxiomChange);
			changes.add(addAxiomChange);

			//process 2
			List<String> processes2 = retrieveProcesses(processScope, onto);
			String process2 = StringUtilities.getRandomString1(processes2);
			process2Ind = df.getOWLNamedIndividual(IRI.create(onto.getOntologyID().getOntologyIRI() + "#IND_PROCESS_" + process2 + "_" + iterator));
			processType2Class = getClass(process2, onto);

			classAssertionAxiom = df.getOWLClassAssertionAxiom(processType2Class, process2Ind);			
			addAxiomChange = new AddAxiom(onto, classAssertionAxiom);
			//manager.applyChange(addAxiomChange);
			changes.add(addAxiomChange);

			//input material process 2
			materials = retrieveMaterials(materialScope, onto);
			material = StringUtilities.getRandomString1(materials);
			material_input_3_Ind = df.getOWLNamedIndividual(IRI.create(onto.getOntologyID().getOntologyIRI() + "#IND_MATERIAL_" + material + "_" + iterator));
			materialTypeClass = getClass(material, onto);
			classAssertionAxiom = df.getOWLClassAssertionAxiom(materialTypeClass, material_input_3_Ind);			
			addAxiomChange = new AddAxiom(onto, classAssertionAxiom);
			//manager.applyChange(addAxiomChange);
			changes.add(addAxiomChange);
			
			//add input material 3 attribute
			material_input_3_Ind_attribute = df.getOWLNamedIndividual(IRI.create(onto.getOntologyID().getOntologyIRI() + "#IND_MATERIAL_ATTRIBUTE" + material + "_" + iterator));
			attributeTypeClass = getClass("AttributeType", onto);
			classAssertionAxiom = df.getOWLClassAssertionAxiom(attributeTypeClass, material_input_3_Ind_attribute);			
			addAxiomChange = new AddAxiom(onto, classAssertionAxiom);
			//manager.applyChange(addAxiomChange);		
			changes.add(addAxiomChange);
			


			//add certifications to supplier
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

			Set<String> certificationsSet = StringUtilities.getRandomString3(certifications);

			Set<OWLClass> certificationClasses = new HashSet<OWLClass>();
			Set<OWLIndividual> certificationInds = new HashSet<OWLIndividual>();

			for (String s : certificationsSet) {
				certificationClasses.add(getClass(s, onto));
			}

			//add certifications
			for (OWLClass c : certificationClasses) {

				certificationInd = df.getOWLNamedIndividual(IRI.create(onto.getOntologyID().getOntologyIRI() + "#" + c.getIRI().getFragment() + "_" + td.getSupplierId().replaceAll(",", "_").replaceAll(" ", "_")));
				//add individual certifications to the set of certification individuals for this supplier
				certificationInds.add(certificationInd);
				classAssertionAxiom = df.getOWLClassAssertionAxiom(c, certificationInd);	
				addAxiomChange = new AddAxiom(onto, classAssertionAxiom);
				//manager.applyChange(addAxiomChange);
				changes.add(addAxiomChange);
			}

			//OP hasSupplier from processChainInd to supplierInd
			OPAssertionAxiom = df.getOWLObjectPropertyAssertionAxiom(OntologyOperations.getObjectProperty("hasSupplier", onto), processChainInd, supplierInd);
			addAxiomChange = new AddAxiom(onto, OPAssertionAxiom);
			//manager.applyChange(addAxiomChange);
			changes.add(addAxiomChange);

			//OP hasProcess from processChainInd to process1Ind
			OPAssertionAxiom = df.getOWLObjectPropertyAssertionAxiom(OntologyOperations.getObjectProperty("hasProcess", onto), processChainInd, process1Ind);
			addAxiomChange = new AddAxiom(onto, OPAssertionAxiom);
			//manager.applyChange(addAxiomChange);
			changes.add(addAxiomChange);
			
			//OP hasAttribute from process1Ind to material_input_1_Ind_attribute
			OPAssertionAxiom = df.getOWLObjectPropertyAssertionAxiom(OntologyOperations.getObjectProperty("hasAttribute", onto), process1Ind, material_input_1_Ind_attribute);
			addAxiomChange = new AddAxiom(onto, OPAssertionAxiom);
			//manager.applyChange(addAxiomChange);
			changes.add(addAxiomChange);
			
			//OP hasAttribute from material_input_1_Ind_attribute to material_input_1_Ind
			OPAssertionAxiom = df.getOWLObjectPropertyAssertionAxiom(OntologyOperations.getObjectProperty("hasValue", onto), material_input_1_Ind_attribute, material_input_1_Ind);
			addAxiomChange = new AddAxiom(onto, OPAssertionAxiom);
			//manager.applyChange(addAxiomChange);
			changes.add(addAxiomChange);
			
			//OP hasAttribute from process1Ind to material_input_2_Ind_attribute
			OPAssertionAxiom = df.getOWLObjectPropertyAssertionAxiom(OntologyOperations.getObjectProperty("hasAttribute", onto), process1Ind, material_input_2_Ind_attribute);
			addAxiomChange = new AddAxiom(onto, OPAssertionAxiom);
			manager.applyChange(addAxiomChange);
			
			//OP hasAttribute from material_input_2_Ind_attribute to material_input_2_Ind
			OPAssertionAxiom = df.getOWLObjectPropertyAssertionAxiom(OntologyOperations.getObjectProperty("hasValue", onto), material_input_2_Ind_attribute, material_input_2_Ind);
			addAxiomChange = new AddAxiom(onto, OPAssertionAxiom);
			//manager.applyChange(addAxiomChange);
			changes.add(addAxiomChange);
			
			//OP hasAttribute from process1Ind to material_input_2_Ind_attribute
			OPAssertionAxiom = df.getOWLObjectPropertyAssertionAxiom(OntologyOperations.getObjectProperty("hasAttribute", onto), process2Ind, material_input_3_Ind_attribute);
			addAxiomChange = new AddAxiom(onto, OPAssertionAxiom);
			//manager.applyChange(addAxiomChange);
			changes.add(addAxiomChange);
			
			//OP hasAttribute from material_input_2_Ind_attribute to material_input_2_Ind
			OPAssertionAxiom = df.getOWLObjectPropertyAssertionAxiom(OntologyOperations.getObjectProperty("hasValue", onto), material_input_3_Ind_attribute, material_input_3_Ind);
			addAxiomChange = new AddAxiom(onto, OPAssertionAxiom);
			//manager.applyChange(addAxiomChange);
			changes.add(addAxiomChange);
			
			//OP hasProcess from processChainInd to process2Ind
			OPAssertionAxiom = df.getOWLObjectPropertyAssertionAxiom(OntologyOperations.getObjectProperty("hasProcess", onto), processChainInd, process2Ind);
			addAxiomChange = new AddAxiom(onto, OPAssertionAxiom);
			//manager.applyChange(addAxiomChange);
			changes.add(addAxiomChange);


			//OP hasCertification from supplierId to certificationInd
			for (OWLIndividual ind : certificationInds) {
				OPAssertionAxiom = df.getOWLObjectPropertyAssertionAxiom(OntologyOperations.getObjectProperty("hasCertification", onto), supplierInd, ind);
				addAxiomChange = new AddAxiom(onto, OPAssertionAxiom);
				//manager.applyChange(addAxiomChange);
				changes.add(addAxiomChange);
			}

			//DP for expressing process chain name and id
			DPAssertionAxiom = df.getOWLDataPropertyAssertionAxiom(OntologyOperations.getDataProperty("hasName", onto), processChainInd, df.getOWLLiteral(td.getProcessChainName().replaceAll(",", "_")));
			addAxiomChange = new AddAxiom(onto, DPAssertionAxiom);
			//manager.applyChange(addAxiomChange);
			changes.add(addAxiomChange);
			
			DPAssertionAxiom = df.getOWLDataPropertyAssertionAxiom(OntologyOperations.getDataProperty("hasId", onto), processChainInd, df.getOWLLiteral(td.getProcessChainId().replaceAll(",", "_")));
			addAxiomChange = new AddAxiom(onto, DPAssertionAxiom);
			//manager.applyChange(addAxiomChange);
			changes.add(addAxiomChange);
			
			//DPs for expressing supplier name and id
			DPAssertionAxiom = df.getOWLDataPropertyAssertionAxiom(OntologyOperations.getDataProperty("hasName", onto), supplierInd, df.getOWLLiteral(td.getSupplierName().replaceAll(",", "_")));
			addAxiomChange = new AddAxiom(onto, DPAssertionAxiom);
			//manager.applyChange(addAxiomChange);
			changes.add(addAxiomChange);
			
			DPAssertionAxiom = df.getOWLDataPropertyAssertionAxiom(OntologyOperations.getDataProperty("hasId", onto), supplierInd, df.getOWLLiteral(td.getSupplierId().replaceAll(",", "_")));
			addAxiomChange = new AddAxiom(onto, DPAssertionAxiom);
			changes.add(addAxiomChange);
			

			
			//manager.applyChange(addAxiomChange);
			
			


		}
		
		System.out.println("Applying " + changes.size() + " changes");
		
		manager.applyChanges(changes);
		
		//save the ontology in each iteration
		System.out.println("Saving ontology");
		manager.saveOntology(onto);
	}


	public String getSupplierName() {
		return supplierName;
	}



	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}



	public Set<String> getCertifications() {
		return certifications;
	}



	public void setCertifications(Set<String> certifications) {
		this.certifications = certifications;
	}



	public String getProcessChainId() {
		return processChainId;
	}



	public void setProcessChainId(String processChainId) {
		this.processChainId = processChainId;
	}



	public String getProcessChainName() {
		return processChainName;
	}



	public void setProcessChainName(String processChainName) {
		this.processChainName = processChainName;
	}



	public String getSupplierId() {
		return supplierId;
	}



	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}



	public String getProcess_1() {
		return process_1;
	}



	public void setProcess_1(String process_1) {
		this.process_1 = process_1;
	}



	public String getInputMaterial_1_1() {
		return inputMaterial_1_1;
	}



	public void setInputMaterial_1_1(String inputMaterial_1_1) {
		this.inputMaterial_1_1 = inputMaterial_1_1;
	}



	public String getInputMaterial_1_2() {
		return inputMaterial_1_2;
	}



	public void setInputMaterial_1_2(String inputMaterial_1_2) {
		this.inputMaterial_1_2 = inputMaterial_1_2;
	}



	public String getOutputMaterial_1_1() {
		return outputMaterial_1_1;
	}



	public void setOutputMaterial_1_1(String outputMaterial_1_1) {
		this.outputMaterial_1_1 = outputMaterial_1_1;
	}



	public String getOutputMaterial_1_1_Quantity() {
		return outputMaterial_1_1_Quantity;
	}



	public void setOutputMaterial_1_1_Quantity(String outputMaterial_1_1_Quantity) {
		this.outputMaterial_1_1_Quantity = outputMaterial_1_1_Quantity;
	}



	public String getOutputMaterial_1_1_Measure() {
		return outputMaterial_1_1_Measure;
	}



	public void setOutputMaterial_1_1_Measure(String outputMaterial_1_1_Measure) {
		this.outputMaterial_1_1_Measure = outputMaterial_1_1_Measure;
	}



	public String getOutputMaterial_1_2() {
		return outputMaterial_1_2;
	}



	public void setOutputMaterial_1_2(String outputMaterial_1_2) {
		this.outputMaterial_1_2 = outputMaterial_1_2;
	}



	public String getOutputMaterial_1_2_Quantity() {
		return outputMaterial_1_2_Quantity;
	}



	public void setOutputMaterial_1_2_Quantity(String outputMaterial_1_2_Quantity) {
		this.outputMaterial_1_2_Quantity = outputMaterial_1_2_Quantity;
	}



	public String getOutputMaterial_1_2_Measure() {
		return outputMaterial_1_2_Measure;
	}



	public void setOutputMaterial_1_2_Measure(String outputMaterial_1_2_Measure) {
		this.outputMaterial_1_2_Measure = outputMaterial_1_2_Measure;
	}



	public String getResource_1_1() {
		return resource_1_1;
	}



	public void setResource_1_1(String resource_1_1) {
		this.resource_1_1 = resource_1_1;
	}



	public String getResource_1_1_Capability() {
		return resource_1_1_Capability;
	}



	public void setResource_1_1_Capability(String resource_1_1_Capability) {
		this.resource_1_1_Capability = resource_1_1_Capability;
	}



	public String getResource_1_1_Capability_AttributeType() {
		return resource_1_1_Capability_AttributeType;
	}



	public void setResource_1_1_Capability_AttributeType(String resource_1_1_Capability_AttributeType) {
		this.resource_1_1_Capability_AttributeType = resource_1_1_Capability_AttributeType;
	}



	public String getResource_1_1_Capability_AttributeValue() {
		return resource_1_1_Capability_AttributeValue;
	}



	public void setResource_1_1_Capability_AttributeValue(String resource_1_1_Capability_AttributeValue) {
		this.resource_1_1_Capability_AttributeValue = resource_1_1_Capability_AttributeValue;
	}



	public String getResource_1_1_Capability_AttributeMeasure() {
		return resource_1_1_Capability_AttributeMeasure;
	}



	public void setResource_1_1_Capability_AttributeMeasure(String resource_1_1_Capability_AttributeMeasure) {
		this.resource_1_1_Capability_AttributeMeasure = resource_1_1_Capability_AttributeMeasure;
	}



	public String getResource_1_2() {
		return resource_1_2;
	}



	public void setResource_1_2(String resource_1_2) {
		this.resource_1_2 = resource_1_2;
	}



	public String getResource_1_2_Capability() {
		return resource_1_2_Capability;
	}



	public void setResource_1_2_Capability(String resource_1_2_Capability) {
		this.resource_1_2_Capability = resource_1_2_Capability;
	}



	public String getProcess_2() {
		return process_2;
	}



	public void setProcess_2(String process_2) {
		this.process_2 = process_2;
	}



	public String getInputMaterial_2_1() {
		return inputMaterial_2_1;
	}



	public void setInputMaterial_2_1(String inputMaterial_2_1) {
		this.inputMaterial_2_1 = inputMaterial_2_1;
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
	 * Retrieves an OWLClass from its class name represented as a string
	 * @param className
	 * @param ontology
	 * @return
	 */
	private static OWLClass getClass(String className, OWLOntology ontology) {

		OWLClass relevantClass = null;

		Set<OWLClass> classes = ontology.getClassesInSignature();

		for (OWLClass cls : classes) {
			if (cls.getIRI().getFragment().equals(className)) {
				relevantClass = cls;
				break;
			} else {
				relevantClass = null;
			}
		}

		return relevantClass;


	}

	//2002-05-30T09:00:00
	private static String convertToDateTime(String input) {

		return input.replaceAll(" ", "") + "T00:00:00";
	}





}
