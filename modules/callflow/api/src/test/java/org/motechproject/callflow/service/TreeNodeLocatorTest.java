package org.motechproject.callflow.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.core.TreeNodeLocator;
import org.motechproject.decisiontree.core.model.AudioPrompt;
import org.motechproject.decisiontree.core.model.Node;
import org.motechproject.decisiontree.core.model.Prompt;
import org.motechproject.decisiontree.core.model.Transition;
import org.motechproject.decisiontree.core.model.Tree;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class TreeNodeLocatorTest {

    Tree tree;

    @InjectMocks
    TreeNodeLocator locator = new TreeNodeLocator();

    @Mock
    ApplicationContext applicationContext;
    @Mock
    AutowireCapableBeanFactory autowireCapableBeanFactory;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(applicationContext.getAutowireCapableBeanFactory()).thenReturn(autowireCapableBeanFactory);
        doNothing().when(autowireCapableBeanFactory).autowireBean(any());
        tree = new Tree().setName("tree1").setRootTransition(new Transition().setDestinationNode(
                new Node().setTransitions(new Object[][]{
                        {"1", new Transition().setName("t1").setDestinationNode(new Node().setTransitions(new Object[][]{
                                {"1", new Transition().setName("sick1").setDestinationNode(new Node())},
                                {"2", new Transition().setName("sick2").setDestinationNode(new Node())},
                                {"3", new Transition().setName("sick3").setDestinationNode(new Node())},
                        }))},
                        {"2", new Transition().setName("ill").setDestinationNode(new Node())}
                })));

    }

    @Test
    public void testFindNode() {
        assertNotNull(locator.findNode(tree, "/", null));
        assertNotNull(locator.findNode(tree, "/1/2", null));
        assertNotNull(locator.findNode(tree, "/1/2/", null));
        assertNotNull(locator.findNode(tree, "//1/2", null));
        assertNotNull(locator.findNode(tree, "//1/2/", null));
        assertNull(locator.findNode(tree, "/2/1/2/", null));
        assertNull(locator.findNode(tree, "3", null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTreeNull() {
        locator.findNode(null, "", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPathNull() {
        locator.findNode(tree, null, null);
    }

    @Test
    public void addPrompts() {
        final Node rootNode = tree.getRootTransition().getDestinationNode(null, Mockito.mock(FlowSession.class));
        rootNode.setPrompts(new AudioPrompt().setName("1"));
        rootNode.addPrompts(new AudioPrompt().setName("2"));

        assertEquals(asList("1", "2"), extract(rootNode.getPrompts(), on(Prompt.class).getName()));
    }

    @Test
    public void addPromptToBeginning() {
        final Node rootNode = tree.getRootTransition().getDestinationNode(null, Mockito.mock(FlowSession.class));
        AudioPrompt audioPrompt_1 = new AudioPrompt();
        audioPrompt_1.setName("1");
        AudioPrompt audioPrompt_2 = new AudioPrompt();
        audioPrompt_2.setName("2");

        rootNode.addPrompts(audioPrompt_1);
        assertEquals(asList(audioPrompt_1), rootNode.getPrompts());

        rootNode.addPromptToBeginning(audioPrompt_2);
        assertEquals(asList(audioPrompt_2, audioPrompt_1), rootNode.getPrompts());
    }
}
