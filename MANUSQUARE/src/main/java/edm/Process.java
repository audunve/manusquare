package edm;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import utilities.StringUtilities;

public class Process {

	private String id;
	private String name;
	private String description;
	private String version;
	private Set<Material> materials;

	public Process(String id, String name, String description, String version, Set<Material> materials) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.version = version;
		this.materials = materials;
	}

	public Process(String name) {
		this.name = name;
	}

	public Process(String name, Set<Material> materials) {
		super();
		this.name = name;
		this.materials = materials;
	}

	public Process() {}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Set<Material> getMaterials() {
		return materials;
	}

	public void setMaterials(Set<Material> materials) {
		this.materials = materials;
	}
	
	//TODO: See if a better approach exist: Use an overridden equals method to ensure that processes with the same name are not contained within the same list.
	@Override
	public boolean equals (Object o) {
		if ( o instanceof Process && ((Process) o).getName().equals(this.name) ) {
			return true;
		} else {
			return false;
		}
	}

	
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    
    //a toString() method that prints processes along with relevant materials
    public String toString() {
    	
    	Set<String> materialNames = new HashSet<String>();
    	Set<Material> materials = this.getMaterials();
    	
    	for (Material material : materials) {
    		materialNames.add(material.getName());
    	}
    	
    	return this.name + " ( " + StringUtilities.printSetItems(materialNames) + " )";
    }
    


}
