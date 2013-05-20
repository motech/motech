package org.motechproject.callflow.service;

import org.motechproject.callflow.domain.IvrEvent;
import org.springframework.web.servlet.ModelAndView;

/**
 * Server IVR provider's markup document based on a decision tree model object and the corresponding velocity template.
 */
public interface CallFlowServer {

    String CURRENT_NODE_PARAM = "CurrentNode";

    /**
     * Generates an ivr markup document based on a velocity template defined by an ivr provider module (kookoo, verboice etc).
     */
    ModelAndView getResponse(String flowSessionId, String phoneNumber, String provider, String tree, String transitionKey, String language);

    /**
     * Raises an IVR event with the call detail record of the call.
     *
     * @param callEvent event to be raised
     * @param flowSessionId FlowSession ID
     */
    void raiseCallEvent(IvrEvent callEvent, String flowSessionId);

    enum Error {
        TREE_OR_LANGUAGE_MISSING("Tree or language missing, please check IVR URL."),
        NULL_DESTINATION_NODE("No destination node."),
        INVALID_TRANSITION_KEY("Invalid transition key"),
        UNEXPECTED_EXCEPTION("Unexpected exception");
        private String message;

        Error(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return message;
        }
    }
}
