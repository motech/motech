package org.motechproject.server.decisiontree.service;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.decisiontree.FlowSession;
import org.motechproject.decisiontree.model.*;
import org.motechproject.decisiontree.repository.AllTrees;
import org.motechproject.server.decisiontree.TreeNodeLocator;

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
    private BaseTransition rootTransition;
    private BaseTransition transition;

    @Before
    public void SetUp() {
        initMocks(this);
        rootTransition = new Transition()
                .setName("pillTakenOnTime")
                .setDestinationNode(nextNode).addOperations(new RootNodeOpteration());
        transition = new Transition().addOperations(new NextOperation());

        nextNode = new Node().setTransitions(new Object[][]{
                {"1", transition
                }
        });
        rootNode = new Node()
                .setTransitions(new Object[][]{
                        {"1", rootTransition

                        }
                });

        pillReminderTree = new Tree()
                .setName("PillReminderTree")
                .setRootNode(rootNode);

        when(allTrees.findByName(pillReminderTree.getName())).thenReturn(pillReminderTree);
        decisionTreeService = new DecisionTreeServiceImpl(allTrees, treeNodeLocator);
    }

    @Test
    public void shouldFetchCommandForRootNode() {
        when(treeNodeLocator.findNode(pillReminderTree, "", null)).thenReturn(rootNode);
        Node nextNode = decisionTreeService.getNode(pillReminderTree.getName(), "", null);
        assertEquals(RootNodeOpteration.class,nextNode.getTransitions().get("1").getOperations().get(0).getClass());
    }

    @Test
    public void shouldFetchNextCommand() {
        when(treeNodeLocator.findNode(pillReminderTree, "/1", null)).thenReturn(nextNode);
        Node nextNode = decisionTreeService.getNode(pillReminderTree.getName(), "/1", null);
        assertEquals(NextOperation.class, nextNode.getTransitions().get("1").getOperations().get(0).getClass());
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
