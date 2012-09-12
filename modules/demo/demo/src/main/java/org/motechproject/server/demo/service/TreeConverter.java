package org.motechproject.server.demo.service;

import org.motechproject.decisiontree.core.model.Action;
import org.motechproject.decisiontree.core.model.ITransition;
import org.motechproject.decisiontree.core.model.Node;
import org.motechproject.decisiontree.core.model.Prompt;
import org.motechproject.decisiontree.core.model.TextToSpeechPrompt;
import org.motechproject.decisiontree.core.model.Transition;
import org.motechproject.decisiontree.core.model.Tree;
import org.motechproject.server.demo.model.NodeRecord;
import org.motechproject.server.demo.model.TransitionRecord;
import org.motechproject.server.demo.model.TreeRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeConverter {
    public static Tree convertToTree(final TreeRecord tree) throws Exception {
        tree.afterPropertiesSet();

        Map<Long, Node> convertedNodes = new HashMap<>(tree.getNodes().size());
        Long rootId = tree.getNodes().get(0).getId();

        for (NodeRecord node : tree.getNodes()) {
            node.afterPropertiesSet();

            TextToSpeechPrompt prompt = new TextToSpeechPrompt().setMessage(node.getMessage());

            List<String> actionBeforeRecords = node.getActionsBefore();
            List<Action> actionsBefore = new ArrayList<>(actionBeforeRecords.size());

            for (String action : actionBeforeRecords) {
                actionsBefore.add(Action.newBuilder().setEventId(action).build());
            }

            List<String> actionAfterRecords = node.getActionsAfter();
            List<Action> actionsAfter = new ArrayList<>(actionBeforeRecords.size());

            for (String action : actionAfterRecords) {
                actionsAfter.add(Action.newBuilder().setEventId(action).build());
            }

            convertedNodes.put(node.getId(), new Node().setActionsAfter(actionsAfter).setActionsBefore(actionsBefore).setPrompts(prompt));
        }

        for (NodeRecord node : tree.getNodes()) {
            Object[][] transitions = new Object[node.getTransitions().size()][2];

            for (int i = 0; i < node.getTransitions().size(); ++i) {
                TransitionRecord transition = node.getTransitions().get(i);
                transition.afterPropertiesSet();

                Node destinationNode = convertedNodes.get(transition.getNode());
                String name = String.format("pressed%s", transition.getKey());

                if (destinationNode == null) {
                    throw new Exception(String.format("Cant found node with ID: %d for transition: %s", node.getId(), transition));
                }

                transitions[i][0] = transition.getKey();
                transitions[i][1] = new Transition().setName(name).setDestinationNode(destinationNode);
            }

            convertedNodes.get(node.getId()).setTransitions(transitions);
        }

        return new Tree()
                .setName(tree.getName())
                .setDescription(tree.getDescription())
                .setRootTransition(new Transition().setDestinationNode(convertedNodes.get(rootId)));
    }

    public static TreeRecord convertToTreeRecord(final Tree tree) {
        TreeRecord treeRecord = new TreeRecord();
        treeRecord.setName(tree.getName());
        treeRecord.setDescription(tree.getDescription());

        Map<Long, Node> nodes = new HashMap<>();
        List<NodeRecord> nodeRecords = new ArrayList<>();

        nodes.put(0L, tree.getRootTransition().getDestinationNode(null, null));

        Long transitionId = 0L;
        Long nodeId = 0L;
        Long count = 1L;

        while(!nodes.isEmpty()) {
            Node next = nodes.get(nodeId);

            NodeRecord converted = convertToNodeRecord(next, nodeId);
            converted.setTransitions(new ArrayList<TransitionRecord>());

            for (Map.Entry<String, ITransition> entry : next.getTransitions().entrySet()) {
                TransitionRecord transitionRecord = new TransitionRecord();
                transitionRecord.setId(transitionId++);
                transitionRecord.setKey(entry.getKey());
                transitionRecord.setNode(count);

                nodes.put(count, entry.getValue().getDestinationNode(null, null));

                converted.getTransitions().add(transitionRecord);
                ++count;
            }

            nodeRecords.add(converted);
            nodes.remove(nodeId);
            ++nodeId;
        }

        treeRecord.setNodes(nodeRecords);

        return treeRecord;
    }

    private static NodeRecord convertToNodeRecord(final Node node, final Long id) {
        NodeRecord nodeRecord = new NodeRecord();
        nodeRecord.setId(id);
        nodeRecord.setName(String.format("Message%d", id));

        Prompt p = node.getPrompts().get(0);

        if (p instanceof TextToSpeechPrompt) {
            nodeRecord.setMessage(((TextToSpeechPrompt) p).getMessage());
        }

        List<Action> actionBefore = node.getActionsBefore();
        List<String> actionBeforeRecords = new ArrayList<>(actionBefore.size());

        for (Action action : actionBefore) {
            actionBeforeRecords.add(action.getEventId());
        }

        nodeRecord.setActionsBefore(actionBeforeRecords);

        List<Action> actionAfter = node.getActionsAfter();
        List<String> actionAfterRecords = new ArrayList<>(actionAfter.size());

        for (Action action : actionAfter) {
            actionAfterRecords.add(action.getEventId());
        }

        nodeRecord.setActionsAfter(actionAfterRecords);

        return nodeRecord;
    }

}
