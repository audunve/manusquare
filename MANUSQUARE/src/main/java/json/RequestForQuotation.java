package json;

import java.io.IOException;
import java.util.List;

import org.json.simple.parser.ParseException;

public class RequestForQuotation {

	String nda;
	String projectName;
	String projectDescription;
	String selectionType;
	String supplierMaxDistance;
	String servicePolicy;
	String projectId;
	String id;
	String projectType;

	public List<ProjectAttributeKeys> projectAttributes;
	public List<SupplierAttributeKeys> supplierAttributes;

	public RequestForQuotation(String nda, String projectName, String projectDescription, String selectionType, String supplierMaxDistance,
			String servicePolicy, String projectId, String id, String projectType,
			List<ProjectAttributeKeys> projectAttributes, List<SupplierAttributeKeys> supplierAttributes) {
		super();
		this.nda = nda;
		this.projectName = projectName;
		this.projectDescription = projectDescription;
		this.selectionType = selectionType;
		this.supplierMaxDistance = supplierMaxDistance;
		this.servicePolicy = servicePolicy;
		this.projectId = projectId;
		this.id = id;
		this.projectType = projectType;
		this.projectAttributes = projectAttributes;
		this.supplierAttributes = supplierAttributes;

	}


}
