package org.motechproject.server.demo.model;

import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.List;

public class TreeRecord implements InitializingBean {
    private String name;
    private String description;
    List<NodeRecord> nodes;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public List<NodeRecord> getNodes() {
        return nodes;
    }

    public void setNodes(final List<NodeRecord> nodes) {
        this.nodes = nodes;
    }

    public boolean isCyclic() throws Exception {
        NodeRecord startNode = nodes.get(0);
        boolean cyclic = false;
        List<Long> visited = new ArrayList<>();

        for (TransitionRecord transition : startNode.getTransitions()) {
            visited.add(startNode.getId());

            cyclic = findCycle(transition.getNode(), visited);

            if (cyclic) {
                break;
            } else {
                visited.clear();
            }
        }

        return cyclic;
    }

    private boolean findCycle(Long nextId, List<Long> visited) throws Exception {
        boolean found = false;

        if (visited.contains(nextId)) {
            found = true;
        }

        if (!found) {
            visited.add(nextId);
            NodeRecord node = findNode(nextId);

            if (node == null) {
                throw new Exception(String.format("Not found node with id: %d", nextId));
            }

            if (node.getTransitions() != null) {
                for (TransitionRecord transition : node.getTransitions()) {
                    found = findCycle(transition.getNode(), visited);

                    if (found) {
                        break;
                    }
                }
            }
        }

        return found;
    }

    private NodeRecord findNode(final Long id) {
        NodeRecord found = null;

        for (NodeRecord node : nodes) {
            if (node.getId().equals(id)) {
                found = node;
                break;
            }
        }

        return found;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (getName() == null || getName().trim().isEmpty()) {
            throw new Exception("Tree name is required");
        }

        if (getNodes() == null || getNodes().size() == 0) {
            throw new Exception("Tree nodes is required");
        }
    }

    @Override
    public String toString() {
        return "TreeRecord{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", nodes=" + nodes +
                '}';
    }
}
