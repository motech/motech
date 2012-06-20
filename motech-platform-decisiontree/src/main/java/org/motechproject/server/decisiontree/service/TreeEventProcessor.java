package org.motechproject.server.decisiontree.service;

import org.motechproject.decisiontree.model.Action;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.scheduler.context.EventContext;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.event.EventRelay;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * Responsible for emitting various tree events, e.g., before landing on to a node, after landing on to a node, whenever a transition happens, etc.
 */
public class TreeEventProcessor {
    private EventRelay eventRelay = EventContext.getInstance().getEventRelay();

    private void sendActions(List<Action> actions, Map<String, Object> params) {
        for (Action action : actions) {
            eventRelay.sendEventMessage(new MotechEvent(action.getEventId(), params));
        }
    }

    /**
     * Emits an event before landing on to a particular node on the specified tree. Event subject is the eventId associated with the action.
     *
     * @param node   landing node
     * @param path   path of the node in decision tree
     * @param params extra information to be passed
     */
    public void sendActionsBefore(Node node, String path, Map<String, Object> params) {
        Assert.notNull(node, "Node must not be null");
        Assert.notNull(node, "Path must not be null");
        Assert.notNull(params, "Params must not be null");
        params.put("treePath", path);
        sendActions(node.getActionsBefore(), params);
    }

    /**
     * Emits an event after landing on to a particular node on the specified tree. Event subject is the eventId associated with the action.
     *
     * @param node   landing node
     * @param path   path of the node in decision tree
     * @param params extra information to be passed
     */
    public void sendActionsAfter(Node node, String path, Map<String, Object> params) {
        Assert.notNull(node, "Node must not be null");
        Assert.notNull(node, "Path must not be null");
        Assert.notNull(params, "Params must not be null");
        params.put("treePath", path);
        sendActions(node.getActionsAfter(), params);
    }

    /**
     * Emits an event before whenever a particular transition happens on the specified tree. Event subject is the eventId associated with the action.
     *
     * @param transition transition that is about to happen
     * @param params     extra information to be passed
     */
    public void sendTransitionActions(Transition transition, Map<String, Object> params) {
        Assert.notNull(transition, "Transition must not be null");
        Assert.notNull(params, "Params must not be null");
        params.put("transitionName", transition.getName());
        sendActions(transition.getActions(), params);
    }
}
