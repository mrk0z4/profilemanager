package com.profilemanager.sparql;

import com.hp.hpl.jena.query.*;

import java.util.Iterator;

public class ProfileManagerEndpoint {

    private final static String SPARQLR_ENDPOINT_QUERY = "http://localhost:3030/ds/query";
    private final static String QUERY_PREFIXES = "PREFIX gg: <http://example.org/groups/>" +
            "PREFIX pp: <http://example.org/privacyPreference/>" +
            "PREFIX foaf: <http://xmlns.com/foaf/0.1/#>";
    private final static String QUERY_STRING_PROFESSORS = "SELECT ?x WHERE { ?x a gg:Professor}";
    private final static String QUERY_STRING_PROFESSORS_WITH_PRIVACY_PREFERENCES = "SELECT ?x ?firstName ?lastName ?pp WHERE { ?x a gg:Professor ." +
            " ?x foaf:firstName ?firstName ." +
            " ?x foaf:lastName ?lastName ." +
            " ?x pp:hasPrivacyPreference ?pp }";

    // Strings for privacy preferences.
    private final static String LOW_PRIVACY = "PublicPrivacyPreference";
    private final static String MEDIUM_PRIVACY = "SemipublicPrivacyPreference";
    private final static String HIGH_PRIVACY = "CompletePrivacyPreference";

    public void getPrivacyPreferences(){
        // Build the query string.
        String queryString = QUERY_PREFIXES + " " + QUERY_STRING_PROFESSORS_WITH_PRIVACY_PREFERENCES;
        Query query = QueryFactory.create(queryString) ;
        // Query a remote triple store. In this case, as we work in local, we use localhost in port 3030.
        QueryExecution qexec = QueryExecutionFactory.sparqlService(SPARQLR_ENDPOINT_QUERY, query) ;
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

}
