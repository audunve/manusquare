package testing;

public class ConsumerQuery {
	
	private String city;
	private String nation;
	private String product;
	private int quantity;
	private double height;
	private double width;
	private double depth;
	private String requiredProcess;
	private String requiredMaterial;
	private String requiredMachine;
	private int RFQResponseTime;
	private int productionLeadTime;
	private String requiredCertificates;
	
	public ConsumerQuery(String city, String nation, String product, int quantity, double height, double width,
			double depth, String requiredProcess, String requiredMaterial, String requiredMachine, int rFQResponseTime,
			int productionLeadTime, String requiredCertificates) {
		super();
		this.city = city;
		this.nation = nation;
		this.product = product;
		this.quantity = quantity;
		this.height = height;
		this.width = width;
		this.depth = depth;
		this.requiredProcess = requiredProcess;
		this.requiredMaterial = requiredMaterial;
		this.requiredMachine = requiredMachine;
		RFQResponseTime = rFQResponseTime;
		this.productionLeadTime = productionLeadTime;
		this.requiredCertificates = requiredCertificates;
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

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
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

	public int getRFQResponseTime() {
		return RFQResponseTime;
	}

	public void setRFQResponseTime(int rFQResponseTime) {
		RFQResponseTime = rFQResponseTime;
	}

	public int getProductionLeadTime() {
		return productionLeadTime;
	}

	public void setProductionLeadTime(int productionLeadTime) {
		this.productionLeadTime = productionLeadTime;
	}

	public String getRequiredCertificates() {
		return requiredCertificates;
	}

	public void setRequiredCertificates(String requiredCertificates) {
		this.requiredCertificates = requiredCertificates;
	}
	
	
	

}
