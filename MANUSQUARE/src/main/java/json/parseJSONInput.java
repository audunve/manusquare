package json;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import query.ConsumerQuery;

public class parseJSONInput {
	
	public static void main(String[] args) throws IOException {
		
		String filename = "./files/GsonSerialization.json";
		
		serialize(filename);
		//deserialize(filename);
		
		Set<ConsumerQuery> consumerQueries = createConsumerQuery(filename);
		System.out.println("There are " + consumerQueries.size() + " consumer queries");
		
		int i = 1; 
		for (ConsumerQuery query : consumerQueries) {
			System.out.println("Query: " + i);
			System.out.println(query.getRequiredProcess());
			for (String s : query.getRequiredCertificates()) {
				System.out.println("Certifications: " + s);
			}
			i++;
		}
		
	}
	
	private static void serialize(String filename) throws IOException {
		
		List<ProjectAttributeKeys> projectAttributes = new ArrayList<ProjectAttributeKeys>();
		projectAttributes.add(new ProjectAttributeKeys("A","Milling","workArea.X(mm)", "3048"));
		projectAttributes.add(new ProjectAttributeKeys("B","Milling","workArea.Y(mm)", "1048"));
		projectAttributes.add(new ProjectAttributeKeys("C","Milling","workArea.Z(mm)", "1230"));
		projectAttributes.add(new ProjectAttributeKeys("D","Milling","tolerance(+- mm)", "1"));
		projectAttributes.add(new ProjectAttributeKeys("D","Turning","workArea.X(mm)", "3048"));
		projectAttributes.add(new ProjectAttributeKeys("D","Turning","workArea.X(mm)", "1048"));
		projectAttributes.add(new ProjectAttributeKeys("D","Turning","workArea.Z(mm)", "1230"));
		projectAttributes.add(new ProjectAttributeKeys("D","Turning","tolerance(+- mm)", "2"));
		
		List<SupplierAttributeKeys> supplierAttributes = new ArrayList<SupplierAttributeKeys>();
		supplierAttributes.add(new SupplierAttributeKeys("supplier-attribute-430396576", "ISO9000", "yes"));
		supplierAttributes.add(new SupplierAttributeKeys("supplier-attribute-430396577", "ISO9001", "yes"));
		supplierAttributes.add(new SupplierAttributeKeys("supplier-attribute-430396578", "LEED", "no"));
		
		RequestForQuotation rfq = new RequestForQuotation("no", "Project1", "ProjectDescription", "Manual", "100", "true",
				"project-1254161155", "project-1254161155", "Capacity Sharing", projectAttributes, supplierAttributes);
		
				
		String json = new Gson().toJson(rfq);
		
		System.out.println(json);
		
		FileWriter writer = new FileWriter(filename);
		writer.write(json);
		writer.close();
		
		
	}
	
	
//	private static void deserialize(String filename) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
//		
//		RequestForQuotation rfq = new Gson().fromJson(new FileReader(filename), RequestForQuotation.class);
//		
//		for (ProjectAttributeKeys projectAttributes : rfq.projectAttributes) {
//			System.out.println(projectAttributes.processId);
//		}
//		
//		for (SupplierAttributeKeys supplierAttributes : rfq.supplierAttributes) {
//			if (supplierAttributes.attributeValue.equals("yes")) {
//			System.out.println(supplierAttributes.attributeKey);
//			}
//		}
//		
//		
//	}
	
	public static Set<ConsumerQuery> createConsumerQuery(String filename) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		
		Set<ConsumerQuery> consumerQueries = new HashSet<ConsumerQuery>();
		
		Set<String> processes = new HashSet<String>();
		Set<String> certifications = new HashSet<String>();
		
		RequestForQuotation rfq = new Gson().fromJson(new FileReader(filename), RequestForQuotation.class);
		
		for (ProjectAttributeKeys projectAttributes : rfq.projectAttributes) {
			processes.add(projectAttributes.processId);
		}
		
		for (SupplierAttributeKeys supplierAttributes : rfq.supplierAttributes) {
			if (supplierAttributes.attributeValue.equals("yes")) {
			certifications.add(supplierAttributes.attributeKey);
			}
		}
		
		for (String process : processes) {
			consumerQueries.add(new ConsumerQuery(process, certifications));
		}
		
		return consumerQueries;
	}

}
