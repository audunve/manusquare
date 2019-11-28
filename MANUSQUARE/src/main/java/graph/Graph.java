package graph;

import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import owlprocessing.OntologyOperations;
import utilities.StringUtilities;

import java.io.File;
import java.sql.Timestamp;
import java.util.*;
import java.util.Map.Entry;

public class Graph {

	private static GraphDatabaseService db;
	private final static String key = "classname";

	public Graph(GraphDatabaseService db) {
		Graph.db = db;
	}

	/**
	 * Finds the lowest common subsumer of the source- and target node
	 *
	 * @param sourceNode
	 * @param targetNode
	 * @param label
	 * @return May 14, 2019
	 */
	public static Node findLCS(Node sourceNode, Node targetNode, Label label) {

		ArrayList<Node> parentsToSource = Graph.getAllParentNodes(sourceNode, label);
		ArrayList<Node> parentsToTarget = Graph.getAllParentNodes(targetNode, label);
		ArrayList<Node> commonParentsList = new ArrayList<>();

		//TODO: If sourceNode is a parent to target or vice versa I suppose this should be considered as LCS?
		//Or, if they are the same node
		for (Node s : parentsToSource) {
			for (Node t : parentsToTarget) {
				if (s.equals(t)) {
					commonParentsList.add(s);
				}
			}
		}


		//find the common parent with the highest depth (i.e. closest to source and target nodes)
		int maxDepth = 0;
		int depth = 0;
		Node LCS = null;
		for (Node o : commonParentsList) {
			depth = Graph.findDistanceToRoot(o);
			if (depth >= maxDepth) {
				LCS = (Node) o;
				maxDepth = depth;
			}
		}

		return LCS;
	}
	
	/**
	 * This method creates a Neo4J graph from an input ontology - with a defined label
	 *
	 * @param OWLOntology          onto
	 * @param Label                label
	 * @param GraphDatabaseService db
	 * @throws OWLOntologyCreationException
	 */
	public static void createOntologyGraphNonNeo4j(File ontoFile, Label label) throws OWLOntologyCreationException {

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology onto = manager.loadOntologyFromOntologyDocument(ontoFile);

		Map<String, String> superClassMap = OntologyOperations.getClassesAndSuperClassesUsingPellet(onto);
		
		System.out.println("Printing superClassMap (" + superClassMap.size() + ")");
		for (Entry<String, String> e : superClassMap.entrySet()) {
			System.out.println("Class: " + e.getKey() + ", SuperClass: " + e.getValue());
		}

		Set<String> classes = superClassMap.keySet();
		Iterator<String> itr = classes.iterator();

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String dbName = String.valueOf(timestamp.getTime());

		File dbFile = new File("/Users/audunvennesland/Documents/phd/development/Neo4J_new/" + dbName);

		//load neo4j config data (e.g. http access) from configuration file
		String pathToConfig = "/Users/audunvennesland/Documents/phd/development/Neo4J_new/conf/";
		db = (GraphDatabaseService) new GraphDatabaseFactory().
				newEmbeddedDatabaseBuilder(dbFile)
				.loadPropertiesFromFile( pathToConfig + "neo4j.conf" )
				.newGraphDatabase();

		try (Transaction tx = db.beginTx()) {
			// creating a node for owl:Thing
			Node thingNode = db.createNode(label);
			thingNode.setProperty(key, "owl:Thing");

			// create nodes from the ontology, that is, create nodes and give them
			// properties (classname) according to their ontology names
			while (itr.hasNext()) {
				Node classNode = db.createNode(label);
				classNode.setProperty(key, itr.next());
			}

			// create isA relationships between classes and their superclasses
			ResourceIterable<Node> testNode = db.getAllNodes();

			// iterate through the nodes of the graph database
			for (Node n : testNode) {
				if (n.hasProperty(key)) {
					String thisClassName = n.getProperty(key).toString();
					String superClass = null;
					// check if thisClassName equals any of the keys in superClassMap
					for (Entry<String, String> entry : superClassMap.entrySet()) {
						// if this graph node matches a key in the map...
						if (thisClassName.equals(entry.getKey())) {
							// get the superclass that belongs to the key in the map
							superClass = superClassMap.get(entry.getKey());
							// find the "superclass-node" that matches the map value belonging to this key
							// class
							Node superClassNode = db.findNode(label, key,
									superClassMap.get(thisClassName));
							// create an isA relationship from this graph node to its superclass
							// if a class does not have any defined super-classes, create an isA
							// relationship to owl:thing
							if (superClassNode != null) {
								n.createRelationshipTo(superClassNode, RelTypes.isA);
							} else {
								n.createRelationshipTo(thingNode, RelTypes.isA);
							}
						}
					}
				}
			}

			tx.success();
		}

	}


