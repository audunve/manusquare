package importtestdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.AutoIRIMapper;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import owlprocessing.OntologyOperations;

/**
 * Reads instance data from CSV and adds these data as individuals in the Manusquare Industrial OWL ontology.
 * A supplier resource (model) is represented as a process chain (PC_x) with the following statements:
 * PC_x hasSupplier [supplier]
 * * [supplier] hasName [supplier name]
 * * [supplier] hasNation [nationality]
 * * [supplier] hasCity [city]
 * PC_x hasInput [material]
 * PC_x hasProcess [process]
 * PC_x hasMachine [machine] NOTE: Added OP _hasMachine until we know how to link machine with process chain
 * PC_x hasCertification [certification] 1..n NOTE: Added OP _hasCertification until we know how to link certification(s) with process chain (should probably be linked directly to supplier)
 * PC_x hasQuantity [capacity]
 * PC_x hasPeriod [period]
 * * [period] hasFrom [from date]
 * * [period] hasTo [to date]
 * @author audunvennesland
 *
 */
public class CSV2OWL {
	
	String processChain;
	String supplierName;
	String supplierNationality;
	String supplierCity;
	int rfqResponseTime;
	Set<String> certification;
	int capacity;
	String material;
	String process;
	String machine;
	String availableFrom;
	String availableTo;
	String comments;


	public CSV2OWL(String processChain, String supplierName, String supplierNationality, String supplierCity, 
			int rfqResponseTime, Set<String> certification, int capacity, String material, String process, String machine, String availableFrom,
			String availableTo) {
		super();
		this.processChain = processChain;
		this.supplierName = supplierName;
		this.supplierNationality = supplierNationality;
		this.supplierCity = supplierCity;
		this.rfqResponseTime = rfqResponseTime;
		this.certification = certification;
		this.capacity = capacity;
		this.material = material;
		this.process = process;
		this.machine = machine;
		this.availableFrom = availableFrom;
		this.availableTo = availableTo;
	}


