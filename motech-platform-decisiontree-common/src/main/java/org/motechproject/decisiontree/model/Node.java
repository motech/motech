package org.motechproject.decisiontree.model;

import org.apache.commons.lang.ArrayUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class Node {

    private List<Action> actionsBefore;
    private List<Action> actionsAfter;
    private List<Prompt> prompts;
    private Map<String, Transition> transitions;
    private ITreeCommand treeCommand = new NullTreeCommand();

    public static class Builder {
    	private Node obj;
		public Builder() {
			obj = new Node();
		} 
		public Node build() {
			return obj;
		}
	    public Builder setActionsBefore(List<Action> actionsBefore) {
	    	obj.actionsBefore = actionsBefore;
	    	return this;
	    }
	    public Builder setActionsAfter(List<Action> actionsAfter) {
	    	obj.actionsAfter = actionsAfter;
	    	return this;
	    }
        public Builder setTreeCommand(ITreeCommand treeCommand) {
            obj.treeCommand = treeCommand;
            return this;
        }
	    public Builder setPrompts(List<Prompt> prompts) {
	    	obj.prompts = prompts;
	    	return this;
	    }
	    /**
	     * @param transitions an Object[][] array containing {Key,Transition} array pairs
	     * @return a Builder
	     */
	    @SuppressWarnings("unchecked")
		public Builder setTransitions(Object[][] transitions) {
	    	obj.transitions = ArrayUtils.toMap(transitions);
	    	return this;
	    }

        public Builder setTransitions(Map<String, Transition> transitions) {
	    	obj.transitions = transitions;
	    	return this;
	    }
    }

    public static Builder newBuilder() {
    	return new Builder();
    }
    
    public List<Action> getActionsBefore() {
        return actionsBefore==null?Collections.<Action>emptyList():actionsBefore;
    }

    public void setActionsBefore(List<Action> actionsBefore) {
        this.actionsBefore = actionsBefore;
    }

    public List<Action> getActionsAfter() {
        return actionsAfter==null?Collections.<Action>emptyList():actionsAfter;
    }

    public void setActionsAfter(List<Action> actionsAfter) {
        this.actionsAfter = actionsAfter;
    }

    public List<Prompt> getPrompts() {
        return prompts==null?Collections.<Prompt>emptyList():prompts;
    }

    public void setPrompts(List<Prompt> prompts) {
        this.prompts = prompts;
    }

    public Map<String, Transition> getTransitions() {
        return transitions==null?Collections.<String, Transition>emptyMap():transitions;
    }

    public void setTransitions(Map<String, Transition> transitions) {
        this.transitions = transitions;
    }

    @JsonIgnore
    public ITreeCommand getTreeCommand() {
        return treeCommand;
    }

    public void setTreeCommand(ITreeCommand treeCommand) {
        this.treeCommand = treeCommand;
    }

    public void addTransition(String transitionKey, Transition transition) {
        if (transitions == null) {
            transitions = new HashMap<String, Transition>();
        }
        transitions.put(transitionKey, transition);
    }

    @JsonIgnore
    public boolean hasTransitions() {
        return transitions != null && !transitions.isEmpty();
    }

    @Override
    public String toString() {
        return "Node{" +
                "actionsBefore=" + actionsBefore +
                ", actionsAfter=" + actionsAfter +
                ", prompts=" + prompts +
                ", transitions=" + transitions +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (actionsAfter != null ? !actionsAfter.equals(node.actionsAfter) : node.actionsAfter != null) return false;
        if (actionsBefore != null ? !actionsBefore.equals(node.actionsBefore) : node.actionsBefore != null)
            return false;
        if (prompts != null ? !prompts.equals(node.prompts) : node.prompts != null) return false;
        if (transitions != null ? !transitions.equals(node.transitions) : node.transitions != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = actionsBefore != null ? actionsBefore.hashCode() : 0;
        result = 31 * result + (actionsAfter != null ? actionsAfter.hashCode() : 0);
        result = 31 * result + (prompts != null ? prompts.hashCode() : 0);
        result = 31 * result + (transitions != null ? transitions.hashCode() : 0);
        return result;
    }
}
