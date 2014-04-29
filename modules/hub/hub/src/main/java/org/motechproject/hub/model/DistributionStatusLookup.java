package org.motechproject.hub.model;

public enum DistributionStatusLookup {
	
	SUCCESS ("success"),
	FAILURE ("failure");
	
	private final String status;
	
	private DistributionStatusLookup (String status)	{
		this.status = status;
	}
	
	@Override
	public String toString() {
		return this.status;
	}
	
}