	public static void main(String[] args) throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {

		CSV2OWL data;

		BufferedReader br = new BufferedReader(new FileReader("./files/OWL_ResourceRecords.csv"));

		String line = br.readLine();

		String[] params = null;

		Set<CSV2OWL> dataset = new HashSet<CSV2OWL>();

		while (line != null) {
			params = line.split(";");

			data = new CSV2OWL();
			data.setProcessChain(params[0]);
			data.setSupplierName(params[2]);
			data.setSupplierNationality(params[4]);
			data.setSupplierCity(params[3]);

			data.setRfqResponseTime(Integer.parseInt(params[5]));
			data.setCapacity(Integer.parseInt(params[6]));
			
			//get certifications from csv
			Set<String> certifications = new HashSet<String>();

			if (params[7].contains(",")) {
				String[] certs = params[7].split(",");
			for (String c : certs) {
				certifications.add(c);
			}
			} else {
				certifications.add(params[7]);
			}
			
			data.setCertification(certifications);

			data.setMaterial(params[8]);
			data.setProcess(params[9]);
			data.setMachine(params[10]);
			data.setAvailableFrom(params[11]);
			data.setAvailableTo(params[12]);
			
			dataset.add(data);
			line = br.readLine();

		}

		br.close();


		//import manusquare ontology
		File ontoFile = new File("./files/manusquare-industrial.owl");
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		//point to a local folder containing local copies of ontologies to sort out the imports
		AutoIRIMapper mapper=new AutoIRIMapper(new File("./files"), true);
		manager.addIRIMapper(mapper);
		
		OWLOntology onto = manager.loadOntologyFromOntologyDocument(ontoFile);
		System.out.println("The ontology contains " + onto.getClassesInSignature().size() + " classes");
		
		OWLDataFactory df = manager.getOWLDataFactory();
		OWLClass processChainClass = getClass("ProcessChain", onto);
		OWLClass supplierClass = getClass("Supplier", onto);
		OWLClass periodClass = getClass("Period", onto);
		OWLClass materialTypeClass = getClass("MaterialType", onto);
		OWLClass processTypeClass = getClass("ProcessType", onto);
		OWLClass machineTypeClass = getClass("MachineType", onto);
		
		OWLIndividual processChainInd = null;
		OWLIndividual supplierInd = null;
		OWLIndividual materialInd = null;
		OWLIndividual processInd = null;
		OWLIndividual machineInd = null;
		OWLIndividual periodInd = null;
		OWLIndividual certificationInd = null;
		
		
		OWLAxiom classAssertionAxiom = null; 
		OWLAxiom OPAssertionAxiom = null; 
		OWLAxiom DPAssertionAxiom = null; 
		
		AddAxiom addAxiomChange = null;
		
		int iterator = 0;
		
		//adding process chain
		for (CSV2OWL td : dataset) {
			iterator+=1;
			
			//adding process chain
			processChainInd = df.getOWLNamedIndividual(IRI.create(onto.getOntologyID().getOntologyIRI() + "#" + td.getProcessChain()));
			classAssertionAxiom = df.getOWLClassAssertionAxiom(processChainClass, processChainInd);			
			addAxiomChange = new AddAxiom(onto, classAssertionAxiom);		
			DPAssertionAxiom = df.getOWLDataPropertyAssertionAxiom(OntologyOperations.getDataProperty("hasQuantity", onto), processChainInd, df.getOWLLiteral(td.getCapacity()));
			addAxiomChange = new AddAxiom(onto, DPAssertionAxiom);	
			manager.applyChange(addAxiomChange);
			
			//adding process
			processInd = df.getOWLNamedIndividual(IRI.create(onto.getOntologyID().getOntologyIRI() + "#" + td.getProcess().replaceAll(",", "_").replaceAll(" ", "_")));
			classAssertionAxiom = df.getOWLClassAssertionAxiom(processTypeClass, processInd);			
			addAxiomChange = new AddAxiom(onto, classAssertionAxiom);
			manager.applyChange(addAxiomChange);
			
			//adding supplier data
			supplierInd = df.getOWLNamedIndividual(IRI.create(onto.getOntologyID().getOntologyIRI() + "#" + td.getSupplierName().replaceAll(",", "_").replaceAll(" ", "_")));
			classAssertionAxiom = df.getOWLClassAssertionAxiom(supplierClass, supplierInd);			
			addAxiomChange = new AddAxiom(onto, classAssertionAxiom);
			manager.applyChange(addAxiomChange);
			
			//add hasName DP
			DPAssertionAxiom = df.getOWLDataPropertyAssertionAxiom(OntologyOperations.getDataProperty("hasName", onto), supplierInd, df.getOWLLiteral(td.getSupplierName().replaceAll(",", "_")));
			addAxiomChange = new AddAxiom(onto, DPAssertionAxiom);
			manager.applyChange(addAxiomChange);
			
			//add hasCity DP
			DPAssertionAxiom = df.getOWLDataPropertyAssertionAxiom(OntologyOperations.getDataProperty("hasCity", onto), supplierInd, df.getOWLLiteral(td.getSupplierCity().replaceAll(",", "_").replaceAll(" ", "_")));
			addAxiomChange = new AddAxiom(onto, DPAssertionAxiom);
			manager.applyChange(addAxiomChange);
			
			//add hasNation DP
			DPAssertionAxiom = df.getOWLDataPropertyAssertionAxiom(OntologyOperations.getDataProperty("hasNation", onto), supplierInd, df.getOWLLiteral(td.getSupplierNationality().replaceAll(",", "_").replaceAll(" ", "_")));
			addAxiomChange = new AddAxiom(onto, DPAssertionAxiom);
			manager.applyChange(addAxiomChange);
			
			//add capacity DP - DO NOT ADD AT SUPPLIER INSTANCE, BUT PROCESS CHAIN INSTANCE
			//DPAssertionAxiom = df.getOWLDataPropertyAssertionAxiom(OntologyOperations.getDataProperty("hasQuantity", onto), supplierInd, df.getOWLLiteral(td.getCapacity()));
			//addAxiomChange = new AddAxiom(onto, DPAssertionAxiom);
			//manager.applyChange(addAxiomChange);
			
			//adding period data
			periodInd = df.getOWLNamedIndividual(IRI.create(onto.getOntologyID().getOntologyIRI() + "#" + "PERIOD_" + iterator));
			classAssertionAxiom = df.getOWLClassAssertionAxiom(periodClass, periodInd);			
			addAxiomChange = new AddAxiom(onto, classAssertionAxiom);
			manager.applyChange(addAxiomChange);
			
			//add hasFrom date
			DPAssertionAxiom = df.getOWLDataPropertyAssertionAxiom(OntologyOperations.getDataProperty("hasFrom", onto), periodInd, df.getOWLTypedLiteral(convertToDateTime(td.getAvailableFrom()), OWL2Datatype.XSD_DATE_TIME));
			addAxiomChange = new AddAxiom(onto, DPAssertionAxiom);
			manager.applyChange(addAxiomChange);
			
			//add hasTo date
			DPAssertionAxiom = df.getOWLDataPropertyAssertionAxiom(OntologyOperations.getDataProperty("hasTo", onto), periodInd, df.getOWLTypedLiteral(convertToDateTime(td.getAvailableTo()), OWL2Datatype.XSD_DATE_TIME));
			addAxiomChange = new AddAxiom(onto, DPAssertionAxiom);
			manager.applyChange(addAxiomChange);
			
			//add material
			materialInd = df.getOWLNamedIndividual(IRI.create(onto.getOntologyID().getOntologyIRI() + "#" + td.getMaterial().replaceAll(",", "_").replaceAll(" ", "_")));
			classAssertionAxiom = df.getOWLClassAssertionAxiom(materialTypeClass, materialInd);			
			addAxiomChange = new AddAxiom(onto, classAssertionAxiom);
			manager.applyChange(addAxiomChange);
			
			//add machine
			machineInd = df.getOWLNamedIndividual(IRI.create(onto.getOntologyID().getOntologyIRI() + "#" + td.getMachine().replaceAll(",", "_").replaceAll(" ", "_")));
			classAssertionAxiom = df.getOWLClassAssertionAxiom(machineTypeClass, machineInd);			
			addAxiomChange = new AddAxiom(onto, classAssertionAxiom);
			manager.applyChange(addAxiomChange);
			
			
			//get the set of certifications for this supplier record
			Set<OWLClass> certificationClasses = new HashSet<OWLClass>();
			Set<OWLIndividual> certificationInds = new HashSet<OWLIndividual>();
						
			for (String s : td.getCertification()) {
				certificationClasses.add(getClass(s, onto));
			}
			
			//add certifications
			for (OWLClass c : certificationClasses) {
				
				certificationInd = df.getOWLNamedIndividual(IRI.create(onto.getOntologyID().getOntologyIRI() + "#" + c.getIRI().getFragment() + "_" + td.getSupplierName().replaceAll(",", "_").replaceAll(" ", "_")));
				//add individual certifications to the set of certification individuals for this supplier
				certificationInds.add(certificationInd);
				classAssertionAxiom = df.getOWLClassAssertionAxiom(c, certificationInd);	
				addAxiomChange = new AddAxiom(onto, classAssertionAxiom);
				manager.applyChange(addAxiomChange);
			}
			
			
			//OP hasProcess from processChainInd to processInd
			OPAssertionAxiom = df.getOWLObjectPropertyAssertionAxiom(OntologyOperations.getObjectProperty("hasProcess", onto), processChainInd, processInd);
			addAxiomChange = new AddAxiom(onto, OPAssertionAxiom);
			manager.applyChange(addAxiomChange);
			
			//OP hasMachine from processChainInd to machineInd
			OPAssertionAxiom = df.getOWLObjectPropertyAssertionAxiom(OntologyOperations.getObjectProperty("_hasMachine", onto), processChainInd, machineInd);
			addAxiomChange = new AddAxiom(onto, OPAssertionAxiom);
			manager.applyChange(addAxiomChange);
			
			//OP hasSupplier from processChainInd to supplierInd
			OPAssertionAxiom = df.getOWLObjectPropertyAssertionAxiom(OntologyOperations.getObjectProperty("hasSupplier", onto), processChainInd, supplierInd);
			addAxiomChange = new AddAxiom(onto, OPAssertionAxiom);
			manager.applyChange(addAxiomChange);
			
			//OP hasPeriod from processChainInd to periodInd
			OPAssertionAxiom = df.getOWLObjectPropertyAssertionAxiom(OntologyOperations.getObjectProperty("hasPeriod", onto), processChainInd, periodInd);
			addAxiomChange = new AddAxiom(onto, OPAssertionAxiom);
			manager.applyChange(addAxiomChange);
			
			//OP hasInput from processChainInd to materialInd
			OPAssertionAxiom = df.getOWLObjectPropertyAssertionAxiom(OntologyOperations.getObjectProperty("hasInput", onto), processChainInd, materialInd);
			addAxiomChange = new AddAxiom(onto, OPAssertionAxiom);
			manager.applyChange(addAxiomChange);
			
			//OP _hasCertification from processChainInd to certificationInd
			//NOTE: _hasCertification is added as OP in the ontology since we couldn´t find another way of associating certifications...
			//NOTE 2: Currently this OP is ProcessChain --> Certification, but it should probably have Stakeholder as domain class...
			for (OWLIndividual ind : certificationInds) {
				OPAssertionAxiom = df.getOWLObjectPropertyAssertionAxiom(OntologyOperations.getObjectProperty("_hasCertification", onto), processChainInd, ind);
				addAxiomChange = new AddAxiom(onto, OPAssertionAxiom);
				manager.applyChange(addAxiomChange);
			}
			

		}
		//save the ontology in each iteration
		manager.saveOntology(onto);
	}
		

	

