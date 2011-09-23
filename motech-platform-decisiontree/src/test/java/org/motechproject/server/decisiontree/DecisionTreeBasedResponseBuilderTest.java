package org.motechproject.server.decisiontree;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.decisiontree.model.*;
import org.motechproject.server.service.ivr.*;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class DecisionTreeBasedResponseBuilderTest {
    private DecisionTreeBasedResponseBuilder treeBasedResponseBuilder;

    @Mock
    private IVRContext ivrContext;
    @Mock
    private IVRSession ivrSession;
    @Mock
    private IVRResponseBuilder ivrResponseBuilder;
    @Mock
    private PostTreeCallContinuation postTreeCallContinuation;

    @Before
    public void setUp() {
        initMocks(this);
        when(ivrSession.getPreferredLanguageCode()).thenReturn("en");
        when(ivrContext.ivrSession()).thenReturn(ivrSession);
        treeBasedResponseBuilder = new DecisionTreeBasedResponseBuilder(postTreeCallContinuation);
    }

    @Test
    public void shouldAddCollectDtmfIfTheNodeHasTransitions() {
        Node rootNode = new Node()
                .setPrompts(new AudioPrompt().setName("foo"))
                .setTransitions(new Object[][]{
                        {"1", new Transition()
                                .setDestinationNode(new Node()
                                        .setPrompts(new AudioPrompt().setName("bar")))
                        },
                        {"2", new Transition()
                                .setDestinationNode(new Node()
                                        .setPrompts(new AudioPrompt().setName("baz")))
                        }});

        nextResponse(rootNode, false);

        verify(ivrResponseBuilder).collectDtmf(1);
        verify(ivrResponseBuilder).withPlayAudios("foo");
        verify(ivrResponseBuilder, never()).withPlayTexts(Matchers.<String>any());
    }

    private IVRResponseBuilder nextResponse(Node rootNode, boolean retryOnIncorrectUserAction) {
        return treeBasedResponseBuilder.ivrResponse(rootNode, ivrContext, ivrResponseBuilder, retryOnIncorrectUserAction);
    }

    @Test
    public void shouldHandOverControlToCallContinuationIfTheNodeDoesNotHaveAnyTransitions() {
        Node rootNode = new Node()
                .setPrompts(new AudioPrompt().setName("foo"));
        nextResponse(rootNode, false);
        verify(postTreeCallContinuation, times(1)).continueCall(ivrContext, ivrResponseBuilder);
    }

    @Test
    public void whenAudioCommandReturnsNullThenItShouldNotGetAddedToResponse() {
        Node rootNode =  new Node()
                .setPrompts(new AudioPrompt().setCommand(new ReturnEmptyCommand()))
                .setTransitions(new Object[][]{
                        {"1", new Transition()
                                .setDestinationNode(new Node()
                                        .setPrompts(new AudioPrompt().setName("bar")))
                        },
                        {"2", new Transition()
                                .setDestinationNode(new Node()
                                        .setPrompts(new AudioPrompt().setName("baz")))
                        }});
        nextResponse(rootNode, false);
        verify(ivrResponseBuilder, never()).withPlayAudios(Matchers.<String>any());
    }

    @Test
    public void createMultiplePlayAudiosWhenACommandReturnsMultiplePrompts() {
        Node rootNode = new Node()
                .setPrompts(new AudioPrompt().setCommand(new ReturnTwoPromptsCommand())).
                        setTransitions(new Object[][]{
                                {"1", new Transition()
                                        .setDestinationNode(new Node()
                                                .setPrompts(new AudioPrompt().setName("bar")))
                                },
                                {"2", new Transition()
                                        .setDestinationNode(new Node()
                                                .setPrompts(new AudioPrompt().setName("baz")))
                                }});
        nextResponse(rootNode, false);
        verify(ivrResponseBuilder,times(2)).withPlayAudios(Matchers.<String>any());
    }

    @Test
    public void shouldAddOnlyMenuAudioPromptsToReplayOnIncorrectUserResponse() {
        Node rootNode = new Node()
                .setPrompts(new AudioPrompt().setName("hello"), new MenuAudioPrompt().setName("menu")).
                        setTransitions(new Object[][]{
                                {"1", new Transition()
                                        .setDestinationNode(new Node()
                                                .setPrompts(new AudioPrompt().setName("bar")))
                                },
                                {"2", new Transition()
                                        .setDestinationNode(new Node()
                                                .setPrompts(new AudioPrompt().setName("baz")))
                                }});

        IVRResponseBuilder responseBuilder = nextResponse(rootNode, true);
        verify(responseBuilder,times(1)).withPlayAudios(Matchers.<String>any());
        verify(responseBuilder).withPlayAudios("menu");
    }

    @Test
    public void shouldExecuteCommandsInMenuAudioPromptsDuringReplayOnIncorrectUserResponse() {
        ITreeCommand mockCommand = mock(ITreeCommand.class);
        when(mockCommand.execute(any())).thenReturn(new String[]{});

        MenuAudioPrompt menu = new MenuAudioPrompt();
        menu.setName("menu");
        menu.setCommand(mockCommand);

        Node rootNode = new Node()
                .setPrompts(new AudioPrompt().setName("hello"), menu);
        nextResponse(rootNode, true);
        verify(mockCommand, times(1)).execute(any());
    }

    class ReturnEmptyCommand implements ITreeCommand {
        @Override
        public String[] execute(Object o) {
            return new String[0];
        }
    }

    class ReturnTwoPromptsCommand implements ITreeCommand {
        @Override
        public String[] execute(Object o) {
            return new String[]{"a", "b"};
        }
    }
}
