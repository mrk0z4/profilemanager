package com.profilemanager;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.hp.hpl.jena.rdf.model.Resource;
import com.profilemanager.calendar.CalendarAPI;
import com.profilemanager.cristal.CristalAPI;
import com.profilemanager.sparql.ProfileManagerEndpoint;

import java.util.*;

/**
 * Created by mc on 7/15/14.
 */
public class Main {

    public static void main (String args[]){

        CristalAPI cristalApi = new CristalAPI();

        //First we initialize the Service Discovery to search cristal service
        cristalApi.connectToService();

        CalendarAPI calendarApi = new CalendarAPI();
        ProfileManagerEndpoint profileManager = new ProfileManagerEndpoint();

        DateTime start = new DateTime(new Date(1405483500000L), TimeZone.getTimeZone("UTC"));
        DateTime end = new DateTime(new Date(1405483500000L + (60 * 60 * 1000)), TimeZone.getTimeZone("UTC"));
        System.out.println(start.toString());
        List<Event> eventList = calendarApi.getEventsWithin(start, end);
        List<String> attendeesEmail = new ArrayList<String>();
        String priorityAttendeeEmail = "";
        System.out.println("List of all events with start date given:");
        for(Event event : eventList){
            System.out.println(event.getId());
            System.out.println(event.getSummary());
            if(event.getAttendees() != null) {
                for (EventAttendee attendee : event.getAttendees()) {
                    System.out.println("Display name: " + attendee.getDisplayName());
                    System.out.println("Email: " + attendee.getEmail());
                    System.out.println("ID: " + attendee.getId());
                    attendeesEmail.add(attendee.getEmail());
                }
            }
            System.out.println(event.getStart().getDateTime());
            System.out.println(event.getEnd().getDateTime());
            System.out.println("Priority Attendee email: " + event.getExtendedProperties().getPrivate().get("priorityAttendeeEmail"));
            priorityAttendeeEmail = calendarApi.getPriorityAttendeeEmail(event);
        }
        HashMap<String, Resource> preferences = profileManager.getPrivacyPreferences(attendeesEmail);
        Iterator it = preferences.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println("\nPreference for " + pair.getKey() + "...");
            System.out.println(((Resource)pair.getValue()).getLocalName());
            if(cristalApi.isConnectedToService() && pair.getKey().equals(priorityAttendeeEmail)){
                System.out.println("We can now operate with cristal.");
            }
        }

    }
}
