package query;

import java.util.Set;

/**
 * Class to represent a consumer query
 * @author audunvennesland
 *
 */
public class ConsumerQuery {
	
	private String city;
	private String nation;
	private int capacity;
	private String requiredProcess;
	private String requiredMaterial;
	private String requiredMachine;
	private Set<String> requiredCertificates;
	private String requiredAvailableFromDate;
	private String requiredAvailableToDate;
	
	public ConsumerQuery(String city, String nation, String product, int capacity, double height, double width,
			double depth, String requiredProcess, String requiredMaterial, String requiredMachine, int rFQResponseTime,
			int productionLeadTime, Set<String> requiredCertificates, String requiredAvailableFromDate, String requiredAvailableToDate) {
		super();
		this.city = city;
		this.nation = nation;
		this.capacity = capacity;
		this.requiredProcess = requiredProcess;
		this.requiredMaterial = requiredMaterial;
		this.requiredMachine = requiredMachine;
		this.requiredCertificates = requiredCertificates;
		this.requiredAvailableFromDate = requiredAvailableFromDate;
		this.requiredAvailableToDate = requiredAvailableToDate;
	}
	
	public ConsumerQuery() {}

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

	public void setQuantity(int capacity) {
		this.capacity = capacity;
	}

	public String getRequiredProcess() {
		return requiredProcess;
	}

	public void setRequiredProcess(String requiredProcess) {
		this.requiredProcess = requiredProcess;
	}

	public String getRequiredMaterial() {
		return requiredMaterial;
	}

	public void setRequiredMaterial(String requiredMaterial) {
		this.requiredMaterial = requiredMaterial;
	}

	public String getRequiredMachine() {
		return requiredMachine;
	}

	public void setRequiredMachine(String requiredMachine) {
		this.requiredMachine = requiredMachine;
	}

	public String getRequiredAvailableFromDate() {
		return requiredAvailableFromDate;
	}

	public Set<String> getRequiredCertificates() {
		return requiredCertificates;
	}

	public void setRequiredCertificates(Set<String> requiredCertificates) {
		this.requiredCertificates = requiredCertificates;
	}

	public void setRequiredAvailableFromDate(String requiredAvailableFromDate) {
		this.requiredAvailableFromDate = requiredAvailableFromDate;
	}

	public String getRequiredAvailableToDate() {
		return requiredAvailableToDate;
	}

	public void setRequiredAvailableToDate(String requiredAvailableToDate) {
		this.requiredAvailableToDate = requiredAvailableToDate;
	}
	
	
	
	
	

}
