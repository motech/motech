package org.motechproject.server.service.ivr;

public interface IVRMessage {

	public abstract String getText(String key);

	public abstract String getWav(String key, String preferredLangCode);
	
	public abstract String getSignatureMusic();
}