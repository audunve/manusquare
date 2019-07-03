package supplierdata;

import java.util.Set;

public class Supplier {
	
	String id;
	String supplierName;
	String supplierNationality;
	String supplierCity;
	int rfqResponseTime;
	String cadType;
	Set<String> certifications;
	Set<Resource> resources;
	
	public Supplier(String id, String supplierName, String supplierNationality, String supplierCity,int rfqResponseTime,
			String cadType, Set<String> certifications,Set<Resource> resources) {
		super();
		this.id = id;
		this.supplierName = supplierName;
		this.supplierNationality = supplierNationality;
		this.supplierCity = supplierCity;
		this.rfqResponseTime = rfqResponseTime;
		this.cadType = cadType;
		this.certifications = certifications;
		this.resources = resources;
	}
	
	//used for creation of test data
	public Supplier(String id, String supplierName, String supplierNationality, String supplierCity,int rfqResponseTime,
			String cadType, Set<String> certifications) {
		super();
		this.id = id;
		this.supplierName = supplierName;
		this.supplierNationality = supplierNationality;
		this.supplierCity = supplierCity;
		this.rfqResponseTime = rfqResponseTime;
		this.cadType = cadType;
		this.certifications = certifications;
	}

	public Supplier(String id, String supplierName, String supplierNationality, String supplierCity,
			Set<Resource> resources) {
		super();
		this.id = id;
		this.supplierName = supplierName;
		this.supplierNationality = supplierNationality;
		this.supplierCity = supplierCity;
		this.resources = resources;
	}
	
	public Supplier() {}
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getCadType() {
		return cadType;
	}

	public void setCadType(String cadType) {
		this.cadType = cadType;
	}

	public Set<String> getCertifications() {
		return certifications;
	}

	public void setCertifications(Set<String> certifications) {
		this.certifications = certifications;
	}

	public Set<Resource> getResources() {
		return resources;
	}

	public void setResources(Set<Resource> resources) {
		this.resources = resources;
	}

}