	/**
	 * This method creates a Neo4J graph from an input ontology - with a defined label
	 *
	 * @param OWLOntology          onto
	 * @param Label                label
	 * @param GraphDatabaseService db
	 * @throws OWLOntologyCreationException
	 */
	public static void createOntologyGraph(File ontoFile, Label label) throws OWLOntologyCreationException {

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology onto = manager.loadOntologyFromOntologyDocument(ontoFile);

		Map<String, String> superClassMap = OntologyOperations.getClassesAndSuperClassesUsingPellet(onto);
		
		System.out.println("Printing superClassMap (" + superClassMap.size() + ")");
		for (Entry<String, String> e : superClassMap.entrySet()) {
			System.out.println("Class: " + e.getKey() + ", SuperClass: " + e.getValue());
		}

		Set<String> classes = superClassMap.keySet();
		Iterator<String> itr = classes.iterator();

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String dbName = String.valueOf(timestamp.getTime());

		File dbFile = new File("/Users/audunvennesland/Documents/phd/development/Neo4J_new/" + dbName);

		System.err.println("Creating a new NEO4J database...");

		//load neo4j config data (e.g. http access) from configuration file
		String pathToConfig = "/Users/audunvennesland/Documents/phd/development/Neo4J_new/conf/";
		db = (GraphDatabaseService) new GraphDatabaseFactory().
				newEmbeddedDatabaseBuilder(dbFile)
				.loadPropertiesFromFile( pathToConfig + "neo4j.conf" )
				.newGraphDatabase();

		registerShutdownHook(db);

		System.err.println("Created a new NEO4J data base at " + dbFile.getPath());

		try (Transaction tx = db.beginTx()) {
			// creating a node for owl:Thing
			Node thingNode = db.createNode(label);
			thingNode.setProperty(key, "owl:Thing");

			// create nodes from the ontology, that is, create nodes and give them
			// properties (classname) according to their ontology names
			while (itr.hasNext()) {
				Node classNode = db.createNode(label);
				classNode.setProperty(key, itr.next());
			}

			// create isA relationships between classes and their superclasses
			ResourceIterable<Node> testNode = db.getAllNodes();

			// iterate through the nodes of the graph database
			for (Node n : testNode) {
				if (n.hasProperty(key)) {
					String thisClassName = n.getProperty(key).toString();
					String superClass = null;
					// check if thisClassName equals any of the keys in superClassMap
					for (Entry<String, String> entry : superClassMap.entrySet()) {
						// if this graph node matches a key in the map...
						if (thisClassName.equals(entry.getKey())) {
							// get the superclass that belongs to the key in the map
							superClass = superClassMap.get(entry.getKey());
							// find the "superclass-node" that matches the map value belonging to this key
							// class
							Node superClassNode = db.findNode(label, key,
									superClassMap.get(thisClassName));
							// create an isA relationship from this graph node to its superclass
							// if a class does not have any defined super-classes, create an isA
							// relationship to owl:thing
							if (superClassNode != null) {
								n.createRelationshipTo(superClassNode, RelTypes.isA);
							} else {
								n.createRelationshipTo(thingNode, RelTypes.isA);
							}
						}
					}
				}
			}

			tx.success();
		}

	}

	public static String getNodeName(Node n) {
		String value = null;
		try (Transaction tx = db.beginTx()) {
			value = n.getProperty(key).toString();
			tx.success();
		}
		return value;
	}

