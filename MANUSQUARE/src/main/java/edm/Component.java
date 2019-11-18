package edm;

import java.util.Set;

public class Component extends Item {
	
	private Set<Material> materials;

	public Component(Set<Material> materials) {
		super(null);
		this.materials = materials;
	}

	public Set<Material> getMaterials() {
		return materials;
	}

	public void setMaterials(Set<Material> materials) {
		this.materials = materials;
	}
	
	
	

}
