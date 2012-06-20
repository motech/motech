package org.motechproject.server.decisiontree.service;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.decisiontree.model.Action;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.scheduler.context.EventContext;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.event.EventRelay;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests TreeEventProcessor by mocking EventRelay
 *
 * @author yyonkov
 */
@RunWith(MockitoJUnitRunner.class)
public class TreeEventProcessorTest {
    private Map<String, Object> params = new HashMap<String, Object>();
    private Node node = new Node()
            .setActionsBefore(Arrays.<Action>asList(
                    Action.newBuilder().setEventId("eventbefore1").build(),
                    Action.newBuilder().setEventId("eventbefore2").build(),
                    Action.newBuilder().setEventId("eventbefore3").build()
            ))
            .setActionsAfter(Arrays.<Action>asList(
                    Action.newBuilder().setEventId("eventafter1").build(),
                    Action.newBuilder().setEventId("eventafter2").build()
            ))
            .setTransitions(new Object[][]{
                    {"1", new Transition().setName("tr1").setActions(Action.newBuilder().setEventId("eventr1").build()
                    )}
            });

    @InjectMocks
    TreeEventProcessor treeEventProcessor = new TreeEventProcessor();

    @Mock
    EventContext context;

    @Mock
    EventRelay eventRelay;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        params.put("patientId", "001");
        when(context.getEventRelay()).thenReturn(eventRelay);
    }

    @Test
    public void testNodeActionsBefore() {
        treeEventProcessor.sendActionsBefore(node, "/", params);
        verify(eventRelay, times(3)).sendEventMessage(any(MotechEvent.class));
    }

    @Test
    public void testNodeActionsAfter() {
        treeEventProcessor.sendActionsAfter(node, "/", params);
        verify(eventRelay, times(2)).sendEventMessage(any(MotechEvent.class));
    }

    @Test
    public void testTransitionActions() {
        treeEventProcessor.sendTransitionActions((Transition) node.getTransitions().get("1"), params);
        verify(eventRelay, times(1)).sendEventMessage(any(MotechEvent.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testActionsEdgeCase1() {
        treeEventProcessor.sendTransitionActions(null, params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testActionsEdgeCase2() {
        treeEventProcessor.sendTransitionActions(new Transition(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testActionsEdgeCase3() {
        treeEventProcessor.sendActionsBefore(null, "/", params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testActionsEdgeCase4() {
        treeEventProcessor.sendActionsBefore(new Node(), "/", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testActionsEdgeCase5() {
        treeEventProcessor.sendActionsAfter(null, "/", params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testActionsEdgeCase6() {
        treeEventProcessor.sendActionsAfter(new Node(), "/", null);
    }
}