	public CSV2OWL() {
		// TODO Auto-generated constructor stub
	}
	
	public String getProcessChain() {
		return processChain;
	}

	public void setProcessChain(String processChain) {
		this.processChain = processChain;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getSupplierNationality() {
		return supplierNationality;
	}

	public void setSupplierNationality(String supplierNationality) {
		this.supplierNationality = supplierNationality;
	}

	public String getSupplierCity() {
		return supplierCity;
	}

	public void setSupplierCity(String supplierCity) {
		this.supplierCity = supplierCity;
	}

	public int getRfqResponseTime() {
		return rfqResponseTime;
	}

	public void setRfqResponseTime(int rfqResponseTime) {
		this.rfqResponseTime = rfqResponseTime;
	}


	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	

	public Set<String> getCertification() {
		return certification;
	}


	public void setCertification(Set<String> certification) {
		this.certification = certification;
	}


	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}
	
	

	public String getMachine() {
		return machine;
	}


	public void setMachine(String machine) {
		this.machine = machine;
	}


	public String getAvailableFrom() {
		return availableFrom;
	}

	public void setAvailableFrom(String availableFrom) {
		this.availableFrom = availableFrom;
	}

	public String getAvailableTo() {
		return availableTo;
	}

	public void setAvailableTo(String availableTo) {
		this.availableTo = availableTo;
	}

	@Override
	public String toString() {
		return "TestData [supplierName=" + supplierName + ", supplierNationality=" + supplierNationality
				+ ", supplierCity=" + supplierCity + ", rfqResponseTime="
				+ rfqResponseTime + ", certification="
				+ certification + ", capacity="
				+ capacity + ", material=" + material + ", process=" + process + ", machine=" + machine +", availableFrom=" + availableFrom
				+ ", availableTo=" + availableTo + "]";
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
