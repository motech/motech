package org.motechproject.server.decisiontree.web;

import org.apache.commons.lang.StringEscapeUtils;
import org.motechproject.decisiontree.FlowSession;
import org.motechproject.decisiontree.model.DialStatus;
import org.motechproject.decisiontree.model.INodeOperation;
import org.motechproject.decisiontree.model.ITransition;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.decisiontree.service.FlowSessionService;
import org.motechproject.server.decisiontree.TreeNodeLocator;
import org.motechproject.server.decisiontree.service.DecisionTreeService;
import org.motechproject.server.decisiontree.service.TreeEventProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * Spring MVC controller implementation provides method to handle HTTP requests and generate
 * Markup document based on a Decision Tree Node model object and the corresponding Velocity template.
 */
@Controller
@RequestMapping("/decisiontree")
public class DecisionTreeController extends MultiActionController {

    private Logger logger = LoggerFactory.getLogger((this.getClass()));

    public static final String TEMPLATE_BASE_PATH = "/vm/";
    public static final String FLOW_SESSION_ID_PARAM = "flowSessionId";
    public static final String CURRENT_NODE = "CurrentNode";

    public static final String TREE_NAME_PARAM = "tree";
    public static final String TRANSITION_KEY_PARAM = "trK";
    public static final String LANGUAGE_PARAM = "ln";
    public static final String TYPE_PARAM = "type";

    public static final String NODE_TEMPLATE_NAME = TEMPLATE_BASE_PATH + "node";
    public static final String ERROR_MESSAGE_TEMPLATE_NAME = TEMPLATE_BASE_PATH + "error";
    public static final String EXIT_TEMPLATE_NAME = TEMPLATE_BASE_PATH + "exit";

    public static final Integer MAX_INPUT_DIGITS = 50;
    public static final Integer MAX_INPUT_TIMEOUT = 5000;

    @Autowired
    private DecisionTreeService decisionTreeService;

    @Autowired
    private TreeEventProcessor treeEventProcessor;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private FlowSessionService flowSessionService;

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

    /**
     * Handles Decision Tree Node HTTP requests and generates a Markup document based on a Velocity template and type (vxml or verboice etc).
     * The HTTP request should contain the Tree ID, Node ID, Patient ID and Selected Transition Key (optional) parameters
     */
    @RequestMapping("/node")
    public ModelAndView node(HttpServletRequest request, HttpServletResponse response) {
        logger.debug(request.getParameterMap().toString());

        String language = request.getParameter(LANGUAGE_PARAM);
        String treeName = request.getParameter(TREE_NAME_PARAM);
        String transitionKey = request.getParameter(TRANSITION_KEY_PARAM);
        String type = request.getParameter(TYPE_PARAM);

        if (logger.isInfoEnabled()) {
            logger.info(format("IVR Transition | tree:%s dtmf:%s ln:%s", treeName, transitionKey, language));
        }

        ModelAndView view;
        try {
            if (isBlank(language) || isBlank(treeName)) {
                logger.error(format("No tree or language specified"));
                view = getErrorModelAndView(Error.TREE_OR_LANGUAGE_MISSING, language, type);
            } else {
               view = getModelViewForNextNode(request, convertParams(request.getParameterMap()));
            }
        } catch (DecisionTreeException e) {
            logger.error(e.getMessage(), e);
            view = getErrorModelAndView(e.subject, language, type);
        } catch (Exception e) {
            logger.error(format("Unexpected exception %s", e.getMessage()),e);
            view = getErrorModelAndView(Error.UNEXPECTED_EXCEPTION, language, type);
        }
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        return view;
    }

    private ModelAndView getModelViewForNextNode(HttpServletRequest request, Map<String, Object> params) {
        String treeName = request.getParameter(TREE_NAME_PARAM);
        String transitionKey = request.getParameter(TRANSITION_KEY_PARAM);

        FlowSession session = flowSessionService.getSession(request);
        Node node = getCurrentNode(session);
        try {
            if (node == null) {
                node = decisionTreeService.getRootNode(treeName, session);
                autowire(node);
                executeOperations(transitionKey, session, node);
            }
            if (transitionKey == null) {
                return constructModelViewForNode(request, node, treeName, params, session);
            } else {
                ITransition nextTransition = getTransitionForUserInput(transitionKey, node);
                autowire(nextTransition);

                treeEventProcessor.sendActionsAfter(node, params);
                if (nextTransition instanceof Transition) {
                    treeEventProcessor.sendTransitionActions((Transition) nextTransition, params);
                }

                node = nextTransition.getDestinationNode(transitionKey, session);
                autowire(node);

                if (isEmptyNode(node)) {
                    return new ModelAndView(templateNameFor(request.getParameter(TYPE_PARAM), EXIT_TEMPLATE_NAME));
                } else {
                    executeOperations(transitionKey, session, node);
                    return constructModelViewForNode(request, node, treeName, params, session);
                }
            }
        } finally {
            flowSessionService.updateSession(session);
        }
    }

