package owlprocessing;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;



/**
 * @author audunvennesland Date:09.12.2019
 * @version 1.0
 */
public class OntologyOperations {

	

	/**
	 * An OWLOntologyManagermanages a set of ontologies. It is the main point
	 * for creating, loading and accessing ontologies.
	 */
	static OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	/**
	 * The OWLReasonerFactory represents a reasoner creation point.
	 */
	static OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
	
	/**
	 * 
	 */
	static PelletReasonerFactory pelletReasonerFactory = new PelletReasonerFactory();



	/**
	 * Default constructor
	 */
	public OntologyOperations() {

	}
	
	/**
	 * Returns a Map holding a class as key and its superclass as value. This version uses the Pellet reasoner, since the structural reasoner does not include all inferred superclasses of a class.
	 * 
	 * @param o
	 *            the input OWL ontology from which classes and superclasses
	 *            should be derived
	 * @return classesAndSuperClasses a Map holding a class as key and its
	 *         superclass as value
	 * @throws OWLOntologyCreationException
	 *             An exception which describes an error during the creation of
	 *             an ontology. If an ontology cannot be created then subclasses
	 *             of this class will describe the reasons.
	 */
	public static Map<String, String> getClassesAndSuperClassesUsingPellet (OWLOntology o) throws OWLOntologyCreationException {

		PelletReasoner reasoner = pelletReasonerFactory.createReasoner(o);
		Set<OWLClass> cls = o.getClassesInSignature();
		Map<String, String> classesAndSuperClasses = new HashMap<String, String>();
		ArrayList<OWLClass> classList = new ArrayList<OWLClass>();

		for (OWLClass i : cls) {
			classList.add(i);
		}

		// Iterate through the arraylist and for each class get the subclasses
		// belonging to it
		// Transform from OWLClass to String to simplify further processing...
		for (int i = 0; i < classList.size(); i++) {
			OWLClass currentClass = classList.get(i);
			NodeSet<OWLClass> n = reasoner.getSuperClasses(currentClass, true);
			Set<OWLClass> s = n.getFlattened();
			for (OWLClass j : s) {

				classesAndSuperClasses.put(currentClass.getIRI().getFragment(), j.getIRI().getFragment());
			}
		}

		manager.removeOntology(o);

		return classesAndSuperClasses;

	}


	/**
	 * Returns a set of classes in an OWL ontology using their class name as string
	 * @param onto OWLOntology
	 * @return Set<String> of class names
	   Dec 9, 2019
	 */
	public static Set<String> getClassesAsString (OWLOntology onto) {
		Set<String> classesAsString = new HashSet<String>();
		
		for (OWLClass c : onto.getClassesInSignature()) {
			classesAsString.add(c.getIRI().getFragment());
		}
		
		return classesAsString;
	}

	
	/**
	 * Helper method that retrieves ALL subclasses (fragments or proper name without URI) for an OWLClass (provided as parameter along with the OWLOntology which is needed for allowing the reasoner to get all subclasses for an OWLClass)
	 * @param onto the input OWLOntology
	 * @param inputClass the OWLClass for which subclasses will be retrieved
	 * @return Set<String> of subclasses for an OWLClass
	 */
	public static Set<String> getAllEntitySubclassesFragments (OWLOntology onto, OWLClass inputClass) {
		OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createReasoner(onto);

		NodeSet<OWLClass> subclasses = reasoner.getSubClasses(inputClass, false);

		Set<String> subclsSet = new HashSet<String>();

		for (OWLClass cls : subclasses.getFlattened()) {
			if (!cls.isOWLNothing()) {
				subclsSet.add(cls.getIRI().getFragment().toString());
			}
		}

		return subclsSet;

	}


	/**
	 * Retrieves an OWLClass from its class name represented as a string
	 * @param className
	 * @param ontology
	 * @return an instance of an OWLClass c given a class name
	 */
	public static OWLClass getClass(String className, OWLOntology ontology) {

		OWLClass relevantClass = null;

		Set<OWLClass> classes = ontology.getClassesInSignature();

		for (OWLClass cls : classes) {
			//System.out.println("Test: Does cls: " + cls.getIRI().getFragment() + " equal " + className + " ?");
			if (cls.getIRI().getFragment().equals(className)) {
				relevantClass = cls;
				break;
			} else {
				relevantClass = null;
			}
		}

		//System.out.println("Test: Returning " + relevantClass);
		return relevantClass;


	}
	
	/**
	 * Retrieves the OWLObjectProperty from an object property name represented as a string
	 * @param objectPropertyName the name of an object property
	 * @param ontology OWLOntology
	 * @return an instance of an OWLObjectProperty op given an object property name
	   Dec 9, 2019
	 */
	public static OWLObjectProperty getObjectProperty(String objectPropertyName, OWLOntology ontology) {
		
		OWLObjectProperty relevantOP = null;
		
		Set<OWLObjectProperty> ops = ontology.getObjectPropertiesInSignature();
		
		for (OWLObjectProperty op : ops) {
			if (op.getIRI().getFragment().equals(objectPropertyName)) {
				relevantOP = op;
				break;
			} else {
				relevantOP = null;
			}
		}
		
		return relevantOP;
		
	}
	
	/**
	 * Retrieves the OWLDataProperty from a data property name represented as a string
	 * @param dataPropertyName the name of a data property
	 * @param ontology OWLOntology
	 * @return an instance of an OWLDataProperty dp given a data property name
	   Dec 9, 2019
	 */
	public static OWLDataProperty getDataProperty(String dataPropertyName, OWLOntology ontology) {
		
		OWLDataProperty relevantDP = null;
		
		Set<OWLDataProperty> dps = ontology.getDataPropertiesInSignature();
		
		for (OWLDataProperty dp : dps) {
			if (dp.getIRI().getFragment().equals(dataPropertyName)) {
				relevantDP = dp;
				break;
			} else {
				relevantDP = null;
			}
		}
		
		return relevantDP;
		
	}


}