package json;

public class ProjectAttributeKeys {
	
	String attributeId;
	String processId;
	String attributeKey;
	String attributeValue;
	
	public ProjectAttributeKeys(String attributeId, String processId, String attributeKey, String attributeValue) {
		super();
		this.attributeId = attributeId;
		this.processId = processId;
		this.attributeKey = attributeKey;
		this.attributeValue = attributeValue;
	}

	public String getAttributeId() {
		return attributeId;
	}

	public void setAttributeId(String attributeId) {
		this.attributeId = attributeId;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
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
