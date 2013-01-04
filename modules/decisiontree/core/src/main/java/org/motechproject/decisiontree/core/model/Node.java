package org.motechproject.decisiontree.core.model;

import org.apache.commons.lang.ArrayUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.motechproject.decisiontree.core.TreeNodeLocator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * Represents a node in the decision tree.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class Node implements Serializable {

    private List<Action> actionsBefore;
    private List<Action> actionsAfter;
    @JsonProperty
    private List<Prompt> noticePrompts = new ArrayList<Prompt>();
    @JsonProperty
    private List<Prompt> prompts = new ArrayList<Prompt>();
    @JsonProperty
    private Map<String, ITransition> transitions;
    @JsonProperty
    private List<INodeOperation> operations = new ArrayList<>();
    @JsonProperty
    private Integer maxTransitionInputDigit;
    @JsonProperty
    private Integer maxTransitionTimeout;
    @JsonProperty
    private String transitionKeyEndMarker;

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

    public List<Prompt> getNoticePrompts() {
        return noticePrompts;
    }

    public Node setNoticePrompts(List<Prompt> noticePrompts) {
        this.noticePrompts = noticePrompts;
        return this;
    }

    @JsonIgnore
    public Node setNoticePrompts(Prompt... noticePrompts) {
        setNoticePrompts(Arrays.asList(noticePrompts));
        return this;
    }

    @JsonIgnore
    public Node addNoticePrompts(Prompt... prompts) {
        this.noticePrompts.addAll(Arrays.asList(prompts));
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
    public Node setPrompts(List<Prompt> prompts) {
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
     * Gets the tree operations for this node
     * @return tree operations of the node
     * @see INodeOperation
     */
    @JsonIgnore
    public List<INodeOperation> getOperations() {
        return this.operations;
    }

    public Node addOperations(INodeOperation ... operations) {
        Collections.addAll(this.operations, operations);
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

    @JsonIgnore
    public boolean hasDialPrompts() {
        for (Prompt prompt : prompts) {
            if (prompt instanceof DialPrompt) {
                return true;
            }
        }
        return false;
    }

    @JsonIgnore
    public boolean hasNoInputTransition() {
        return hasTransitions() && (transitions.get(TreeNodeLocator.NO_INPUT) != null);
    }

    public Integer getMaxTransitionInputDigit() {
        return maxTransitionInputDigit;
    }

    /**
     * Sets the Max number of input digits for transition from this node.
     * @param maxTransitionInputDigit
     * @return the current node instance
     */
    @JsonIgnore
    public Node setMaxTransitionInputDigit(Integer maxTransitionInputDigit) {
        this.maxTransitionInputDigit = maxTransitionInputDigit;
        return this;
    }


    public Integer getMaxTransitionTimeout() {
        return maxTransitionTimeout;
    }

    /**
     * Sets the max timeout in milliseconds for transition from this node.
     * @param maxTransitionTimeout
     * @return the current node instance
     */
    @JsonIgnore
    public Node setMaxTransitionTimeout(Integer maxTransitionTimeout) {
        this.maxTransitionTimeout = maxTransitionTimeout;
        return this;
    }

    public String getTransitionKeyEndMarker() {
        return transitionKeyEndMarker;
    }

    public Node setTransitionKeyEndMarker(String transitionKeyEndMarker) {
        this.transitionKeyEndMarker = transitionKeyEndMarker;
        return this;
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
        if (this == o) {
            return true;
        }

        if (!(o instanceof Node)) {
            return false;
        }

        Node node = (Node) o;

        return Objects.equals(actionsAfter, node.actionsAfter) && Objects.equals(actionsBefore, node.actionsBefore) &&
                Objects.equals(prompts, node.prompts) && Objects.equals(transitions, node.transitions);
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
