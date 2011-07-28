package org.motechproject.decisiontree.model;


/**
 *
 */
public class TextToSpeechPrompt extends Prompt {

    private String message;

    public static class Builder {
    	private TextToSpeechPrompt obj;
		public Builder() {
			obj = new TextToSpeechPrompt();
		} 
		public Prompt build() {
			return obj;
		}
	    public Builder setName(String name) {
	        obj.setName(name);
	        return this;
	    }		
	    public Builder setMessage(String message) {
	        obj.message = message;
	        return this;
	    }
        public Builder setTTSCommand(ITreeCommand command) {
            obj.command = command;
            return this;
        }
    }
    
    public static Builder newBuilder() {
    	return new Builder();
    }        
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "TextToSpeechPrompt{" +
                "message='" + message + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        TextToSpeechPrompt that = (TextToSpeechPrompt) o;

        if (message != null ? !message.equals(that.message) : that.message != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }
}
