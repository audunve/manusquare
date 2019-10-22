package json;

public class SupplierAttributeKeys {
	
	String id;
	String attributeKey;
	String attributeValue;
	
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
