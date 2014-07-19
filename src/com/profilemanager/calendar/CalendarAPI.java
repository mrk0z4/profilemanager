package com.profilemanager.calendar;

import com.profilemanager.Main;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.*;
import com.google.api.services.calendar.model.*;
import com.google.api.services.calendar.model.Calendar;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * An easy to use API for Google Calendar in order to obtain relevant events regarding a date to create a context.
 * Previous to use, you must follow the instructions given in developer.google.com for Google Calendar API.
 *
 * This class provides functionality for authorizing the account if it hasn't been yet authorized.
 *
 * Created by mc on 7/15/14.
 */
public class CalendarAPI {

    /**
     * Be sure to specify the name of your application. If the application name is {@code null} or
     * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
     */
    private static final String APPLICATION_NAME = "Profile Manager";

    /** Directory to store user credentials. */
    private static final java.io.File DATA_STORE_DIR =
            new java.io.File(System.getProperty("user.home"), ".store/calendar_sample");

    /**
     * Global instance of the {@link com.google.api.client.util.store.DataStoreFactory}. The best practice is to make it a single
     * globally shared instance across your application.
     */
    private static FileDataStoreFactory dataStoreFactory;

    /** Global instance of the HTTP transport. */
    private static HttpTransport httpTransport;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private com.google.api.services.calendar.Calendar client;

    public CalendarAPI(){
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            // initialize the data store factory
            dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

            // authorization
            Credential credential = authorize();

            // set up global Calendar instance
            client = new com.google.api.services.calendar.Calendar.Builder(
                    httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Event createNewEvent(Calendar calendar, String summary, Date startDate, Date endDate){
        Event event = new Event();
        event.setSummary(summary);
        DateTime start = new DateTime(startDate, TimeZone.getTimeZone("UTC"));
        event.setStart(new EventDateTime().setDateTime(start));
        DateTime end = new DateTime(endDate, TimeZone.getTimeZone("UTC"));
        event.setEnd(new EventDateTime().setDateTime(end));
        Event result;
        try {
            result = client.events().insert(calendar.getId(), event).execute();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Event> getAllEvents(){
        List<Event> eventList;
        try {
            Events events = client.events().list("primary").execute();
            eventList = events.getItems();
            return eventList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Event getActualEvent(Date startDate, Date endDate){
        try {
            DateTime start = new DateTime(startDate, TimeZone.getTimeZone("UTC"));
            DateTime end = new DateTime(endDate, TimeZone.getTimeZone("UTC"));
            Events events = client.events().list("primary")
                    .setMaxResults(1)
                    .setShowDeleted(false)
                    .setTimeMax(end)
                    .setTimeMin(start).execute();
            // Because we only expect one event we should return the first element
            try{
                return events.getItems().get(0);
            } catch(IndexOutOfBoundsException e){
                e.printStackTrace();
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Event getActualEvent(Date date){
        try {
            DateTime start = new DateTime(date, TimeZone.getTimeZone("UTC"));
            Events events = client.events().list("primary")
                    .setMaxResults(1)
                    .setShowDeleted(false)
                    .setTimeMin(start).execute();
            // Because we only expect one event we should return the first element
            try{
                return events.getItems().get(0);
            } catch(IndexOutOfBoundsException e){
                e.printStackTrace();
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Event getActualEvent(DateTime date){
        try {
            Events events = client.events().list("primary")
                    .setSingleEvents(true)
                    .setOrderBy("startTime")
                    .setShowDeleted(false)
                    .setTimeMin(date).execute();
            // Because we only expect one event we should return the first element
            try{
                return events.getItems().get(0);
            } catch(IndexOutOfBoundsException e){
                e.printStackTrace();
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Event> getEventsWithStartDate(DateTime date){
        try {
            Events events = client.events().list("primary")
                    .setShowDeleted(false)
                    .setTimeMin(date).execute();
            // Because we only expect one event we should return the first element
            try{
                return events.getItems();
            } catch(IndexOutOfBoundsException e){
                e.printStackTrace();
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Event> getEventsWithin(DateTime startDate, DateTime endDate){
        try {
            Events events = client.events().list("primary")
                    .setShowDeleted(false)
                    .setTimeMin(startDate)
                    .setTimeMax(endDate)
                    .setShowDeleted(false)
                    .setSingleEvents(false)
                    .execute();
            // Because we only expect one event we should return the first element
            try{
                return events.getItems();
            } catch(IndexOutOfBoundsException e){
                e.printStackTrace();
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<EventAttendee> getAttendeeListForEvent(Event event){
        List<EventAttendee> eventAttendeeList = event.getAttendees();
        return eventAttendeeList;
    }

    public List<EventAttendee> getAttendeeListForDate(Date startDate, Date endDate){
        Event event = getActualEvent(startDate, endDate);
        return getAttendeeListForEvent(event);
    }

    /** Authorizes the installed application to access user's protected data. */
    private Credential authorize() throws Exception {
        // load client secrets
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                new InputStreamReader(Main.class.getResourceAsStream("/client_secrets.json")));
        if (clientSecrets.getDetails().getClientId().startsWith("Enter")
                || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
            System.out.println(
                    "Enter Client ID and Secret from https://code.google.com/apis/console/?api=calendar "
                            + "into calendar-cmdline-sample/src/main/resources/client_secrets.json");
            return null;
        }
        // set up authorization code flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets,
                Collections.singleton(CalendarScopes.CALENDAR)).setDataStoreFactory(dataStoreFactory)
                .build();
        // authorize
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    private boolean isAuthorized(){
        return client == null;
    }

}
