package org.motechproject.decisiontree.core.model;

import org.junit.Test;
import org.mockito.Mockito;
import org.motechproject.decisiontree.core.FlowSession;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
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
                        .setActionsBefore(asList(Action.newBuilder()
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

        assertNotNull(t);
        assertEquals("tree1", t.getName());
        assertEquals("desc", t.getDescription());
        assertNotNull(t.getRootTransition().getDestinationNode(null, Mockito.mock(FlowSession.class)).getTransitions());
        assertNotNull(t.getRootTransition().getDestinationNode(null, Mockito.mock(FlowSession.class)));
        assertEquals(asList("healthy", "sick"), extract(t.getRootTransition().getDestinationNode(null, Mockito.mock(FlowSession.class)).getTransitions(),
                on(Transition.class).getName()));
    }

}
