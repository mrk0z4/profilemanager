package com.profilemanager.sparql;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Resource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * A class that provides an SPARQL endpoint to make queries to a server.
 *
 * It only provides the functions to make select queries for email and privacy preferences.
 * Created by mc on 7/21/14.
 */
public class ProfileManagerEndpoint {

    /**
     * Common objects using for constructing a query.
     */
    private final static String SPARQL_ENDPOINT_QUERY = "http://localhost:3030/ds/query";
    private final static String QUERY_PREFIXES = "PREFIX gg: <http://example.org/groups/>" +
            "PREFIX pp: <http://example.org/privacyPreference/>" +
            "PREFIX foaf: <http://xmlns.com/foaf/0.1/#>";
    private final static String QUERY_STRING_PROFESSORS = "SELECT ?x WHERE { ?x a gg:Professor}";
    private final static String QUERY_STRING_PROFESSORS_WITH_PRIVACY_PREFERENCES = "SELECT ?x ?firstName ?lastName ?pp WHERE { ?x a gg:Professor ." +
            " ?x foaf:firstName ?firstName ." +
            " ?x foaf:lastName ?lastName ." +
            " ?x pp:hasPrivacyPreference ?pp }";
    private final static String QUERY_STRING_BY_EMAIL = "SELECT ?x ?pp WHERE { ?x foaf:mbox \"%s\" ." +
            " ?x pp:hasPrivacyPreference ?pp }";

    // Strings for privacy preferences.
    private final static String LOW_PRIVACY = "PublicPrivacyPreference";
    private final static String MEDIUM_PRIVACY = "SemipublicPrivacyPreference";
    private final static String HIGH_PRIVACY = "CompletePrivacyPreference";

    private final static String PRIVACY_PREFERENCE = "?pp";

    /**
     * Constructor
     */
    public ProfileManagerEndpoint(){

    }

    public void getPrivacyPreferences(){
        // Build the query string.
        String queryString = QUERY_PREFIXES + " " + QUERY_STRING_PROFESSORS_WITH_PRIVACY_PREFERENCES;
        Query query = QueryFactory.create(queryString) ;
        // Query a remote triple store. In this case, as we work in local, we use localhost in port 3030.
        QueryExecution qexec = QueryExecutionFactory.sparqlService(SPARQL_ENDPOINT_QUERY, query) ;
        try {
            ResultSet results = qexec.execSelect();
            QuerySolution sol;
            while(results.hasNext()) {
                sol = results.next();
                Iterator<String> itStr = sol.varNames();
                while(itStr.hasNext()){
                    String p = itStr.next();
                    if(sol.get(p).isLiteral()){
                        System.out.println(sol.getLiteral(p));
                    }
                    else if(sol.get(p).isResource()){
                        String name = sol.getResource(p).getLocalName();
                        // Do the necessary to control the opacity of the smart glass
                        if(name.equals(LOW_PRIVACY)){

                        }
                        else if(name.equals(MEDIUM_PRIVACY)){

                        }
                        else if(name.equals(HIGH_PRIVACY)){

                        }
                        System.out.println();
                    }
                }
                System.out.println(sol.toString());
            }
        } finally {
            qexec.close();
        }
    }


    /**
     * Obtains the privacy preferences given a email.
     *
    * The way we check the preferences are using the foaf:mbox as a search term.
    * This method appends the string "mailto:" to maintain simplicity.
     */
    public Resource getPrivacyPreferences(String mbox){
        Resource pref = null;
        String queryString = QUERY_PREFIXES + " " + QUERY_STRING_BY_EMAIL.replace("%s", "mailto:" + mbox);
        Query query = QueryFactory.create(queryString) ;
        // Query a remote triple store. In this case, as we work in local, we use localhost in port 3030.
        QueryExecution qexec = QueryExecutionFactory.sparqlService(SPARQL_ENDPOINT_QUERY, query) ;
        try {
            ResultSet results = qexec.execSelect();
            QuerySolution sol;
            // We assume that we only have one result.
            if(results.hasNext()) {
                sol = results.next();
                pref = sol.get(PRIVACY_PREFERENCE).asResource();
            }
        } finally {
            qexec.close();
        }
        return pref;
    }

    /**
     * Obtains the preferences for a list of attendees email.
     * @param attendeesEmail the list of emails to check.
     * @return a hashmap containing a email:preference relation.
     */
    public HashMap<String, Resource> getPrivacyPreferences(List<String> attendeesEmail){
        HashMap<String, Resource> preferences = new HashMap<String, Resource>();
        for(String attendeeEmail : attendeesEmail){
            String queryString = QUERY_PREFIXES + " " + QUERY_STRING_BY_EMAIL.replace("%s", "mailto:" + attendeeEmail);
            Query query = QueryFactory.create(queryString) ;
            // Query a remote triple store. In this case, as we work in local, we use localhost in port 3030.
            QueryExecution qexec = QueryExecutionFactory.sparqlService(SPARQL_ENDPOINT_QUERY, query) ;
            try {
                ResultSet results = qexec.execSelect();
                QuerySolution sol;
                if(results.hasNext()) {
                    sol = results.next();
                    // We assume that we only have one result.
                    preferences.put(attendeeEmail, sol.get(PRIVACY_PREFERENCE).asResource());
                }
            } finally {
                qexec.close();
            }
        }
        return preferences;
    }

}
