package org.motechproject.hub.model;

public enum DistributionStatusLookup {
	
	SUCCESS ("success"),
	FAILURE ("failure");
	
	private final String mode;
	
	private DistributionStatusLookup (String mode)	{
		this.mode = mode;
	}
	
	public String getMode() {
		return this.mode;
	}
	
}
