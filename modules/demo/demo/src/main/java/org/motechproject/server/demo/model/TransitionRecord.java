package org.motechproject.server.demo.model;

import org.springframework.beans.factory.InitializingBean;

public class TransitionRecord implements InitializingBean {
    private Long id;
    private String key;
    private Long node;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public Long getNode() {
        return node;
    }

    public void setNode(final Long node) {
        this.node = node;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (getKey() == null || getKey().trim().isEmpty()) {
            throw new Exception("Transition key is required");
        }

        if (getNode() == null) {
            throw new Exception("Transition node id is required");
        }
    }

    @Override
    public String toString() {
        return "TransitionRecord{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", node=" + node +
                '}';
    }
}
