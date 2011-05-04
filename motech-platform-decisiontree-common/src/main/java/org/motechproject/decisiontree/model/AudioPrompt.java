package org.motechproject.decisiontree.model;

import org.motechproject.decisiontree.model.TextToSpeechPrompt.Builder;


/**
 *
 */
public class AudioPrompt extends Prompt {

    private String audioFileUrl;
    private String altMessage; //Text To Speech Alternate if audio file not available

    public static class Builder {
    	private AudioPrompt obj;
		public Builder() {
			obj = new AudioPrompt();
		} 
		public Prompt build() {
			return obj;
		}
	    public Builder setName(String name) {
	        obj.setName(name);
	        return this;
	    }			
	    public Builder setAudioFileUrl(String audioFileUrl) {
	        obj.audioFileUrl = audioFileUrl;
	        return this;
	    }
	    public Builder setAltMessage(String altMessage) {
	        obj.altMessage = altMessage;
	        return this;
	    }		
    }
    public static Builder newBuilder() {
    	return new Builder();
    }    

    public String getAudioFileUrl() {
        return audioFileUrl;
    }

    public void setAudioFileUrl(String audioFileUrl) {
        this.audioFileUrl = audioFileUrl;
    }

    public String getAltMessage() {
        return altMessage;
    }

    public void setAltMessage(String altMessage) {
        this.altMessage = altMessage;
    }

    @Override
    public String toString() {
        return "AudioPrompt{" +
                "audioFileUrl='" + audioFileUrl + '\'' +
                ", altMessage='" + altMessage + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AudioPrompt that = (AudioPrompt) o;

        if (altMessage != null ? !altMessage.equals(that.altMessage) : that.altMessage != null) return false;
        if (audioFileUrl != null ? !audioFileUrl.equals(that.audioFileUrl) : that.audioFileUrl != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (audioFileUrl != null ? audioFileUrl.hashCode() : 0);
        result = 31 * result + (altMessage != null ? altMessage.hashCode() : 0);
        return result;
    }
}
