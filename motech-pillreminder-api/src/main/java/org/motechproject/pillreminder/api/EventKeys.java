package org.motechproject.pillreminder.api;

public class EventKeys {

	public final static String BASE_SUBJECT = "org.motechproject.server.";
	
	public final static String PILLREMINDER_ID_KEY = "PillReminderID";

    public final static String PILLREMINDER_WILDCARD_SUBJECT = BASE_SUBJECT + "pillreminder.*";
    public final static String PILLREMINDER_CREATED_SUBJECT = BASE_SUBJECT + "pillreminder.created";
    public final static String PILLREMINDER_UPDATED_SUBJECT = BASE_SUBJECT + "pillreminder.updated";
    public final static String PILLREMINDER_DELETED_SUBJECT = BASE_SUBJECT + "pillreminder.deleted";
	
}
