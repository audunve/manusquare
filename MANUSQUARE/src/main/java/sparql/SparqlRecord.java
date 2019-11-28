package sparql;

public class SparqlRecord {
	
	private String processChainId;
	private String supplierId;
	private String process;
	private String material;
	private String certification;
	
	public SparqlRecord(String processChainId, String supplierId, String process, String material,
			String certification) {
		super();
		this.processChainId = processChainId;
		this.supplierId = supplierId;
		this.process = process;
		this.material = material;
		this.certification = certification;
	}
	
	public SparqlRecord(String supplierId, String process, String material,
			String certification) {
		super();
		this.supplierId = supplierId;
		this.process = process;
		this.material = material;
		this.certification = certification;
	}
	
	public SparqlRecord () {}

	public String getProcessChainId() {
		return processChainId;
	}

	public void setProcessChainId(String processChainId) {
		this.processChainId = processChainId;
	}

	public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getCertification() {
		return certification;
	}

	public void setCertification(String certification) {
		this.certification = certification;
	}
	
	
	
	
	

}
