package org.motechproject.decisiontree.model;

import org.motechproject.model.MotechAuditableDataObject;

import java.util.List;
import java.util.Map;

/**
 * 
 */
public class Node extends MotechAuditableDataObject {

    private static final long serialVersionUID = 1L;

    private List<Action> actionsBefore;
    private List<Action> actionsAfter;
    private List<Prompt> prompts;
    private Map<String, Transition> transitions;

    public List<Action> getActionsBefore() {
        return actionsBefore;
    }

    public void setActionsBefore(List<Action> actionsBefore) {
        this.actionsBefore = actionsBefore;
    }

    public List<Action> getActionsAfter() {
        return actionsAfter;
    }

    public void setActionsAfter(List<Action> actionsAfter) {
        this.actionsAfter = actionsAfter;
    }

    public List<Prompt> getPrompts() {
        return prompts;
    }

    public void setPrompts(List<Prompt> prompts) {
        this.prompts = prompts;
    }

    public Map<String, Transition> getTransitions() {
        return transitions;
    }

    public void setTransitions(Map<String, Transition> transitions) {
        this.transitions = transitions;
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
