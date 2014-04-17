package org.motechproject.hub.model;

public enum SubscriptionStatusLookup {
	
	ACCEPTED ("accepted"),
	INTENT_FAILED ("intent_failed"),
	INTENT_VERIFIED ("intent_verified");
	
	private final String status;
	
	private SubscriptionStatusLookup (String status)	{
		this.status = status;
	}
	
	public String getSubscriptionStatusLookup() {
		return this.status;
	}
	
}
