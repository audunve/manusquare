package ui.mvp;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.codehaus.jettison.json.JSONException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class BasicMatchmaking_MVP {
	

	public static void main(String[] args) throws OWLOntologyCreationException, IOException, ParseException, JSONException {

		long startTime = System.currentTimeMillis();
		
		logging(false);
		
		int numMatchingResults = 15;
		String jsonIn = "./files/rfq.json";
		String jsonOut = "./files/matchingResults.json";
		
		//if weighted == true, Process facet is using a weight of 0.8 while certifications uses a weight of 0.2
		SemanticMatching_MVP.performSemanticMatching(jsonIn, numMatchingResults, jsonOut, true);
		
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        
        System.out.println("Process completed in " + elapsedTime/1000 + " seconds.");

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
