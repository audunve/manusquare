package utilities;

import net.didion.jwnl.JWNLException;
import org.apache.commons.lang3.StringUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtilities {
    static OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    static OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
    
    /**
	 * Returns ONE string by random from a list of strings
	 * @param listOfStrings
	 * @return
	   Jul 2, 2019
	 */
	public static String getRandomString1(List<String> listOfStrings) {
		Random rand = new Random();
		String returnedString = null;

		int numberOfElements = 1;

		for (int i = 0; i < numberOfElements; i++) {
			int randomIndex = rand.nextInt(listOfStrings.size());
			returnedString = listOfStrings.get(randomIndex);

		}

		return returnedString;
	}

	/**
	 * Returns THREE string by random from a list of strings
	 * @param listOfStrings
	 * @return
	   Jul 2, 2019
	 */
	public static Set<String> getRandomString3(List<String> listOfStrings) {
		Random rand = new Random();
		Set<String> returnedStrings = new HashSet<String>();

		int numberOfElements = 3;

		for (int i = 0; i < numberOfElements; i++) {
			int randomIndex = rand.nextInt(listOfStrings.size());
			returnedStrings.add(listOfStrings.get(randomIndex));
		}

		return returnedStrings;
	}

	/**
	 * Returns ONE integer by random from a list of integers
	 * @param list
	 * @return
	   Jul 2, 2019
	 */
	public static int getRandomInt1(List<Integer> list) {
		Random rand = new Random();
		int returnedString = 0;

		int numberOfElements = 1;

		for (int i = 0; i < numberOfElements; i++) {
			int randomIndex = rand.nextInt(list.size());
			returnedString = list.get(randomIndex);

		}

		return returnedString;
	}

    public static String splitCompounds(String input) {
        String[] compounds = input.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
        StringBuilder sb = new StringBuilder();

        for (String compound : compounds) {
            sb.append(compound).append(" ");
        }
        return sb.toString();
    }

    public static void fixAlignmentName(String folder) throws IOException {
        File allFiles = new File(folder);
        File[] files = allFiles.listFiles();
        System.err.println("Number of files: " + Objects.requireNonNull(files).length);

        String fileName = null;
        File newFile = null;

        for (File file : files) {
            fileName = file.getName();
            file.renameTo(new File(fileName = "./files/DBLP-Scholar/alignments/new/" + fileName.replaceAll("[^a-zA-Z0-9.-]", "_")));
        }
    }

    /**
     * Takes a string as input and returns an arraylist of tokens from this string
     *
     * @param s:         the input string to tokenize
     * @param lowercase: if the output tokens should be lowercased
     * @return an ArrayList of tokens
     */
    public static ArrayList<String> tokenize(String s, boolean lowercase) {
        if (s == null) {
            return null;
        }

        ArrayList<String> strings = new ArrayList<>();
        performTokinization(s, lowercase, strings);
        return strings;
    }

    private static void performTokinization(String s, boolean lowercase, Collection<String> strings) {
        String current = "";
        Character prevC = 'x';
        for (Character c : s.toCharArray()) {
            if ((Character.isLowerCase(prevC) && Character.isUpperCase(c)) ||
                    c == '_' || c == '-' || c == ' ' || c == '/' || c == '\\' || c == '>') {
                current = current.trim();
                if (current.length() > 0) {
                    if (lowercase)
                        strings.add(current.toLowerCase());
                    else
                        strings.add(current);
                }
                current = "";
            }

            if (c != '_' && c != '-' && c != '/' && c != '\\' && c != '>') {
                current += c;
                prevC = c;
            }
        }

        current = current.trim();
        if (current.length() > 0) {
            // this check is to handle the id numbers in YAGO
            if (!(current.length() > 4 && Character.isDigit(current.charAt(0)) &&
                    Character.isDigit(current.charAt(current.length() - 1)))) {
                strings.add(current.toLowerCase());
            }
        }
    }

    /**
     * Takes a string as input and returns set of tokens from this string
     *
     * @param s:         the input string to tokenize
     * @param lowercase: if the output tokens should be lowercased
     * @return a set of tokens
     */
    public static Set<String> tokenizeToSet(String s, boolean lowercase){
        if (s == null) {
            return null;
        }

        String stringWOStopWords = removeStopWords(s);
        Set<String> strings = new HashSet<>();
        performTokinization(stringWOStopWords, lowercase, strings);
        return strings;
    }


    /**
     * Returns a string of tokens
     *
     * @param s:         the input string to be tokenized
     * @param lowercase: whether the output tokens should be in lowercase
     * @return a string of tokens from the input string
     */
    public static String stringTokenize(String s, boolean lowercase) {
        StringBuilder result = new StringBuilder();
        ArrayList<String> tokens = tokenize(s, lowercase);
        for (String token : tokens) {
            result.append(token).append(" ");
        }
        return result.toString().trim();
    }


    /**
     * Removes prefix from property names (e.g. hasCar is transformed to car)
     *
     * @param s: the input property name to be
     * @return a string without any prefix
     */
    public static String stripPrefix(String s) {
        if (s.startsWith("has")) {
            s = s.replaceAll("^has", "");
        } else if (s.startsWith("is")) {
            s = s.replaceAll("^is", "");
        } else if (s.startsWith("is_a_")) {
            s = s.replaceAll("^is_a_", "");
        } else if (s.startsWith("has_a_")) {
            s = s.replaceAll("^has_a_", "");
        } else if (s.startsWith("was_a_")) {
            s = s.replaceAll("^was_a_", "");
        } else if (s.endsWith("By")) {
            s = s.replaceAll("By", "");
        } else if (s.endsWith("_by")) {
            s = s.replaceAll("_by^", "");
        } else if (s.endsWith("_in")) {
            s = s.replaceAll("_in^", "");
        } else if (s.endsWith("_at")) {
            s = s.replaceAll("_at^", "");
        }
        s = s.replaceAll("_", " ");
        s = stringTokenize(s, true);

        return s;
    }

    //line1 = line1.replace("\"", "");
    public static String removeSymbols(String s) {
        s = s.replace("\"", "");
        s = s.replace(".", "");
        s = s.replace("@en", "");

        return s;
    }

    /**
     * Takes a filename as input and removes the IRI prefix from this file
     *
     * @param fileName
     * @return filename - without IRI
     */
    public static String stripPath(String fileName) {
        return fileName.substring(fileName.lastIndexOf("/") + 1);

    }

//	/**
//	 * Takes a string as input, tokenizes it, and removes stopwords from this string
//	 * @param analyzer
//	 * @param str
//	 * @return results - as a string of tokens, without stopwords
//	 */
//	public static String tokenize(Analyzer analyzer, String str) {
//		String result = null;
//		StringBuilder sb = new StringBuilder();
//
//		try {
//			TokenStream stream  = analyzer.tokenStream(null, new StringReader(str));
//			stream.reset();
//			while (stream.incrementToken()) {
//				sb.append(stream.getAttribute(CharTermAttribute.class).toString());
//				sb.append(" ");
//			}
//			stream.close();
//		} catch (IOException e) {
//
//			throw new RuntimeException(e);
//		}
//
//
//		result = sb.toString();
//		return result;
//	}


    /**
     * Returns the label from on ontology concept without any prefix
     *
     * @param label: an input label with a prefix (e.g. an IRI prefix)
     * @return a label without any prefix
     */
    public static String getString(String label) {
        if (label.contains("#")) {
            label = label.substring(label.indexOf('#') + 1);
            return label;
        }

        if (label.contains("/")) {
            label = label.substring(label.lastIndexOf('/') + 1);
            return label;
        }
        return label;
    }
    

    /**
     * Removes underscores from a string (replaces underscores with "no space")
     *
     * @param input: string with an underscore
     * @return string without any underscores
     */
    public static String replaceUnderscore(String input) {
        String newString = null;
        Pattern p = Pattern.compile("_([a-zA-Z])");
        Matcher m = p.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, m.group(1).toUpperCase());
        }

        m.appendTail(sb);
        newString = sb.toString();

        return newString;
    }

    /**
     * Checks if an input string is an abbreviation (by checking if there are two consecutive uppercased letters in the string)
     *
     * @param s input string
     * @return boolean stating whether the input string represents an abbreviation
     */
    public static boolean isAbbreviation(String s) {

        boolean isAbbreviation = false;
        int counter = 0;
        for (int i = 0; i < s.length(); i++) {
            if (Character.isUpperCase(s.charAt(i))) {
                counter++;
            }
            isAbbreviation = counter > 2;
        }
        return isAbbreviation;
    }

    /**
     * Returns the names of the ontology from the full file path (including owl or rdf suffixes)
     *
     * @param ontology name without suffix
     * @return
     */
    public static String stripOntologyName(String fileName) {
        String trimmedPath = fileName.substring(fileName.lastIndexOf("/") + 1);
        String owl = ".owl";
        String rdf = ".rdf";
        String stripped = null;

        if (fileName.endsWith(".owl")) {
            stripped = trimmedPath.substring(0, trimmedPath.indexOf(owl));
        } else {
            stripped = trimmedPath.substring(0, trimmedPath.indexOf(rdf));
        }

        return stripped;
    }

    /**
     * Returns the full IRI of an input ontology
     *
     * @param o the input OWLOntology
     * @return the IRI of an OWLOntology
     */
    public static String getOntologyIRI(OWLOntology o) {
        return o.getOntologyID().getOntologyIRI().toString();
    }

    /**
     * Convert from a filename to a file URL.
     */
    public static String convertToFileURL(String filename) {
        String path = new File(filename).getAbsolutePath();
        if (File.separatorChar != '/') {
            path = path.replace(File.separatorChar, '/');
        }

        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return "file:" + path;
    }

    public static String validateRelationType(String relType) {
        if (relType.equals("<")) {
            relType = "&lt;";
        }
        return relType;
    }

    //NOTE: There is a Lucene conflict between this implementation of Lucene and the Lucene version used by Neo4J. When running this method the Lucene import declarations in the POM.xml file needs to be "uncommented", and...
    //...when running Neo4J they have to be commented out.
	  
	/* public static String removeStopWordsfromFile(File inputFile) throws IOException {

		StringBuilder tokens = new StringBuilder();

		FileUtils fs = new FileUtils();

		String text = fs.readFileToString(inputFile);

		Analyzer analyzer = new StopAnalyzer(Version.LUCENE_36);
		TokenStream tokenStream = analyzer.tokenStream(
				LuceneConstants.CONTENTS, new StringReader(text));
		TermAttribute term = tokenStream.addAttribute(TermAttribute.class);
		while(tokenStream.incrementToken()) {
			tokens.append(term + " ");
		}

		String tokenizedText = tokens.toString();
		return tokenizedText;

	}*/

