package org.motechproject.server.demo.ex;

import org.motechproject.server.demo.model.TransitionRecord;

public class NodeNotFoundException extends Exception {

    private Long nodeId;

    public NodeNotFoundException(Long nodeId) {
        super(String.format("Not found node with id: %d", nodeId));
        this.nodeId = nodeId;
    }

    public NodeNotFoundException(Long nodeId, TransitionRecord transition) {
        super(String.format("Cant found node with ID: %d for transition: %s", nodeId, transition));
        this.nodeId = nodeId;
    }

    public Long getNodeId() {
        return nodeId;
    }
}
