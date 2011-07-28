package org.motechproject.server.decisiontree.service;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.decisiontree.model.Tree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationDecisionTree.xml"})
public class DecisionTreeServiceTest {

    @Autowired
    private DecisionTreeService decisionTreeService;

    private Tree pillReminderTree;

    @Before
    public void SetUp() {
        Node rootNode = Node.newBuilder()
                .setTreeCommand(new RootNodeCommand())
                .setTransitions(new Object[][]{
                        {"1", Transition.newBuilder()
                                .setName("pillTakenOnTime")
                                .setDestinationNode(
                                        Node.newBuilder()
                                                .setTreeCommand(new NextCommand())
                                                .build())
                                .build()
                        }
                })
                .build();

        pillReminderTree = Tree.newBuilder()
                .setName("PillReminderTree")
                .setRootNode(rootNode)
                .build();

        pillReminderTree = Tree.newBuilder()
                .setName("PillReminderTree")
                .setRootNode(rootNode)
                .build();
    }

    @Test
    public void shouldFetchCommandForRootNode () {
        Node nextNode = decisionTreeService.getNode("", "");
        assertEquals(RootNodeCommand.class, nextNode.getTreeCommand().getClass());
    }

    @Test
    public void shouldFetchNextCommand() {
        Node nextNode = decisionTreeService.getNode("/", "1");
        assertEquals(NextCommand.class, nextNode.getTreeCommand().getClass());
    }

    private class RootNodeCommand implements ITreeCommand {
        @Override
        public String execute(Object obj) {
            return null;
        }
    }

    private class NextCommand implements ITreeCommand {

        @Override
        public String execute(Object obj) {
            return null;
        }
    }
}
