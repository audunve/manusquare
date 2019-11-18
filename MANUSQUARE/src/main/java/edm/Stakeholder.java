package edm;

import java.util.Set;

public class Stakeholder {
	
	private String name;
	private String address;
	private String city;
	private String nation;
	private Set<Certification> certifications;
	
	
	public Stakeholder(String name, String address, String city, String nation, Set<Certification> certifications) {
		super();
		this.name = name;
		this.address = address;
		this.city = city;
		this.nation = nation;
		this.certifications = certifications;
	}

	public Stakeholder() {}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public Set<Certification> getCertifications() {
		return certifications;
	}

	public void setCertifications(Set<Certification> certifications) {
		this.certifications = certifications;
	}
		

}
