package org.motechproject.decisiontree.model;

import org.junit.Test;

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
                .setRootNode(new Node()
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
                );
//        System.out.print(t);
        assertNotNull(t);
        assertEquals("tree1", t.getName());
        assertEquals("desc", t.getDescription());
        assertNotNull(t.getRootNode());
        assertNotNull(t.getRootNode().getTransitions());
        assertEquals(2, t.getRootNode().getTransitions().size());
    }

}
