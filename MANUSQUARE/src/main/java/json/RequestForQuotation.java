package json;

import java.util.List;
import java.util.Set;

public class RequestForQuotation {

	String nda;
	String projectName;
	String projectDescription;
	String selectionType;
	String supplierMaxDistance;
	String servicePolicy;
	Set<String> processNames;
	String projectId;
	String id;
	String projectType;

	public List<ProjectAttributeKeys> projectAttributes;
	public List<SupplierAttributeKeys> supplierAttributes;

	public RequestForQuotation(String nda, String projectName, String projectDescription, String selectionType, String supplierMaxDistance,
			String servicePolicy, Set<String> processnames, String projectId, String id, String projectType,
			List<ProjectAttributeKeys> projectAttributes, List<SupplierAttributeKeys> supplierAttributes) {
		super();
		this.nda = nda;
		this.projectName = projectName;
		this.projectDescription = projectDescription;
		this.selectionType = selectionType;
		this.supplierMaxDistance = supplierMaxDistance;
		this.servicePolicy = servicePolicy;
		this.processNames = processnames;
		this.id = id;
		this.projectId = projectId;
		this.projectType = projectType;
		this.projectAttributes = projectAttributes;
		this.supplierAttributes = supplierAttributes;

	}
	
	public RequestForQuotation(List<ProjectAttributeKeys> projectAttributes, List<SupplierAttributeKeys> supplierAttributes) {
		super();
		this.projectAttributes = projectAttributes;
		this.supplierAttributes = supplierAttributes;
	}
	
	public RequestForQuotation(List<ProjectAttributeKeys> projectAttributes) {
		super();
		this.projectAttributes = projectAttributes;
	}
	
	public class ProjectAttributeKeys {
		
		public String attributeId;
		public String processName;
		public String attributeKey;
		public String attributeValue;
		
		public ProjectAttributeKeys(String attributeId, String processId, String attributeKey, String attributeValue) {
			super();
			this.attributeId = attributeId;
			this.processName = processId;
			this.attributeKey = attributeKey;
			this.attributeValue = attributeValue;
		}

		public String getAttributeId() {
			return attributeId;
		}

		public void setAttributeId(String attributeId) {
			this.attributeId = attributeId;
		}

		public String getProcessName() {
			return processName;
		}

		public void setProcessName(String processName) {
			this.processName = processName;
		}

		public String getAttributeKey() {
			return attributeKey;
		}

		public void setAttributeKey(String attributeKey) {
			this.attributeKey = attributeKey;
		}

		public String getAttributeValue() {
			return attributeValue;
		}

		public void setAttributeValue(String attributeValue) {
			this.attributeValue = attributeValue;
		}
		
		
	}
	
	public class SupplierAttributeKeys {
		
		public String id;
		public String attributeKey;
		public String attributeValue;
		
		public SupplierAttributeKeys(String id, String attributeKey, String attributeValue) {
			super();
			this.id = id;
			this.attributeKey = attributeKey;
			this.attributeValue = attributeValue;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getAttributeKey() {
			return attributeKey;
		}

		public void setAttributeKey(String attributeKey) {
			this.attributeKey = attributeKey;
		}

		public String getAttributeValue() {
			return attributeValue;
		}

		public void setAttributeValue(String attributeValue) {
			this.attributeValue = attributeValue;
		}
	}


}
