package org.motechproject.server.decisiontree.service;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.decisiontree.model.Tree;
import org.motechproject.server.decisiontree.TreeNodeLocator;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TreeNodeLocatorTest {

    Tree tree;

    TreeNodeLocator locator;

    @Before
    public void setUp() {
        tree = new Tree().setName("tree1").setRootNode(
                new Node().setTransitions(new Object[][]{
                        {"1", new Transition().setName("t1").setDestinationNode(new Node().setTransitions(new Object[][]{
                                {"1", new Transition().setName("sick1").setDestinationNode(new Node())},
                                {"2", new Transition().setName("sick2").setDestinationNode(new Node())},
                                {"3", new Transition().setName("sick3").setDestinationNode(new Node())},
                        }))},
                        {"2", new Transition().setName("ill").setDestinationNode(new Node())}
                }));
        locator = new TreeNodeLocator();
    }

    @Test
    public void testFindNode() {
        assertNotNull(locator.findNode(tree, "/"));
        assertNotNull(locator.findNode(tree, "/1/2"));
        assertNotNull(locator.findNode(tree, "/1/2/"));
        assertNotNull(locator.findNode(tree, "//1/2"));
        assertNotNull(locator.findNode(tree, "//1/2/"));
        assertNull(locator.findNode(tree, "/2/1/2/"));
        assertNull(locator.findNode(tree, "3"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTreeNull() {
        locator.findNode(null, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPathNull() {
        locator.findNode(tree, null);
    }

    @Test
    public void addPrompts() {
        final Node rootNode = tree.getRootNode();
        rootNode.setPrompts(new AudioPrompt());
        rootNode.addPrompts(new AudioPrompt());

        assertEquals(2, rootNode.getPrompts().size());
    }

    @Test
    public void addPromptToBeginning() {
        final Node rootNode = tree.getRootNode();
        AudioPrompt audioPrompt_1 = new AudioPrompt();
        audioPrompt_1.setName("1");
        AudioPrompt audioPrompt_2 = new AudioPrompt();
        audioPrompt_2.setName("2");

        rootNode.addPrompts(audioPrompt_1);
        assertEquals(1, rootNode.getPrompts().size());

        rootNode.addPromptToBeginning(audioPrompt_2);
        assertEquals(2, rootNode.getPrompts().size());
        assertEquals("2", rootNode.getPrompts().get(0).getName());
        assertEquals("1", rootNode.getPrompts().get(1).getName());
    }
}
