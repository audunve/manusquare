

import java.io.File;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import owl.OntologyOperations;


public class OWLAPITest {
	
	public static void main(String[] args) throws OWLOntologyCreationException {
		
		//import manusquare ontology
		File ontoFile = new File("./files/manusquare-industrial.owl");
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology onto = manager.loadOntologyFromOntologyDocument(ontoFile);
		
		//get the ontology IRI
		IRI ontoIRI = onto.getOntologyID().getOntologyIRI();
		
		//create a data factory
		OWLDataFactory df = OWLManager.getOWLDataFactory();
		
		//add a query resource to the ontology
		OWLIndividual query_1 = df.getOWLNamedIndividual(IRI.create(ontoIRI + "#query_1"));
		OWLIndividual Ryan_Inc = df.getOWLNamedIndividual(IRI.create(ontoIRI + "#Ryan_Inc"));
		OWLIndividual switzerland = df.getOWLNamedIndividual(IRI.create(ontoIRI + "#switzerland"));
		OWLIndividual cnc = df.getOWLNamedIndividual(IRI.create(ontoIRI + "#cnc"));
		OWLIndividual milling = df.getOWLNamedIndividual(IRI.create(ontoIRI + "#miling"));
		OWLIndividual steel = df.getOWLNamedIndividual(IRI.create(ontoIRI + "#steel"));
		
		
		//add predicates to the query resource
		OWLClass cls = OntologyOperations.getClass("Equipment", onto);
		
		
		//get inferred class relationships for a particular individual
		Set<OWLObjectProperty> opSet = OntologyOperations.getObjectProperties(onto, cls);
		
		System.out.println("The object properties (direct) where " + cls.getIRI() + " is domain are:");
		for (OWLObjectProperty op : opSet) {
			System.out.println(op);
		}

	}
	
	/**
	 * Retrieves all object properties related to an OWLClass. 
	 * @param onto
	 * @param clsString
	 * @return
	 */
	private static Set<OWLObjectProperty> getObjectProperties(OWLOntology onto, OWLClass oCls) {

		Set<OWLClass> allClasses = onto.getClassesInSignature();		

		Set<OWLObjectProperty> ops = new HashSet<OWLObjectProperty>();

		for (OWLClass cls : allClasses) {
			if (cls.equals(oCls)) {

				for (OWLObjectPropertyDomainAxiom op : onto.getAxioms(AxiomType.OBJECT_PROPERTY_DOMAIN)) {
					if (op.getDomain().equals(cls)) {
						for (OWLObjectProperty oop : op.getObjectPropertiesInSignature()) {
							//ops.add(oop.getIRI().getFragment().substring(oop.getIRI().getFragment().lastIndexOf("-") +1));
							ops.add(oop);
						}
					}
				}

				for (OWLObjectPropertyRangeAxiom op : onto.getAxioms(AxiomType.OBJECT_PROPERTY_RANGE)) {
					if (op.getRange().equals(cls)) {
						for (OWLObjectProperty oop : op.getObjectPropertiesInSignature()) {
							//ops.add(oop.getIRI().getFragment().substring(oop.getIRI().getFragment().lastIndexOf("-") +1));
							ops.add(oop);
						}
					}
				}


			}
		}

		return ops;

	}

}
