package org.motechproject.outbox.model;


/**
 *
 */
public class VoiceMessageType {	
	private String voiceMessageTypeName;
    private MessagePriority priority;
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (canBeSaved ? 1231 : 1237);
		result = prime * result
				+ ((priority == null) ? 0 : priority.hashCode());
		result = prime * result + ((vXmlUrl == null) ? 0 : vXmlUrl.hashCode());
		result = prime
				* result
				+ ((voiceMessageTypeName == null) ? 0 : voiceMessageTypeName
						.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VoiceMessageType other = (VoiceMessageType) obj;
		if (canBeSaved != other.canBeSaved)
			return false;
		if (priority != other.priority)
			return false;
		if (vXmlUrl == null) {
			if (other.vXmlUrl != null)
				return false;
		} else if (!vXmlUrl.equals(other.vXmlUrl))
			return false;
		if (voiceMessageTypeName == null) {
			if (other.voiceMessageTypeName != null)
				return false;
		} else if (!voiceMessageTypeName.equals(other.voiceMessageTypeName))
			return false;
		return true;
	}
}
