package org.motechproject.decisiontree.model;

import org.junit.Test;
import org.mockito.Mockito;
import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.core.model.Action;
import org.motechproject.decisiontree.core.model.Node;
import org.motechproject.decisiontree.core.model.TextToSpeechPrompt;
import org.motechproject.decisiontree.core.model.Transition;
import org.motechproject.decisiontree.core.model.Tree;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Test model Builders
 *
 * @author yyonkov
 */
public class BuildersTest {
    /**
     * Example of how to use Builders for easy tree building
     */
    @Test
    public void testTreeBuilder() {
        Tree t = new Tree()
                .setName("tree1")
                .setDescription("desc")
                .setRootTransition(new Transition().setDestinationNode(new Node()
                        .setActionsBefore(Arrays.asList(Action.newBuilder()
                                .setEventId("event_x")
                                .build()))
                        .setPrompts(new TextToSpeechPrompt()
                                .setMessage("haha"))
                        .setTransitions(new Object[][]{
                                {"1", new Transition()
                                        .setName("sick")},
                                {"2", new Transition()
                                        .setName("healthy")}
                        })
                ));
//        System.out.print(t);
        assertNotNull(t);
        assertEquals("tree1", t.getName());
        assertEquals("desc", t.getDescription());
        assertNotNull(t.getRootTransition().getDestinationNode(null, Mockito.mock(FlowSession.class)).getTransitions());
        assertNotNull(t.getRootTransition().getDestinationNode(null, Mockito.mock(FlowSession.class)));
        assertEquals(2, t.getRootTransition().getDestinationNode(null, Mockito.mock(FlowSession.class)).getTransitions().size());
    }

}
