package org.motechproject.decisiontree.model;

import org.apache.commons.lang.ArrayUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.*;

/**
 * Represents a node in the decision tree.
 */
public class Node {

    private List<Action> actionsBefore;
    private List<Action> actionsAfter;
    @JsonProperty
    private List<Prompt> prompts = new ArrayList<Prompt>();
    @JsonProperty
    private Map<String, ITransition> transitions;
    @JsonProperty
    private List<ITreeCommand> treeCommands = new ArrayList<ITreeCommand>();

    public List<Action> getActionsBefore() {
        return actionsBefore == null ? Collections.<Action>emptyList() : actionsBefore;
    }

    public Node setActionsBefore(List<Action> actionsBefore) {
        this.actionsBefore = actionsBefore;
        return this;
    }

    public List<Action> getActionsAfter() {
        return actionsAfter == null ? Collections.<Action>emptyList() : actionsAfter;
    }

    public Node setActionsAfter(List<Action> actionsAfter) {
        this.actionsAfter = actionsAfter;
        return this;
    }

    /**
     * Returns the list of prompts
     * @return
     * @see AudioPrompt
     * @see TextToSpeechPrompt
     */
    public List<Prompt> getPrompts() {
        return prompts;
    }

    /**
     * Sets the prompt list with the given prompts
     * @param prompts
     * @return the current node instance
     */
    @JsonIgnore
    public Node setPrompts(Prompt... prompts) {
        setPrompts(Arrays.asList(prompts));
        return this;
    }

    /**
     * Adds the given prompts to the existing list
     * @param prompts
     * @return the current node instance
     */
    @JsonIgnore
    public Node addPrompts(Prompt... prompts) {
        this.prompts.addAll(Arrays.asList(prompts));
        return this;
    }

    /**
     * Adds the given prompt to the beginning of the prompt list
     * @param prompt
     * @return the current node instance
     */
    @JsonIgnore
    public Node addPromptToBeginning(Prompt prompt) {
        this.prompts.add(0, prompt);
        return this;
    }

    /**
     * Sets the prompt list with the given list of prompts
     * @param prompts
     * @return
     */
    private Node setPrompts(List<Prompt> prompts) {
        this.prompts.addAll(prompts);
        return this;
    }

    /**
     * Gets the transition from the current node.
     * @return a map of userInput => transition pairs
     */
    public Map<String, ITransition> getTransitions() {
        return transitions == null ? Collections.<String, ITransition>emptyMap() : transitions;
    }

    /**
     * @param transitions an Object[][] array containing {Key,Transition} array pairs
     * @return the current node instance
     */
    @JsonIgnore
    @SuppressWarnings("unchecked")
    public Node setTransitions(Object[][] transitions) {
        this.transitions = ArrayUtils.toMap(transitions);
        return this;
    }

    public Node setTransitions(Map<String, ? extends ITransition> transitions) {
        this.transitions = (Map<String, ITransition>) transitions;
        return this;
    }

    /**
     * Gets the tree commands for this node
     * @return tree commands of the node
     * @see ITreeCommand
     */
    @JsonIgnore
    public List<ITreeCommand> getTreeCommands() {
        return treeCommands;
    }

    public Node setTreeCommands(ITreeCommand... treeCommands) {
        Collections.addAll(this.treeCommands, treeCommands);
        return this;
    }

    /**
     * Adds a transitions to the transitions userInput => transition pair of the node.
     * @param transitionKey  User input
     * @param transition transition object See {@link Transition}
     * @return the current node instance
     */
    public Node addTransition(String transitionKey, ITransition transition) {
        if (transitions == null) {
            transitions = new HashMap<String, ITransition>();
        }
        transitions.put(transitionKey, transition);
        return this;
    }

    /**
     * Returns a boolean value to state if the node has any transition
     * @return boolean value representing if the node has any transition
     */
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
