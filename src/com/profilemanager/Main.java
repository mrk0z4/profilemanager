package com.profilemanager;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.hp.hpl.jena.rdf.model.Resource;
import com.profilemanager.calendar.CalendarAPI;
import com.profilemanager.sparql.ProfileManagerEndpoint;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by mc on 7/15/14.
 */
public class Main {

    public static void main (String args[]){
        CalendarAPI calendarApi = new CalendarAPI();
        ProfileManagerEndpoint profileManager = new ProfileManagerEndpoint();

        DateTime start = new DateTime(new Date(1405483500000L), TimeZone.getTimeZone("UTC"));
        DateTime end = new DateTime(new Date(1405483500000L + (60 * 60 * 1000)), TimeZone.getTimeZone("UTC"));
        System.out.println(start.toString());
        List<Event> eventList = calendarApi.getEventsWithin(start, end);
        List<String> attendeesEmail = new ArrayList<String>();
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
        }
        List<Resource> preferences = profileManager.getPrivacyPreferences(attendeesEmail);
        for(Resource preference : preferences) {
            System.out.println("\nPreference for " + attendeesEmail.get(preferences.indexOf(preference)) + "...");
            System.out.println(preference != null ? preference.getLocalName() : "null");
        }

    }
}
