package org.motechproject.server.demo.model;

import org.springframework.beans.factory.InitializingBean;

import java.util.List;

public class NodeRecord implements InitializingBean {
    private Long id;
    private String name;
    private String message;
    private List<String> actionsBefore;
    private List<String> actionsAfter;
    private List<TransitionRecord> transitions;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public List<String> getActionsBefore() {
        return actionsBefore;
    }

    public void setActionsBefore(final List<String> actionsBefore) {
        this.actionsBefore = actionsBefore;
    }

    public List<String> getActionsAfter() {
        return actionsAfter;
    }

    public void setActionsAfter(final List<String> actionsAfter) {
        this.actionsAfter = actionsAfter;
    }

    public List<TransitionRecord> getTransitions() {
        return transitions;
    }

    public void setTransitions(final List<TransitionRecord> transitions) {
        this.transitions = transitions;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (getMessage() == null || getMessage().trim().isEmpty()) {
            throw new Exception("Message content is required");
        }
    }

    @Override
    public String toString() {
        return "NodeRecord{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", message='" + message + '\'' +
                ", actionsBefore=" + actionsBefore +
                ", actionsAfter=" + actionsAfter +
                ", transitions=" + transitions +
                '}';
    }
}
