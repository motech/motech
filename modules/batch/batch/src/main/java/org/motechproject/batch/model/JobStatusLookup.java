package org.motechproject.batch.model;

public enum JobStatusLookup {

	ACTIVE(1);
	
	private final int id;
	
	public int getId() {
		return id;
	}

	private JobStatusLookup (int id)	{
		this.id =id;
	}
	
	
}
