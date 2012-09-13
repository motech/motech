package org.motechproject.server.demo.service;

import org.junit.Test;
import org.motechproject.decisiontree.core.model.Action;
import org.motechproject.decisiontree.core.model.Node;
import org.motechproject.decisiontree.core.model.TextToSpeechPrompt;
import org.motechproject.decisiontree.core.model.Transition;
import org.motechproject.decisiontree.core.model.Tree;
import org.motechproject.server.demo.model.NodeRecord;
import org.motechproject.server.demo.model.TransitionRecord;
import org.motechproject.server.demo.model.TreeRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TreeConverterTest {

    @Test
    public void shouldConvertToTreeRecord() throws Exception {

        Tree tree = new Tree()
                .setName("Illness Tree")
                .setRootTransition(new Transition().setDestinationNode(new Node()
                        .setPrompts(new TextToSpeechPrompt().setMessage("if you feel sick select 1, if not select 2"))
                        .setTransitions(new Object[][]{
                                {"1", new Transition().setName("pressed1")
                                        .setDestinationNode(new Node()
                                                .setPrompts(new TextToSpeechPrompt().setMessage("if you have a fever select 1, if not select 3"))
                                                .setActionsAfter(new ArrayList<Action>() {{ add(Action.newBuilder().setEventId("1").build()); }})
                                                .setTransitions(new Object[][]{
                                                        {"1", new Transition().setName("pressed1").setDestinationNode(
                                                                new Node().setPrompts(new TextToSpeechPrompt().setMessage("come to the hospital now"))
                                                        )},
                                                        {"3", new Transition().setName("pressed3").setDestinationNode(
                                                                new Node().setPrompts(new TextToSpeechPrompt().setMessage("be patient, we will call you"))
                                                                .setActionsBefore(new ArrayList<Action>() {{ add(Action.newBuilder().setEventId("2").build()); add(Action.newBuilder().setEventId("3").build()); }})
                                                        )}
                                                })
                                        )},
                                {"2", new Transition().setName("pressed2")
                                        .setDestinationNode(new Node().setPrompts(new TextToSpeechPrompt().setMessage("Check with us again")))}
                        })
                ));

        TreeRecord converted = TreeConverter.convertToTreeRecord(tree);

        assertEquals("Illness Tree", converted.getName());
        assertEquals(5, converted.getNodes().size());

        NodeRecord one = converted.getNodes().get(0);
        NodeRecord two = converted.getNodes().get(1);
        NodeRecord three = converted.getNodes().get(2);
        NodeRecord four = converted.getNodes().get(3);
        NodeRecord five = converted.getNodes().get(4);

        assertNodeRecord(one, 0L, "if you feel sick select 1, if not select 2");
        assertNodeRecord(two, 1L, "Check with us again");
        assertNodeRecord(three, 2L, "if you have a fever select 1, if not select 3");
        assertNodeRecord(four, 3L, "be patient, we will call you");
        assertNodeRecord(five, 4L, "come to the hospital now");

        assertEquals(2, one.getTransitions().size());
        assertEquals(0, two.getTransitions().size());
        assertEquals(2, three.getTransitions().size());
        assertEquals(0, four.getTransitions().size());
        assertEquals(0, five.getTransitions().size());

        assertTransitionRecord(one.getTransitions().get(0), 0L, "2", 1L);
        assertTransitionRecord(one.getTransitions().get(1), 1L, "1", 2L);
        assertTransitionRecord(three.getTransitions().get(0), 2L, "3", 3L);
        assertTransitionRecord(three.getTransitions().get(1), 3L, "1", 4L);

        assertEquals(0, one.getActionsBefore().size());
        assertEquals(0, two.getActionsBefore().size());
        assertEquals(0, three.getActionsBefore().size());
        assertEquals(2, four.getActionsBefore().size());
        assertEquals(0, five.getActionsBefore().size());

        assertActions(four.getActionsBefore(), Arrays.asList("2", "3"));

        assertEquals(0, one.getActionsAfter().size());
        assertEquals(0, two.getActionsAfter().size());
        assertEquals(1, three.getActionsAfter().size());
        assertEquals(0, four.getActionsAfter().size());
        assertEquals(0, five.getActionsAfter().size());

        assertActions(three.getActionsAfter(), Arrays.asList("1"));
    }

    private void assertNodeRecord(final NodeRecord nodeRecord, final Long id, final String message) {
        assertEquals(String.format("Message%d", id), nodeRecord.getName());
        assertEquals(id, nodeRecord.getId());
        assertEquals(message, nodeRecord.getMessage());
    }

    private void assertTransitionRecord(final TransitionRecord transitionRecord, final Long id, final String key, final Long nodeId) {
        assertEquals(id, transitionRecord.getId());
        assertEquals(key, transitionRecord.getKey());
        assertEquals(nodeId, transitionRecord.getNode());
    }

    private void assertActions(final List<String> actual, List<String> expected) {
        assertEquals(expected.size(), actual.size());

        for (int i = 0; i < actual.size(); ++i) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }

}