	/**
	 * Returns a graph node given a label, a property name and property value
	 *
	 * @param value
	 * @param label a label represents the graph/ontology to process
	 * @return the node searched for
	 */
	public static Node getNode(String value, Label label) {
		Node testNode = null;

		try (Transaction tx = db.beginTx()) {
			testNode = db.findNode(label, key, value);
			tx.success();
		}
		return testNode;

	}

	/**
	 * Returns the ID of a node given the Node instance as parameter
	 *
	 * @param n a Node instance
	 * @return the ID of a node as a long
	 */
	public long getNodeID(Node n) {
		long id = 0;
		try (Transaction tx = db.beginTx()) {
			id = n.getId();
			tx.success();
		}
		return id;
	}

	/**
	 * Returns a Traverser that traverses the children of a node given a Node
	 * instance as parameter
	 *
	 * @param classNode a Node instance
	 * @return a traverser
	 */
	public static Traverser getChildNodesTraverser(Node classNode) {
		TraversalDescription td = null;
		try (Transaction tx = db.beginTx()) {
			td = db.traversalDescription().breadthFirst().relationships(RelTypes.isA, Direction.INCOMING)
					.evaluator(Evaluators.excludeStartPosition());
			tx.success();
		}
		return td.traverse(classNode);
	}

	/**
	 * Returns an ArrayList of all child nodes of a node
	 *
	 * @param classNode a Node instance
	 * @param label     representing the graph/ontology to process
	 * @return
	 */
	public static ArrayList<Object> getClosestChildNodesAsList(Node classNode, Label label) {
		ArrayList<Object> childNodeList = new ArrayList<Object>();
		Traverser childNodesTraverser = null;

		try (Transaction tx = db.beginTx()) {
			childNodesTraverser = getChildNodesTraverser(classNode);
			for (Path childNodePath : childNodesTraverser) {
				if (childNodePath.length() == 1 && childNodePath.endNode().hasLabel(label)) {
					childNodeList.add(childNodePath.endNode().getProperty(key));
				}
			}
			tx.success();
		}
		return childNodeList;
	}

	/**
	 * Returns the number of children a particular node in the graph has
	 *
	 * @param classNode
	 * @param label
	 * @return Feb 4, 2019
	 */
	public static int getNumChildNodes(Node classNode, Label label) {
		ArrayList<Object> childNodeList = new ArrayList<Object>();
		Traverser childNodesTraverser = null;

		try (Transaction tx = db.beginTx()) {
			childNodesTraverser = getChildNodesTraverser(classNode);
			for (Path childNodePath : childNodesTraverser) {
				if (childNodePath.endNode().hasLabel(label)) {
					childNodeList.add(childNodePath.endNode().getProperty(key));
					//System.out.println("Adding child-node " + childNodePath.endNode().getProperty(key) + " to list");
				}
			}
			tx.success();
		}
		return childNodeList.size();
	}

	/**
	 * Returns a Traverser that traverses the parents of a node given a Node
	 * instance as parameter
	 *
	 * @param classNode a Node instance
	 * @return a traverser
	 */
	public static Traverser getParentNodeTraverser(Node classNode) {

		TraversalDescription td = null;

		try (Transaction tx = db.beginTx()) {

			td = db.traversalDescription().breadthFirst().relationships(RelTypes.isA, Direction.OUTGOING)
					.evaluator(Evaluators.excludeStartPosition());

			tx.success();

		}

		return td.traverse(classNode);
	}

	// TODO: Why is this an ArrayList and not a Node being returned?

	/**
	 * Returns an ArrayList holding the parent node of the node provided as
	 * parameter
	 *
	 * @param classNode a node for which the closest parent is to be returned
	 * @param label     a label representing the graph (ontology) to process
	 * @return the closest parent node
	 */
	public static ArrayList<Object> getClosestParentNode(Node classNode, Label label) {

		ArrayList<Object> parentNodeList = new ArrayList<Object>();
		Traverser parentNodeTraverser = null;

		try (Transaction tx = db.beginTx()) {

			parentNodeTraverser = getParentNodeTraverser(classNode);

			for (Path parentNodePath : parentNodeTraverser) {
				if (parentNodePath.length() == 1 && parentNodePath.endNode().hasLabel(label)) {
					parentNodeList.add(parentNodePath.endNode().getProperty(key));
				}
			}

			tx.success();

		}

		return parentNodeList;
	}


