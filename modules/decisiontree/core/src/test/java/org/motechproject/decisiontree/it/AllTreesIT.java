package org.motechproject.decisiontree.it;

import org.ektorp.CouchDbConnector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.core.model.AudioPrompt;
import org.motechproject.decisiontree.core.model.ITransition;
import org.motechproject.decisiontree.core.model.ITreeCommand;
import org.motechproject.decisiontree.core.model.Node;
import org.motechproject.decisiontree.core.model.TextToSpeechPrompt;
import org.motechproject.decisiontree.core.model.Transition;
import org.motechproject.decisiontree.core.model.Tree;
import org.motechproject.decisiontree.core.repository.AllTrees;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/motech/*.xml"})
public class AllTreesIT extends SpringIntegrationTest {

    @Autowired
    AllTrees allTrees;
    @Autowired @Qualifier("treesDatabase")
    private CouchDbConnector connector;

    @Test
    public void shouldStoreTree() throws Exception {
        Tree tree = new Tree();
        HashMap<String, ITransition> transitions = new HashMap<String, ITransition>();
        final Node textToSpeechNode = new Node().addPrompts(new TextToSpeechPrompt().setName("Say this")).setMaxTransitionTimeout(25);
        transitions.put("1", new Transition().setDestinationNode(textToSpeechNode));
        final Node audioPromptNode = new Node().addPrompts(new AudioPrompt().setName("abc")).setTransitions(transitions);
        tree.setName("tree").setRootTransition(new Transition().setDestinationNode(audioPromptNode));

        allTrees.addOrReplace(tree);
        markForDeletion(tree);

        Tree fromDb = allTrees.get(tree.getId());
        assertNotNull(fromDb);
        final Node rootNodeFromDb = fromDb.getRootTransition().getDestinationNode(null, Mockito.mock(FlowSession.class));
        assertNotNull(rootNodeFromDb);
        assertEquals(AudioPrompt.class.getName(), rootNodeFromDb.getPrompts().get(0).getClass().getName());

        final Node nextNode = rootNodeFromDb.getTransitions().get("1").getDestinationNode("1", null);
        assertEquals(TextToSpeechPrompt.class.getName(), nextNode.getPrompts().get(0).getClass().getName());
        assertEquals((Integer) 25, nextNode.getMaxTransitionTimeout());
        assertNull(nextNode.getMaxTransitionInputDigit());
    }

    @Test
    public void shouldStoreTreeWithCommands() throws Exception {
        Tree tree = new Tree();
        tree.setName("tree").setRootTransition(new Transition().setDestinationNode(
                new Node().addPrompts(new AudioPrompt().setCommand(new TestCommand()))
        ));
        allTrees.addOrReplace(tree);
        markForDeletion(tree);

        Tree fromDb = allTrees.get(tree.getId());
        assertNotNull(fromDb);
        final ITreeCommand command = fromDb.getRootTransition().getDestinationNode(null, Mockito.mock(FlowSession.class)).getPrompts().get(0).getCommand();
        assertEquals(TestCommand.class.getName(), command.getClass().getName());
        final String[] result = command.execute(null);
        assertEquals("ok", result[0]);

    }

    @Test
    public void shouldFindTreeByName() throws Exception {
        Tree tree = new Tree();
        tree.setName("someTree");
        tree.setRootTransition(new Transition().setDestinationNode(new Node().addPrompts(new AudioPrompt().setName("audioFile"))));
        allTrees.addOrReplace(tree);
        markForDeletion(tree);

        final Tree fromDb = allTrees.findByName("someTree");
        assertNotNull(fromDb);
        assertEquals("audioFile", fromDb.getRootTransition().getDestinationNode(null, Mockito.mock(FlowSession.class)).getPrompts().get(0).getName());
    }

    @Test
    public void shouldAddOrReplaceTree() throws Exception {
        Tree tree = new Tree();
        tree.setName("someTree");
        tree.setRootTransition(new Transition().setDestinationNode(
                new Node().addPrompts(new AudioPrompt().setName("audioFile1"))
        ));
        allTrees.addOrReplace(tree);
        markForDeletion(tree);

        tree = new Tree();
        tree.setName("someTree");
        tree.setRootTransition(new Transition().setDestinationNode(new Node().addPrompts(new AudioPrompt().setName("audioFile2"))));
        allTrees.addOrReplace(tree);
        markForDeletion(tree);

        final Tree someTree = allTrees.findByName("someTree");
        assertEquals("audioFile2", someTree.getRootTransition().getDestinationNode(null, Mockito.mock(FlowSession.class)).getPrompts().get(0).getName());
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return connector;
    }
}

