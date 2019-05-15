package importtestdata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import testing.SupplierResource;

public class CSV2Set {


	public static void main(String[] args) throws IOException {

		String csvFile = "./files/CSV2Set.csv";

		Set<SupplierResource> resources = createSupplierResourceRecords(csvFile);

		System.out.println("The set contains " + resources.size() + " records.");

	}

	public static Set<SupplierResource> createSupplierResourceRecords(String csvFilePath) throws IOException {

		Set<SupplierResource> resources = new HashSet<SupplierResource>();

		SupplierResource resource;

		BufferedReader br = new BufferedReader(new FileReader(csvFilePath));

		String line = br.readLine();

		String[] params = null;

		while (line != null) {
			params = line.split(";");

			resource = new SupplierResource();

			resource.setId(params[0]);
			resource.setSupplierName(params[1]);
			resource.setNation(params[2]);
			resource.setCity(params[3]);
			resource.setPromisedproductionLeadTime(Integer.parseInt(params[4]));
			resource.setPromisedRFQResponseTime(Integer.parseInt(params[5]));
			resource.setCadCapability(params[6]);

			if (params[7].equals("1")) {			
				resource.setPosessedCertificates("ISO9001");
			} else {
				resource.setPosessedCertificates(null);
			}

			resource.setCapacity(Integer.parseInt(params[8]));
			resource.setUsedMachine(params[9]);
			resource.setUsedMaterial(params[10]);
			resource.setUsedProcess(params[11]);
			resource.setAvailableFrom(params[12]);
			resource.setAvailableTo(params[13]);


			resources.add(resource);
			line = br.readLine();

		}

		br.close();

		return resources;

	}



}