	/**
	 * Returns an ArrayList holding all parent nodes to the Node provided as
	 * parameter
	 *
	 * @param classNode the Node for which all parent nodes are to be retrieved
	 * @param label     representing the graph/ontology to process
	 * @return all parent nodes to node provided as parameter
	 */
	public static ArrayList<Node> getAllParentNodes(Node classNode, Label label) {

		//System.err.println("Graph: Getting parent nodes of " + getNodeName(classNode) + " with label: " + label.toString());

		ArrayList<Node> parentNodeList = new ArrayList<Node>();
		Traverser parentNodeTraverser = null;

		try (Transaction tx = db.beginTx()) {

			parentNodeTraverser = getParentNodeTraverser(classNode);

			for (Path parentNodePath : parentNodeTraverser) {
				if (parentNodePath.endNode().hasLabel(label)) {
					parentNodeList.add(parentNodePath.endNode());

				}

			}

			tx.success();

		}

		return parentNodeList;
	}

	/**
	 * This method finds the shortest path between two nodes used as parameters. The
	 * path is the full path consisting of nodes and relationships between the
	 * classNode.. ...and the parentNode.
	 *
	 * @param parentNode
	 * @param classNode
	 * @param label
	 * @param rel
	 * @return Iterable<Path> paths
	 */
	public static Iterable<Path> findShortestPathBetweenNodes(Node parentNode, Node classNode,
			RelationshipType rel) {
		PathFinder<Path> finder = GraphAlgoFactory.shortestPath(PathExpanders.forType(rel), 15);
		return finder.findAllPaths(classNode, parentNode);

	}

	/**
	 * Returns the distance from the Node provided as parameter and the root node
	 * (i.e. owl:Thing) We use a Map as a work-around to counting the edges between
	 * a given node and the root (owl:Thing). This is possible since a Map only
	 * allows unique keys and a numbered Neo4J path consists of a set of path items
	 * <edge-count, node (property)> where all nodes for each edge-count is listed
	 * (e.g. for the node "AcademicArticle" the upwards path is <1, Article>, <2,
	 * Document>, <3, owl:Thing>).
	 *
	 * @param classNode
	 * @return
	 */
	public static int findDistanceToRoot(Node classNode) {
		Traverser parentNodeTraverser = null;
		Map<Object, Object> parentNodeMap = new HashMap<>();
		try (Transaction tx = db.beginTx()) {
			parentNodeTraverser = getParentNodeTraverser(classNode);
			for (Path parentNodePath : parentNodeTraverser) {
				parentNodeMap.put(parentNodePath.length(), parentNodePath.endNode().getProperty(key));
			}
			tx.success();
		}
		return parentNodeMap.size();
	}

	//The depth of a node is the number of parent nodes (including owl:thing) - 1
	public static int findNodeDepth(Node node) {
		return findDistanceToRoot(node) - 1;
	}

	/**
	 * This method finds the shortest path between two nodes used as parameters. The
	 * path is the full path consisting of nodes and relationships between the
	 * classNode.. ...and the rootNode.
	 *
	 * @param rootNode
	 * @param classNode
	 * @param rel
	 * @return Iterable<Path> paths
	 */
	public Iterable<Path> findShortestPathToRoot(Node rootNode, Node classNode, RelationshipType rel) {
		PathFinder<Path> finder = GraphAlgoFactory.shortestPath(PathExpanders.forType(rel), 15);
		Iterable<Path> paths = finder.findAllPaths(classNode, rootNode);
		return paths;
	}

	/**
	 * Registers a shutdown hook for the Neo4j instance so that it shuts down nicely
	 * when the VM exits
	 *
	 * @param graphDb
	 */
	private static void registerShutdownHook(final GraphDatabaseService graphDb) {
		Runtime.getRuntime().addShutdownHook(new Thread(graphDb::shutdown));
	}

	public enum RelTypes implements RelationshipType {
		isA
	}

	public static <K, V extends Comparable<V>> V findMapMax(Map<K, V> map) {
		Entry<K, V> maxEntry = Collections.max(map.entrySet(), Comparator.comparing(Entry::getValue));
		return maxEntry.getValue();
	}

}