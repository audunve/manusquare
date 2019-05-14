package parsing;

import java.util.Iterator;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.VCARD;

public class JENATest {
	
	
	public static void main(String[] args) {
		
	String personURI = "http://somewhere/JohnSmith";
	String fullName = "John Smith";
	String givenName = "John";
	String familyName = "Smith";
	
	//create a model
	Model model = ModelFactory.createDefaultModel();
	
	//create a resource
	Resource johnSmith = model.createResource(personURI)
			.addProperty(VCARD.FN, fullName)
			.addProperty(VCARD.N,
					model.createResource()
					.addProperty(VCARD.Given, givenName)
					.addProperty(VCARD.Family, familyName));
	

	System.out.println("Using model.write to print output");
	model.write(System.out);
	System.out.println("\n");
	
	System.out.println("Using model.write (N-TRIPLES) to print output");
	model.write(System.out, "N-TRIPLES");
	System.out.println("\n");
	
	StmtIterator iter = model.listStatements();
	
	while (iter.hasNext()) {
		Statement stmt = iter.nextStatement();
		Resource subject = stmt.getSubject();
		Property predicate = stmt.getPredicate();
		RDFNode object = stmt.getObject();
		
		System.out.print(subject.toString());
		System.out.print(" " + predicate.toString() + " ");
		//if the object is a resource
		if (object instanceof Resource) {
			System.out.print(object.toString());
			//object is a literal
		} else {
			System.out.print(" \" " + object.toString() + "\" ");
		}
		
		System.out.println(" .");
	}
	
	String SOURCE = "http://manusquare.project.eu/industrial-manusquare";
	String NS = SOURCE + "#";
	OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
	ontModel.read("./files/manusquare-industrial.owl");
	
	//create a reasoning model using the base
	OntModel inf = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF, ontModel);
	
	//create a dummy paper instance
	OntClass carbonSteel = ontModel.getOntClass(NS + "CarbonSteel");
	
	Individual steel_1_material = ontModel.createIndividual(NS + "steel_1", carbonSteel);
	
	
	OntClass resource = ontModel.getOntClass(NS + "Resource");
	Individual query_1 = ontModel.createIndividual(NS + "query_1", resource);
	
	//list asserted types
	System.out.println("\nAsserted types of " + steel_1_material.getLocalName());
	for (Iterator<Resource> i = steel_1_material.listRDFTypes(true); i.hasNext();) {
		System.out.println(steel_1_material.getURI() + " is asserted in class " + i.next());
	}
	
	System.out.println("\nInferred types of " + steel_1_material.getLocalName());
	
	//list inferred types
	steel_1_material = inf.getIndividual(NS + "steel_1");
	
	
	for (Iterator<Resource> i = steel_1_material.listRDFTypes(false); i.hasNext();) {
		
		//String localName = i.next().getLocalName();
		
		//System.out.println(localName);
		
		System.out.println(steel_1_material.getURI() + " is inferred to be in class " + i.next());
	}
	
	
//	System.out.println("Print all triples in Manusquare ontology");
//	base.write(System.out, "TTL");
	
	
	}
	

}
