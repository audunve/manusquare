package ui;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.codehaus.jettison.json.JSONException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class BasicMatchmaking_MVP {
	

	public static void main(String[] args) throws OWLOntologyCreationException, IOException, ParseException, JSONException, OWLOntologyStorageException {

		long startTime = System.currentTimeMillis();
			
		logging(false);
		
		int numMatchingResults = 10;
		String jsonIn = "./files/rfq.json";
		String jsonOut = "./files/matchingResults.json";
		
		//if test == true -> local KB + additional data written to console, if test == false, MANUSQUARE Semantic Infrastructure
		boolean testing = true;
		
		//if weighted == true, I'm trying a weight configuration of (process=0.75, materials 0.25; processAndMaterials=0.75, certifications=0.25)
		boolean weighted = true;		

		SemanticMatching_MVP.performSemanticMatching(jsonIn, numMatchingResults, jsonOut, testing, weighted);
		
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        
        if (testing == true) {
        System.out.println("The entire Matchmaking process completed in " + elapsedTime/1000 + " seconds.");
        }

	}


	private static void logging(boolean logging) {
		Set<String> loggers = new HashSet<>(Arrays.asList("org.apache.http", "org.eclipse.rdf4j"));

		if (logging == false) {			
			for(String log:loggers) { 
				Logger logger = (Logger)LoggerFactory.getLogger(log);
				logger.setLevel(Level.ERROR);
				logger.setAdditive(false);
			}
		} else {

			System.out.println("Logging:");

		}

	}

}
