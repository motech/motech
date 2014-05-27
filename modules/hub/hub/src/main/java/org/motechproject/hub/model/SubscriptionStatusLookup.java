package org.motechproject.hub.model;

public enum SubscriptionStatusLookup {
	
	ACCEPTED (1,"accepted"),
	INTENT_FAILED (2,"intent_failed"),
	INTENT_VERIFIED (3,"intent_verified");
	
	private final String status;
	private final int id;
	
	public int getId() {
		return id;
	}

	private SubscriptionStatusLookup (int id, String status)	{
		this.status = status;
		this.id =id;
	}
	
	@Override
	public String toString() {
		return this.status;
	}
	
}
