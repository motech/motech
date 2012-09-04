package org.motechproject.server.demo.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class TreeRecordTest {

    @Test
    public void shouldFoundCycle8Nodes() throws Exception {
        TreeRecord tree = new TreeRecord();
        tree.setName("Cycle");
        tree.setNodes(createNodes(8));

        for (int i = 0; i < 7; ++i) {
            createTransition(tree.getNodes().get(i), Long.valueOf(i + 1));
        }

        createTransition(tree.getNodes().get(7), 0L);

        assertTrue(tree.isCyclic());
    }

    @Test
    public void shouldFoundCycle9Nodes() throws Exception {
        TreeRecord tree = new TreeRecord();
        tree.setName("Cycle");
        tree.setNodes(createNodes(9));

        createTransition(tree.getNodes().get(0), 1L, 2L, 8L);
        createTransition(tree.getNodes().get(1), 5L);
        createTransition(tree.getNodes().get(2), 3L, 4L);
        createTransition(tree.getNodes().get(8), 6L, 7L);
        createTransition(tree.getNodes().get(7), 4L);
        createTransition(tree.getNodes().get(4), 0L);

        assertTrue(tree.isCyclic());
    }

    @Test
    public void shouldFoundCycle6Nodes() throws Exception {
        TreeRecord tree = new TreeRecord();
        tree.setName("Cycle");
        tree.setNodes(createNodes(5));

        createTransition(tree.getNodes().get(0), 1L, 2L);
        createTransition(tree.getNodes().get(1), 3L);
        createTransition(tree.getNodes().get(2), 4L);
        createTransition(tree.getNodes().get(4), 0L);

        assertTrue(tree.isCyclic());
    }

    @Test
    public void shouldFoundCycle16Nodes() throws Exception {
        TreeRecord tree = new TreeRecord();
        tree.setName("Cycle");
        tree.setNodes(createNodes(16));

        createTransition(tree.getNodes().get(0), 1L, 7L);
        createTransition(tree.getNodes().get(1), 2L, 9L);
        createTransition(tree.getNodes().get(7), 8L);
        createTransition(tree.getNodes().get(9), 10L);
        createTransition(tree.getNodes().get(2), 3L, 14L);
        createTransition(tree.getNodes().get(3), 12L ,14L, 4L);
        createTransition(tree.getNodes().get(4), 5L);
        createTransition(tree.getNodes().get(5), 6L, 11L);
        createTransition(tree.getNodes().get(6), 15L);
        createTransition(tree.getNodes().get(11), 15L);
        createTransition(tree.getNodes().get(15), 0L);

        assertTrue(tree.isCyclic());
    }

    private List<NodeRecord> createNodes(final int count) {
        List<NodeRecord> list = new ArrayList<>(count);

        for (int i = 0; i < count; ++i) {
            NodeRecord record = new NodeRecord();
            record.setId(Long.valueOf(i));
            record.setName(String.format("Name %d", i));
            record.setMessage(String.format("Message %d", i));

            list.add(record);
        }

        return list;
    }

    private void createTransition(final NodeRecord node, Long... destination) {
        List<TransitionRecord> list = new ArrayList<>();

        for (Long dest : Arrays.asList(destination)) {
            TransitionRecord record = new TransitionRecord();
            record.setKey(String.valueOf(dest));
            record.setNode(dest);

            list.add(record);
        }

        node.setTransitions(list);
    }
}
