package supplierdata;

import java.util.List;
import java.util.Set;

public class SupplierResourceRecord {
	
	private String id;
	private String supplierName;
	private String city;
	private String nation;
	private int capacity;
	private double height;
	private double width;
	private double depth;
	private String cadCapability;
	private String usedProcess;
	private String usedMaterial;
	private String usedMachine;
	private int promisedRFQResponseTime;
	private int promisedproductionLeadTime;
//	private String posessedCertificates;
	private Set<String> posessedCertificates;
	private String availableFrom;
	private String availableTo;
	
	public SupplierResourceRecord(String id, String supplierName, String city, String nation, int capacity, double height, double width, double depth,
			String cadCapability, String usedProcess, String usedMaterial, String usedMachine, int promisedRFQResponseTime,
			int promisedproductionLeadTime, Set<String> posessedCertificates, String availableFrom, String availableTo) {
		super();
		this.id = id;
		this.supplierName = supplierName;
		this.city = city;
		this.nation = nation;
		this.capacity = capacity;
		this.height = height;
		this.width = width;
		this.depth = depth;
		this.cadCapability = cadCapability;
		this.usedProcess = usedProcess;
		this.usedMaterial = usedMaterial;
		this.usedMachine = usedMachine;
		this.promisedRFQResponseTime = promisedRFQResponseTime;
		this.promisedproductionLeadTime = promisedproductionLeadTime;
		this.posessedCertificates = posessedCertificates;
		this.availableFrom = availableFrom;
		this.availableTo = availableTo;
	}
	
	public SupplierResourceRecord() {}
	
	

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

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getNation() {
		return nation;
	}

	public void setNation(String nation) {
		this.nation = nation;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getDepth() {
		return depth;
	}

	public void setDepth(double depth) {
		this.depth = depth;
	}
	
	

	public String getCadCapability() {
		return cadCapability;
	}

	public void setCadCapability(String cadCapability) {
		this.cadCapability = cadCapability;
	}

	public String getUsedProcess() {
		return usedProcess;
	}

	public void setUsedProcess(String usedProcess) {
		this.usedProcess = usedProcess;
	}

	public String getUsedMaterial() {
		return usedMaterial;
	}

	public void setUsedMaterial(String usedMaterial) {
		this.usedMaterial = usedMaterial;
	}

	public String getUsedMachine() {
		return usedMachine;
	}

	public void setUsedMachine(String usedMachine) {
		this.usedMachine = usedMachine;
	}

	public int getPromisedRFQResponseTime() {
		return promisedRFQResponseTime;
	}

	public void setPromisedRFQResponseTime(int promisedRFQResponseTime) {
		this.promisedRFQResponseTime = promisedRFQResponseTime;
	}

	public int getPromisedproductionLeadTime() {
		return promisedproductionLeadTime;
	}

	public void setPromisedproductionLeadTime(int promisedproductionLeadTime) {
		this.promisedproductionLeadTime = promisedproductionLeadTime;
	}

//	public String getPosessedCertificates() {
//		return posessedCertificates;
//	}
//
//	public void setPosessedCertificates(String posessedCertificates) {
//		this.posessedCertificates = posessedCertificates;
//	}
	
	

	public String getAvailableFrom() {
		return availableFrom;
	}

	public Set<String> getPosessedCertificates() {
		return posessedCertificates;
	}

	public void setPosessedCertificates(Set<String> posessedCertificates) {
		this.posessedCertificates = posessedCertificates;
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
	
	
	
	
}
