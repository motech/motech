package org.motechproject.outbox.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class VoiceMessageType extends MotechAuditableDataObject {

	private static final long serialVersionUID = 1096770767856112663L;
	
	private String voiceMessageTypeName;
    private MessagePriority priority;
    @TypeDiscriminator
    private String vXmlUrl;
    private boolean canBeSaved; // indicates if this type of messages allowed to be saved by patients in they voice outbox
	public String getVoiceMessageTypeName() {
		return voiceMessageTypeName;
	}
	public void setVoiceMessageTypeName(String voiceMessageTypeName) {
		this.voiceMessageTypeName = voiceMessageTypeName;
	}
	public MessagePriority getPriority() {
		return priority;
	}
	public void setPriority(MessagePriority priority) {
		this.priority = priority;
	}
	public String getvXmlUrl() {
		return vXmlUrl;
	}
	public void setvXmlUrl(String vXmlUrl) {
		this.vXmlUrl = vXmlUrl;
	}
	public boolean isCanBeSaved() {
		return canBeSaved;
	}
	public void setCanBeSaved(boolean canBeSaved) {
		this.canBeSaved = canBeSaved;
	}
	@Override
	public String toString() {
		return "VoiceMessageType [voiceMessageTypeName=" + voiceMessageTypeName
				+ ", priority=" + priority + ", vXmlUrl=" + vXmlUrl
				+ ", canBeSaved=" + canBeSaved + "]";
	}
}
