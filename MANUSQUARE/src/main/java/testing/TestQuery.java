package testing;

import graph.Graph;
import importtestdata.CSV2Set;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import similarity.SimilarityMeasures;
import similarity.SimilarityMethods;
import utilities.MathUtils;
import utilities.StringUtilities;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class TestQuery {

    static File ontologyFile = new File("./files/manusquare-consumer.owl");
    static SimilarityMethods similarityMethod = SimilarityMethods.WU_PALMER;
    static String csv = "./files/CSV2Set3.csv";

    //test method
    public static void main(String[] args) throws OWLOntologyCreationException, IOException {

        //import and parse the owl file (throws Exception)
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology sourceOnto = manager.loadOntologyFromOntologyDocument(ontologyFile);

        Label label = Label.label(StringUtilities.stripPath(ontologyFile.toString()));

        //creates a new Neo4J db and a new ontology graph
        Graph.createOntologyGraph(ontologyFile);

        //dummy consumer query
        ConsumerQuery query = new ConsumerQuery();
        query.setRequiredProcess("GasTungstenArcWelding");
        query.setRequiredMaterial("AluminiumAlloy");
        query.setRequiredMachine("WeldingMachine");
        query.setRequiredCertificates("ISO9001");
        query.setQuantity(80);

        //retrieve dummy supplier resources from testdata from Hans de Man
        Set<SupplierResource> resources = CSV2Set.createSupplierResourceRecords(csv);

        //holds a mapping between SupplierResource objects and the similarity score
        Map<SupplierResource, Double> ranking = new HashMap<>();

        double semanticSim = 0;
        for (SupplierResource resource : resources) {
            semanticSim = SimilarityMeasures.computeSemanticSimilarity(query, resource, label, sourceOnto, similarityMethod);
            ranking.put(resource, semanticSim);
        }

        //sort by similarity scores
        Map<SupplierResource, Double> sortedSuppliers = sortByValues(ranking);

        System.out.println("Ranked list of supplier given the consumer query (Process: " + query.getRequiredProcess() + ", Material: " + query.getRequiredMaterial() + ", Machine: " + query.getRequiredMachine() + ")");
        System.out.println("\n");

        for (Entry<SupplierResource, Double> e : sortedSuppliers.entrySet()) {
            System.out.println("Supplier-Id:" + e.getKey().getId() + " (Supplier: " + e.getKey().getSupplierName() + ", Process: "
                    + e.getKey().getUsedProcess() + ", Material: " + e.getKey().getUsedMaterial() + ", Machine: " + e.getKey().getUsedMachine() + ") : " + MathUtils.round(e.getValue(), 2));
        }
    }


    /**
     * Sorts a map based on similarity scores (values in the map)
     *
     * @param map
     * @return May 16, 2019
     */
    private static <K, V extends Comparable<V>> Map<K, V> sortByValues(final Map<K, V> map) {
        Comparator<K> valueComparator = (k1, k2) -> {
            int compare = map.get(k2).compareTo(map.get(k1));
            if (compare == 0) return 1;
            else return compare;
        };
        Map<K, V> sortedByValues = new TreeMap<>(valueComparator);
        sortedByValues.putAll(map);
        return sortedByValues;
    }

}