//	public static String removeStopWordsFromString(String inputText) throws IOException {
//
//		StringBuilder tokens = new StringBuilder();
//
//
//		Analyzer analyzer = new StopAnalyzer(Version.LUCENE_36);
//		TokenStream tokenStream = analyzer.tokenStream(
//				LuceneConstants.CONTENTS, new StringReader(inputText));
//		TermAttribute term = tokenStream.addAttribute(TermAttribute.class);
//		while(tokenStream.incrementToken()) {
//			tokens.append(term + " ");
//		}
//
//		String tokenizedText = tokens.toString();
//		return tokenizedText;
//
//	}
	/*
	public static void remStopWords(String inputText) throws IOException {

		StringBuilder tokens = new StringBuilder();


		Analyzer analyzer = new StopAnalyzer(Version.LUCENE_36);
		TokenStream tokenStream = analyzer.tokenStream(
				LuceneConstants.CONTENTS, new StringReader(inputText));
		TermAttribute term = tokenStream.addAttribute(TermAttribute.class);
		while(tokenStream.incrementToken()) {
			tokens.append(term + " ");
		}

		inputText = tokens.toString();


	}*/

    public static String removeStopWords(String inputString) {
        List<String> stopWordsList = Arrays.asList(
                "a", "an", "and", "are", "as", "at", "be", "but", "by",
                "for", "if", "in", "into", "is", "it",
                "no", "not", "of", "on", "or", "such",
                "that", "the", "their", "then", "there", "these",
                "they", "this", "to", "was", "will", "with"
        );

        String[] words = inputString.split(" ");
        ArrayList<String> wordsList = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            String wordCompare = word.toLowerCase();
            if (!stopWordsList.contains(wordCompare)) {
                wordsList.add(word);
            }
        }

        for (String str : wordsList) {
            sb.append(str).append(" ");
        }

        return sb.toString();
    }

    /**
     * Takes as input a String and produces an array of Strings from this String
     *
     * @param s
     * @return result
     */
    public static String[] split(String s) {
        return s.split(" ");
    }

    public static boolean isCompoundWord(String s) {
        String[] compounds = s.split("(?<=.)(?=\\p{Lu})");
        return compounds.length > 1 && !StringUtils.isAllUpperCase(s);

    }

    public static String splitCompoundString(String s) {
        StringBuilder splitCompound = new StringBuilder();
        String[] compounds = s.split("(?<=.)(?=\\p{Lu})");
        for (String compound : compounds) {
            splitCompound.append(" ").append(compound);
        }

        return splitCompound.toString();

    }

    public static String getCompoundWordWithSpaces(String s) {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> compoundWordsList = getWordsFromCompound(s);
        for (String word : compoundWordsList) {
            sb.append(word).append(" ");
        }
        return sb.toString();
    }

    public static String getCompoundHead(String s) {
        if (isCompoundWord(s)) {
            String[] compounds = s.split("(?<=.)(?=\\p{Lu})");
            return compounds[compounds.length - 1];
        } else {
            return null;
        }
    }

    public static String getCompoundQualifier(String s) {
        String[] compounds = s.split("(?<=.)(?=\\p{Lu})");
        return compounds[0];
    }

    public static String getCompoundModifier(String s) {
        return s.replace(Objects.requireNonNull(getCompoundHead(s)), "");
    }

    public static ArrayList<String> getWordsFromCompound(String s) {
        String[] compounds = s.split("(?<=.)(?=\\p{Lu})");
        return new ArrayList<>(Arrays.asList(compounds));

    }

    public static Set<String> getWordsAsSetFromCompound(String s) {
        String[] compounds = s.split("(?<=.)(?=\\p{Lu})");
        return new HashSet<>(Arrays.asList(compounds));

    }

    public static int countCharsInString(String s) {
        int counter = 0;
        for (int i = 0; i < s.length(); i++) {
            if (Character.isLetter(s.charAt(i)))
                counter++;
        }
        return counter;
    }
    
    //FROM MANUSQUARE 
    
    /**
   	 * prints each (string) item in a set of items
   	 * @param certifications
   	 * @return sequenced string of certifications separated by commas
   	   Oct 12, 2019
   	 */
   	public static String printSetItems(Set<String> set) {
   		StringBuffer sb = new StringBuffer();
   		for (String s : set) {
   			sb.append(s + ", ");
   		}

   		String setItem = sb.deleteCharAt(sb.lastIndexOf(",")).toString();

   		return setItem;

   	}

    // ***Methods not in use***

    /*	*//**
     * Takes as input a Set of strings along with a separator (usually whitespace) and uses StringBuilder to create a string from the Set.
     * @param set
     * @param sep
     * @return result
     *//*
	public static String join(Set<String> set, String sep) {
		String result = null;
		if(set != null) {
			StringBuilder sb = new StringBuilder();
			Iterator<String> it = set.iterator();
			if(it.hasNext()) {
				sb.append(it.next());
			}
			while(it.hasNext()) {
				sb.append(sep).append(it.next());
			}
			result = sb.toString();
		}
		return result;
	}*/



    /*	*/

    /**
     * Takes as input two arrays of String and compares each string in one array with each string in the other array if they are equal
     *
     * @param s1
     * @param s2
     * @return results - basically an iterator that counts the number of equal strings in the two arrays
     * @throws OWLOntologyCreationException
     * @throws IOException
     * @throws JWNLException
     *//*
	public static int commonWords(String[] s1, String[] s2) {

		int results = 0;

		for (int i = 0; i < s1.length; i++) {
			for (int j = 0; j < s2.length; j++) {
				if (s1[i].equals(s2[j])) {
					results++;
				}
			}
		}

		return results;
	}*/

	/*	public static String removeDuplicates(String s) {

		return new LinkedHashSet<String>(Arrays.asList(s.split(" "))).toString().replaceAll("(^\\[|\\]$)", "").replace(", ", " ");


	}*/

	/*public static String getString(OWLEntity e, OWLOntology ontology) {

		String label = e.getIRI().toString();

		if (label.contains("#")) {
			label = label.substring(label.indexOf('#')+1);
			return label;
		}

		if (label.contains("/")) {
			label = label.substring(label.lastIndexOf('/')+1);
			return label;
		}

		Set<OWLAnnotation> labels = e.getAnnotations(ontology);
		//.getAnnotationPropertiesInSignature();

		if (labels != null && labels.size() > 0) {
			label = ((OWLAnnotation) labels.toArray()[0]).getValue().toString();
			if (label.startsWith("\"")) {
				label = label.substring(1);
			}

			if (label.contains("\"")) {
				label = label.substring(0, label.lastIndexOf('"'));
			}
		}

		return label;
	}*/
    public static void main(String args[]) throws OWLOntologyCreationException, IOException, JWNLException {
        String word = "MusicPublisherNumber";
        System.out.println("The modifier is " + getCompoundModifier(word));
//		
//		
//		String test = "regularly";
//		System.out.println("The lemma of " + test + " is " + getLemma(test));
//		
//		//public static String stringTokenize(String s, boolean lowercase) {
//		
//		System.out.println("This is the tokenized version of the gloss where stopwords are removed: " + removeStopWords(stringTokenize(WordNet.getGloss("publication"), true)));
//		
//		//public static Set<String> tokenizeToSet(String s, boolean lowercase) throws IOException {
//		System.out.println("\nThis is the set version of the gloss");
//		Set<String> glossSet = tokenizeToSet(WordNet.getGloss("publication"), true);
//		for (String s : glossSet) {
//			System.out.println(s);
//		}
//		
//		String s = "cloudLayer";
//		String t = "cloud";
//		
//		System.out.println("Testing stringTokenize: " + stringTokenize(s, true));
//		
//		String u = getCompoundQualifier(s);
//		System.out.println(u);
//		
//		String v = "flight-ExclusionSpec";
//		System.out.println(getCompoundHead(v));
//		
//		
//		//public static void fixAlignmentName(String folderName) {
//		
//		String folder = "./files/DBLP-Scholar/alignments";
//		
//		//public static String splitCompoundString(String s) {
//		String propName = "isWrittenBy";
//		System.out.println("The new property name is " + splitCompoundString(propName));
//		
//		//public static String stringTokenize(String s, boolean lowercase) {
//		String def = "a written or printed work consisting of pages glued or sewn together along one side and bound in covers.";
//		System.out.println(stringTokenize(def, true));
//		
//		

        //fixAlignmentName(folder);

//		String s = stringTokenize("AircraftEngine", false).toLowerCase();
//		
//		System.out.println(s + " contains " + countCharsInString(s) + " characters");
//		
//		System.out.println(s);
		
		/*
		String testString = "motionPicture";
		String experiment = "biblio-bibo";

		System.out.println(tokenize(testString, true));

		String onto1 = experiment.substring(0, experiment.lastIndexOf("-"));
		String onto2 = experiment.substring(experiment.lastIndexOf("-")+1, experiment.length());
		System.out.println(onto1);

		System.out.println(onto2);

		String test = "academicArticle";

		String newString = stringTokenize(test, false);

		System.out.println("Original string: " + test + ", tokenized string: " + newString);

		String prop = "hasCar";
		System.out.println("Without prefix the property name is " + stripPrefix(prop));

		String s = "Testing underscore";
		System.out.println("Without underscore: " + replaceUnderscore(s));

		File ontoFile = new File("./files/ontologies/BIBO.owl"); 
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager(); 
		OWLOntology onto = manager.loadOntologyFromOntologyDocument(ontoFile);

		String ontoIRI = getOntologyIRI(onto);
		System.out.println("The IRI of the ontology is " + ontoIRI);

		String relType = "<";

		System.out.println("The relation type is " + relType);

		String newRelType = validateRelationType(relType);

		System.out.println("New relationtype is " + newRelType);

		System.out.println(removeStopWords("Here we go again and if we do not go then we stay put is that not the case?"));
		
		String compositeString = "MusicReleaseFormatType";
		
		System.out.println("Is " + compositeString + " a compound: " + isCompoundWord(compositeString));
		
		System.out.println("The compound head is " + getCompoundHead(compositeString));
		
		System.out.println("Printing all individual words from " + compositeString + " :");
		
		ArrayList<String> compoundWordsList = getWordsFromCompound(compositeString);
		
		for (String word : compoundWordsList) {
			System.out.println(word);
		}

		String word = getCompoundWordsWithSpaces(compositeString);
		
		System.out.println(word);
		*/
    }


}
