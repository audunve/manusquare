package edm;

import java.util.Objects;

public class Material {
	
	private String name;

	public Material(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	//TODO: See if a better approach exist: Use an overridden equals method to ensure that materials with the same name are not contained within the same list.
	@Override
	public boolean equals (Object o) {
		if ( o instanceof Material && ((Material) o).getName().equals(this.name) ) {
			return true;
		} else {
			return false;
		}
	}
	
	  @Override
	    public int hashCode() {
	        return Objects.hash(name);
	    }
	
	

}
