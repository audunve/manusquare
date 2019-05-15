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
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.AutoIRIMapper;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import owlprocessing.OntologyOperations;

/**
 * Reads instance data from CSV and adds these data as individuals in the Manusquare Industrial OWL ontology.
 * @author audunvennesland
 *
 */
public class CSV2OWL {
	
	String processChain;
	String supplierName;
	String supplierNationality;
	String supplierCity;
	int productionLeadTime;
	int rfqResponseTime;
	String sector;
	String componentDesign;
	String certification;
	double sizeX;
	double sizeY;
	double sizeZ;
	int quantity;
	String materials;
	String process;
	String availableFrom;
	String availableTo;
	String comments;


	public CSV2OWL(String processChain, String supplierName, String supplierNationality, String supplierCity, int productionLeadTime,
			int rfqResponseTime, String sector, String componentDesign, String certification, double sizeX,
			double sizeY, double sizeZ, int quantity, String materials, String process, String availableFrom,
			String availableTo, String comments) {
		super();
		this.processChain = processChain;
		this.supplierName = supplierName;
		this.supplierNationality = supplierNationality;
		this.supplierCity = supplierCity;
		this.productionLeadTime = productionLeadTime;
		this.rfqResponseTime = rfqResponseTime;
		this.sector = sector;
		this.componentDesign = componentDesign;
		this.certification = certification;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.quantity = quantity;
		this.materials = materials;
		this.process = process;
		this.availableFrom = availableFrom;
		this.availableTo = availableTo;
		this.comments = comments;
	}


