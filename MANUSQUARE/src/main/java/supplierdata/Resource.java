package supplierdata;

import java.util.Set;

public class Resource {
	
	Supplier supplier;
	int capacity;
	String material;
	String process;
	String machine;
	Set<String> certifications;
	String availableFrom;
	String availableTo;

	
	public Resource(Supplier supplier, int capacity, String material, String process, String machine, /*Set<String> certifications, */String availableFrom,
			String availableTo) {
		super();
		this.supplier = supplier;
		this.capacity = capacity;
		this.material = material;
		this.process = process;
		this.machine = machine;
		//this.certifications = certifications;
		this.availableFrom = availableFrom;
		this.availableTo = availableTo;
	}
	
	public Resource(int capacity, String material, String process, String machine, Set<String> certifications, String availableFrom,
			String availableTo) {
		super();
		this.capacity = capacity;
		this.material = material;
		this.process = process;
		this.machine = machine;
		this.certifications = certifications;
		this.availableFrom = availableFrom;
		this.availableTo = availableTo;
	}
	
	public Resource(String material, String process, String machine, Set<String> certifications) {
		super();
		this.material = material;
		this.process = process;
		this.machine = machine;
		this.certifications = certifications;
	}
	
	public Resource() {}
	
	

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getMachine() {
		return machine;
	}

	public void setMachine(String machine) {
		this.machine = machine;
	}
	

	public Set<String> getCertifications() {
		return certifications;
	}

	public void setCertifications(Set<String> certifications) {
		this.certifications = certifications;
	}

	public String getAvailableFrom() {
		return availableFrom;
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

	@Override
	public String toString() {
		return "Resource [supplier=" + supplier + ", capacity=" + capacity + ", material=" + material + ", process="
				+ process + ", machine=" + machine + ", certifications=" + certifications + ", availableFrom="
				+ availableFrom + ", availableTo=" + availableTo + "]";
	}
	
	
	

}