    private ModelAndView constructModelViewForNode(HttpServletRequest request, Node node, String treeName, Map<String, Object> params, FlowSession session) {
        validateNode(node);
        treeEventProcessor.sendActionsBefore(node, params);

        ModelAndView mav = new ModelAndView();
        mav.setViewName(templateNameFor(request.getParameter(TYPE_PARAM), NODE_TEMPLATE_NAME));
        mav.addObject("treeName", treeName);
        mav.addObject("contextPath", request.getContextPath());
        mav.addObject("servletPath", request.getServletPath());
        mav.addObject("node", node);
        mav.addObject("language", session.get(LANGUAGE_PARAM));
        mav.addObject("type", request.getParameter(TYPE_PARAM));
        mav.addObject("escape", new StringEscapeUtils());
        mav.addObject("maxDigits", maxDigits(node));
        mav.addObject("maxTimeout", maxTimeout(node));
        mav.addObject("host", request.getHeader("Host"));
        mav.addObject("scheme", request.getScheme());
        session.setCurrentNode(node);
        return mav;
    }

    private boolean isEmptyNode(Node node) {
        return node == null || (node.getPrompts().isEmpty() && node.getActionsAfter().isEmpty() && node.getActionsBefore().isEmpty() && node.getTransitions().isEmpty());
    }

    private void executeOperations(String transitionKey, FlowSession session, Node node) {
        for (INodeOperation operation : node.getOperations()) {
            operation.perform(transitionKey, session);
        }
    }

    private void autowire(Object transition) {
        applicationContext.getAutowireCapableBeanFactory().autowireBean(transition);
    }

    private Node getCurrentNode(FlowSession session) {
        Node node = session.getCurrentNode();
        return node;
    }

    private void validateNode(Node node) {
        for (Map.Entry<String, ITransition> transitionEntry : node.getTransitions().entrySet()) {
            final String key = transitionEntry.getKey();
            if (noInput(key)) {
                return;
            }
            if (anyInput(key)) {
                return;
            }
            if (dtmfKey(key)) {
                return;
            }
            if (DialStatus.isValid(key)) {
                return;
            }
            try {
                Integer.parseInt(key);
            } catch (NumberFormatException e) {
                throw new DecisionTreeException(Error.INVALID_TRANSITION_KEY, format("Invalid transition key [%s] for node [%s]", key, node));
            }
            ITransition transition = transitionEntry.getValue();
            if (transition instanceof Transition && ((Transition) transition).getDestinationNode() == null) {
                throw new DecisionTreeException(Error.NULL_DESTINATION_NODE, format("Missing destination node in the transition for key [%s] on node [%s]: ", key, node));
            }
        }
    }

    private boolean noInput(String key) {
        return TreeNodeLocator.NO_INPUT.equals(key);
    }

    private boolean dtmfKey(String key) {
        return "*#".contains(key);
    }

    private boolean anyInput(String key) {
        return TreeNodeLocator.ANY_KEY.equals(key);
    }

    private ITransition getTransitionForUserInput(String userInput, Node parentNode) {
        ITransition transition = getPreConfiguredTransition(parentNode, userInput);
        if (transition == null) {
            transition = parentNode.getTransitions().get(TreeNodeLocator.ANY_KEY);
        }

        if (transition == null) {
            throw new DecisionTreeException(Error.INVALID_TRANSITION_KEY,
                    "Invalid Transition Key. There is no transition with key: " + userInput + " in the Node: " + parentNode);
        }
        return transition;
    }

    private ITransition getPreConfiguredTransition(Node parentNode, String userInput) {
        return parentNode.getTransitions().get(userInput);
    }

    private Integer maxDigits(Node node) {
        Map<String, ITransition> transitions = node.getTransitions();
        int maxDigits = 1;
        for (String key : transitions.keySet()) {
            if (anyInput(key)) {
                return (node.getMaxTransitionInputDigit() == null) ? MAX_INPUT_DIGITS : node.getMaxTransitionInputDigit();
            }
            if (maxDigits < key.length()) {
                maxDigits = key.length();
            }
        }
        return maxDigits;
    }

    private Integer maxTimeout(Node node) {
        return node.getMaxTransitionTimeout() == null ? MAX_INPUT_TIMEOUT : node.getMaxTransitionTimeout();
    }

    private String templateNameFor(String type, String templateName) {
        return templateName + "-" + type;
    }

    private ModelAndView getErrorModelAndView(Error error, String language, String type) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName(templateNameFor(type, ERROR_MESSAGE_TEMPLATE_NAME));
        mav.addObject("message", error.toString());
        mav.addObject("language", language);
        return mav;
    }

    private class DecisionTreeException extends RuntimeException {

        private Error subject;

        public DecisionTreeException(Error subject, String description) {
            super(description);
            this.subject = subject;
        }

    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> convertParams(@SuppressWarnings("rawtypes") Map requestParams) {
        Map<String, Object> params = new HashMap<String, Object>();
        Assert.notNull(requestParams);
        for (Map.Entry<String, String[]> e : (Set<Map.Entry<String, String[]>>) requestParams.entrySet()) {
            if (e.getValue().length == 1) {
                params.put(e.getKey(), e.getValue()[0]);
            } else {
                params.put(e.getKey(), e.getValue());
            }
        }
        return params;
    }
}
