package org.motechproject.tama.model;

public enum Gender {
	MALE("Male"), FEMALE("Female"), HIJIRA("Hijira");
	
	private final String value;
	
	private Gender(String value) {
		this.value=value;
	}
	@Override
	public String toString(){
		return value;
	}
	
	public String getKey(){
		return name();
	}
}