	public static void main(String[] args) throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {

		CSV2OWL data;

		BufferedReader br = new BufferedReader(new FileReader("./files/Manusquare - testdata - short-V3.csv"));

		String line = br.readLine();

		String[] params = null;

		Set<CSV2OWL> dataset = new HashSet<CSV2OWL>();

		while (line != null) {
			params = line.split(";");

			data = new CSV2OWL();
			data.setProcessChain(params[0]);
			data.setSupplierName(params[1]);
			data.setSupplierNationality(params[2]);
			data.setSupplierCity(params[3]);
			data.setProductionLeadTime(Integer.parseInt(params[4]));
			data.setRfqResponseTime(Integer.parseInt(params[5]));
			data.setSector(params[6]);
			data.setComponentDesign(params[7]);

			if (params[8].equals("1")) {			
				data.setCertification("ISO9001");
			} else {
				data.setCertification(null);
			}

			data.setSizeX(Double.parseDouble(params[9]));
			data.setSizeY(Double.parseDouble(params[10]));
			data.setSizeZ(Double.parseDouble(params[11]));
			data.setQuantity(Integer.parseInt(params[12]));
			data.setMaterials(params[13]);
			data.setProcess(params[14]);
			data.setAvailableFrom(params[15]);
			data.setAvailableTo(params[16]);
			//all comments were empty so disregarding those...
			//data.setComments(params[16]);

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
		//OWLOntology coreOnto = manager.loadOntologyFromOntologyDocument(ontoFile2)
		System.out.println("The ontology contains " + onto.getClassesInSignature().size() + " classes");
		
		OWLDataFactory df = manager.getOWLDataFactory();
		OWLClass processChainClass = getClass("ProcessChain", onto);
		OWLClass supplierClass = getClass("Supplier", onto);
		OWLClass periodClass = getClass("Period", onto);
		OWLClass capabilityTypeClass = getClass("CapabilityType", onto);
		OWLClass materialTypeClass = getClass("MaterialType", onto);
		OWLClass processTypeClass = getClass("ProcessType", onto);
		OWLClass certificationClass = getClass("Certification", onto);
		
		OWLIndividual processChainInd = null;
		OWLIndividual supplierInd = null;
		OWLIndividual capabilityInd = null;
		OWLIndividual materialInd = null;
		OWLIndividual processInd = null;
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
			DPAssertionAxiom = df.getOWLDataPropertyAssertionAxiom(OntologyOperations.getDataProperty("hasQuantity", onto), processChainInd, df.getOWLLiteral(td.getQuantity()));
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

			capabilityInd = df.getOWLNamedIndividual(IRI.create(onto.getOntologyID().getOntologyIRI() + "#" + td.getComponentDesign().replaceAll(",", "_").replaceAll(" ", "_")));
			classAssertionAxiom = df.getOWLClassAssertionAxiom(capabilityTypeClass, capabilityInd);			
			addAxiomChange = new AddAxiom(onto, classAssertionAxiom);
			manager.applyChange(addAxiomChange);
			
			materialInd = df.getOWLNamedIndividual(IRI.create(onto.getOntologyID().getOntologyIRI() + "#" + td.getMaterials().replaceAll(",", "_").replaceAll(" ", "_")));
			classAssertionAxiom = df.getOWLClassAssertionAxiom(materialTypeClass, materialInd);			
			addAxiomChange = new AddAxiom(onto, classAssertionAxiom);
			manager.applyChange(addAxiomChange);
			
			if (certificationInd != null) {
			certificationInd = df.getOWLNamedIndividual(IRI.create(onto.getOntologyID().getOntologyIRI() + "#" + td.getCertification().replaceAll(",", "_").replaceAll(" ", "_")));
			classAssertionAxiom = df.getOWLClassAssertionAxiom(certificationClass, certificationInd);			
			addAxiomChange = new AddAxiom(onto, classAssertionAxiom);
			manager.applyChange(addAxiomChange);
			}
			
			//OP hasProcess from processChainInd to processInd
			OPAssertionAxiom = df.getOWLObjectPropertyAssertionAxiom(OntologyOperations.getObjectProperty("hasProcess", onto), processChainInd, processInd);
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
			
			//OP hasCapability from processChainInd to capabilityInd
			OPAssertionAxiom = df.getOWLObjectPropertyAssertionAxiom(OntologyOperations.getObjectProperty("hasCapability", onto), processChainInd, capabilityInd);
			addAxiomChange = new AddAxiom(onto, OPAssertionAxiom);
			manager.applyChange(addAxiomChange);
			
			//OP _hasCertification from processChainInd to certificationInd - but only if its not null!
			if (certificationInd != null) {
			OPAssertionAxiom = df.getOWLObjectPropertyAssertionAxiom(OntologyOperations.getObjectProperty("_hasCertification", onto), processChainInd, certificationInd);
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

	public int getProductionLeadTime() {
		return productionLeadTime;
	}

	public void setProductionLeadTime(int productionLeadTime) {
		this.productionLeadTime = productionLeadTime;
	}

	public int getRfqResponseTime() {
		return rfqResponseTime;
	}

	public void setRfqResponseTime(int rfqResponseTime) {
		this.rfqResponseTime = rfqResponseTime;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public String getComponentDesign() {
		return componentDesign;
	}

	public void setComponentDesign(String componentDesign) {
		this.componentDesign = componentDesign;
	}

	public String getCertification() {
		return certification;
	}

	public void setCertification(String certification) {
		this.certification = certification;
	}

	public double getSizeX() {
		return sizeX;
	}

	public void setSizeX(double sizeX) {
		this.sizeX = sizeX;
	}

	public double getSizeY() {
		return sizeY;
	}

	public void setSizeY(double sizeY) {
		this.sizeY = sizeY;
	}

	public double getSizeZ() {
		return sizeZ;
	}

	public void setSizeZ(double sizeZ) {
		this.sizeZ = sizeZ;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getMaterials() {
		return materials;
	}

	public void setMaterials(String materials) {
		this.materials = materials;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
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

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		return "TestData [supplierName=" + supplierName + ", supplierNationality=" + supplierNationality
				+ ", supplierCity=" + supplierCity + ", productionLeadTime=" + productionLeadTime + ", rfqResponseTime="
				+ rfqResponseTime + ", sector=" + sector + ", componentDesign=" + componentDesign + ", certification="
				+ certification + ", sizeX=" + sizeX + ", sizeY=" + sizeY + ", sizeZ=" + sizeZ + ", quantity="
				+ quantity + ", materials=" + materials + ", process=" + process + ", availableFrom=" + availableFrom
				+ ", availableTo=" + availableTo + ", comments=" + comments + "]";
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
