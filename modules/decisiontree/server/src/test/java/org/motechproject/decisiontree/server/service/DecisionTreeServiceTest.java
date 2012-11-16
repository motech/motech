package org.motechproject.decisiontree.server.service;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.decisiontree.core.DecisionTreeService;
import org.motechproject.decisiontree.core.DecisionTreeServiceImpl;
import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.core.TreeNodeLocator;
import org.motechproject.decisiontree.core.model.INodeOperation;
import org.motechproject.decisiontree.core.model.Node;
import org.motechproject.decisiontree.core.model.Transition;
import org.motechproject.decisiontree.core.model.Tree;
import org.motechproject.decisiontree.core.repository.AllTrees;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class DecisionTreeServiceTest {
    @Mock
    private AllTrees allTrees;

    @Mock
    private TreeNodeLocator treeNodeLocator;

    private DecisionTreeService decisionTreeService;

    private Tree pillReminderTree;
    private Node rootNode;
    private Node nextNode;

    @Before
    public void SetUp() {
        initMocks(this);
        nextNode = new Node()
                .addOperations(new NextOperation());
        rootNode = new Node()
                .addOperations(new RootNodeOpteration())
                .setTransitions(new Object[][]{
                        {"1", new Transition()
                                .setName("pillTakenOnTime")
                                .setDestinationNode(nextNode)

                        }
                });

        pillReminderTree = new Tree()
                .setName("PillReminderTree")
                .setRootTransition(new Transition().setDestinationNode(rootNode));

        when(allTrees.findByName(pillReminderTree.getName())).thenReturn(pillReminderTree);
        decisionTreeService = new DecisionTreeServiceImpl(allTrees, treeNodeLocator);
    }

    @Test
    public void shouldFetchCommandForRootNode() {
        when(treeNodeLocator.findNode(pillReminderTree, "", null)).thenReturn(rootNode);
        Node nextNode = decisionTreeService.getNode(pillReminderTree.getName(), "", null);
        assertEquals(RootNodeOpteration.class, nextNode.getOperations().get(0).getClass());
    }

    @Test
    public void shouldFetchNextCommand() {
        when(treeNodeLocator.findNode(pillReminderTree, "/1", null)).thenReturn(nextNode);
        Node nextNode = decisionTreeService.getNode(pillReminderTree.getName(), "/1", null);
        assertEquals(NextOperation.class, nextNode.getOperations().get(0).getClass());
    }

    private class RootNodeOpteration implements INodeOperation {
        @Override
        public void perform(String userInput, FlowSession session) {
        }
    }

    private class NextOperation implements INodeOperation {
        @Override
        public void perform(String userInput, FlowSession session) {

        }
    }
}
