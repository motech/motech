package org.motechproject.server.decisiontree.web;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.decisiontree.FlowSession;
import org.motechproject.decisiontree.model.*;
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

/**
 * Spring MVC controller implementation provides method to handle HTTP requests and generate
 * Markup document based on a Decision Tree Node model object and the corresponding Velocity template.
 */
@Controller
@RequestMapping("/decisiontree")
public class DecisionTreeController extends MultiActionController {

    public static final String TEMPLATE_BASE_PATH = "/vm/";
    public static final String FLOW_SESSION_ID_PARAM = "flowSessionId";
    private static final String TREE_ROOT_PATH = TreeNodeLocator.PATH_DELIMITER;
    private Logger logger = LoggerFactory.getLogger((this.getClass()));
    public static final String CURRENT_NODE = "CurrentNode";

    public static final String TREE_NAME_PARAM = "tree";
    public static final String TRANSITION_KEY_PARAM = "trK";
    public static final String TRANSITION_PATH_PARAM = "trP";
    public static final String LANGUAGE_PARAM = "ln";
    public static final String TYPE_PARAM = "type";

    public static final String NODE_TEMPLATE_NAME = TEMPLATE_BASE_PATH + "node";
    public static final String ERROR_MESSAGE_TEMPLATE_NAME = TEMPLATE_BASE_PATH + "node-error";
    public static final String EMPTY_INPUT_TEMPLATE_NAME = TEMPLATE_BASE_PATH + "node-null";
    public static final String EXIT_TEMPLATE_NAME = TEMPLATE_BASE_PATH + "exit";

    public static final String TREE_NAME_SEPARATOR = ",";
    public static final int MAX_INPUT_DIGITS = 50;

    @Autowired
    private DecisionTreeService decisionTreeService;

    @Autowired
    private TreeEventProcessor treeEventProcessor;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private FlowSessionService flowSessionService;

    enum Errors {
        NULL_PATIENTID_LANGUAGE_OR_TREENAME_PARAM,
        NULL_TRANSITION_PATH_PARAM,
        NULL_DESTINATION_NODE,
        INVALID_TRANSITION_KEY,
        INVALID_TRANSITION_KEY_TYPE,
        GET_NODE_ERROR
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

    /**
     * Handles Decision Tree Node HTTP requests and generates a Markup document based on a Velocity template and type (vxml or verboice etc).
     * The HTTP request should contain the Tree ID, Node ID, Patient ID and Selected Transition Key (optional) parameters
     */
    @RequestMapping("/node")
    public ModelAndView node(HttpServletRequest request, HttpServletResponse response) {
        logger.info(request.getParameterMap().toString());
        logger.info("Generating decision tree node xml");

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> params = convertParams(request.getParameterMap());
        String language = request.getParameter(LANGUAGE_PARAM);
        String treeNameString = request.getParameter(TREE_NAME_PARAM);
        String encodedTransitionPath = getParentTransitionPath(request);
        String transitionKey = request.getParameter(TRANSITION_KEY_PARAM);

        logger.info(" Node HTTP  request parameters: "
                + LANGUAGE_PARAM + ": " + language + ", "
                + TREE_NAME_PARAM + ": " + treeNameString + ", "
                + TRANSITION_PATH_PARAM + ": " + encodedTransitionPath + ", "
                + TRANSITION_KEY_PARAM + ": " + transitionKey);
        try {
            if (StringUtils.isBlank(language) || StringUtils.isBlank(treeNameString)) {
                logger.error("Invalid HTTP request - the following parameters: "
                        + ", " + LANGUAGE_PARAM + " and " + TREE_NAME_PARAM + " are mandatory");
                return getErrorModelAndView(Errors.NULL_PATIENTID_LANGUAGE_OR_TREENAME_PARAM, language);
            }

            return getModelViewForNextNode(request, getParentTransitionPath(request), params);
        } catch (DecisionTreeException exception) {
            logger.error(exception.getMessage());
            return getErrorModelAndView(exception.subject, language);
        } catch (Exception e) {
            logger.error(format("Can not get node by Tree ID: %s and transition path %s\n%s", treeNameString, encodedTransitionPath, e));
        }
        return getErrorModelAndView(Errors.GET_NODE_ERROR, language);
    }

    private ModelAndView getModelViewForNextNode(HttpServletRequest request, String parentTransitionPath, Map<String, Object> params) {
        String treeNameString = request.getParameter(TREE_NAME_PARAM);
        String transitionKey = request.getParameter(TRANSITION_KEY_PARAM);
        String[] treeNames = treeNameString.split(TREE_NAME_SEPARATOR);
        String currentTree = treeNames[0];

        // put only one tree name in params
        params.put(TREE_NAME_PARAM, currentTree);

        FlowSession session = flowSessionService.getSession(request);
        Node node = getCurrentNode(session);
        try {
            if (node == null) {
                node = decisionTreeService.getRootNode(currentTree, session);
            }
            if (transitionKey == null) { //node = decisionTreeService.getNode(currentTree, TreeNodeLocator.PATH_DELIMITER, session);
                return constructModelViewForNode(request, node, parentTransitionPath, treeNames, params, session);
            } else {
                //Node parentNode = decisionTreeService.getNode(currentTree, parentTransitionPath, session);

                ITransition transition = sendTreeEventActions(params, transitionKey, parentTransitionPath, node);
                autowire(transition);

                node = transition.getDestinationNode(transitionKey, session);
                autowire(node);


                final boolean emptyNode = node == null || (node.getPrompts().isEmpty() && node.getActionsAfter().isEmpty()
                        && node.getActionsBefore().isEmpty() && node.getTransitions().isEmpty());
                if (emptyNode) {
                    if (treeNames.length > 1) {
                        //reduce the current tree and redirect to the next tree
                        treeNames = (String[]) ArrayUtils.remove(treeNames, 0);
                        return new ModelAndView(String.format("redirect:/decisiontree/node?%s=%s&%s=%s", TREE_NAME_PARAM, LANGUAGE_PARAM, StringUtils.join(treeNames, TREE_NAME_SEPARATOR), request.getParameter(LANGUAGE_PARAM)));
                    } else {
                        return new ModelAndView(EXIT_TEMPLATE_NAME);
                    }
                } else {
                    for (INodeOperation operation : node.getOperations()) {
                        operation.perform(transitionKey, session);
                    }
                    String modifiedTransitionPath = parentTransitionPath +
                            (TREE_ROOT_PATH.equals(parentTransitionPath) ? "" : TreeNodeLocator.PATH_DELIMITER)
                            + transitionKey;
                    session.setCurrentNode(node);
                    return constructModelViewForNode(request, node, modifiedTransitionPath, treeNames, params, session);
                    //return constructModelViewForNode(request, node, modifiedTransitionPath, language, treeNameString, type, treeNames, params, session);
                }
            }
        } finally {
            flowSessionService.updateSession(session);
        }
    }

    private void autowire(Object transition) {
        applicationContext.getAutowireCapableBeanFactory().autowireBean(transition);
    }

    private Node getCurrentNode(FlowSession session) {
        Node node = session.getCurrentNode();
        return node;
    }

    private ModelAndView constructModelViewForNode(HttpServletRequest request, Node node, String transitionPath, String[] treeNames, Map<String, Object> params, FlowSession session) {
        validateNode(node);
        treeEventProcessor.sendActionsBefore(node, transitionPath, params);

        ModelAndView mav = new ModelAndView();
        if (node.getTransitions().size() > 0) {
            mav.addObject("treeName", request.getParameter(TREE_NAME_PARAM));
        } else { // leaf
            //reduce the current tree and redirect to the next tree
            mav.addObject("treeName", StringUtils.join(ArrayUtils.remove(treeNames, 0), TREE_NAME_SEPARATOR));
        }
        mav.setViewName(templateNameFor(request.getParameter(TYPE_PARAM), NODE_TEMPLATE_NAME));
        mav.addObject("contextPath", request.getContextPath());
        mav.addObject("servletPath", request.getServletPath());
        mav.addObject("node", node);
        mav.addObject("language", session.get(LANGUAGE_PARAM));
        mav.addObject("type", request.getParameter(TYPE_PARAM));
        mav.addObject("transitionPath", Base64.encodeBase64URLSafeString(transitionPath.getBytes()));
        mav.addObject("escape", new StringEscapeUtils());
        mav.addObject("maxDigits", maxDigits(node.getTransitions()));
        mav.addObject("host", request.getHeader("Host"));
        mav.addObject("scheme", request.getScheme());
        return mav;
    }

    private void validateNode(Node node) {
        for (Map.Entry<String, ITransition> transitionEntry : node.getTransitions().entrySet()) {

            final String key = transitionEntry.getKey();
            if(noInput(key)){
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
                throw new DecisionTreeException(Errors.INVALID_TRANSITION_KEY_TYPE, "Invalid node: " + node
                        + "\n In order  to be used in VXML transition keys should be an Integer");
            }

            ITransition transition = transitionEntry.getValue();
            if (transition instanceof Transition && ((Transition) transition).getDestinationNode() == null) {
                throw new DecisionTreeException(Errors.NULL_DESTINATION_NODE, "Invalid node: " + node + "\n Null Destination Node in the Transition: " + transition);
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

    private ITransition sendTreeEventActions(Map<String, Object> params, String transitionKey, String parentTransitionPath, Node parentNode) {
        ITransition transition = getTransitionForUserInput(transitionKey, parentNode);

        treeEventProcessor.sendActionsAfter(parentNode, parentTransitionPath, params);

        if (transition instanceof Transition) {
            treeEventProcessor.sendTransitionActions((Transition) transition, params);
        }
        return transition;
    }

    private ITransition getTransitionForUserInput(String userInput, Node parentNode) {
        ITransition transition = getPreConfiguredTransition(parentNode, userInput);
        if (transition == null) {
            transition = parentNode.getTransitions().get(TreeNodeLocator.ANY_KEY);
        }

        if (transition == null) {
            throw new DecisionTreeException(Errors.INVALID_TRANSITION_KEY,
                    "Invalid Transition Key. There is no transition with key: " + userInput + " in the Node: " + parentNode);
        }
        return transition;
    }

    private ITransition getPreConfiguredTransition(Node parentNode, String userInput) {
        return parentNode.getTransitions().get(userInput);
    }

    private String getParentTransitionPath(HttpServletRequest request) {
        final String encodedTransitionPath = request.getParameter(TRANSITION_PATH_PARAM);
        if (encodedTransitionPath == null) {
            return TREE_ROOT_PATH;
        }
        return new String(Base64.decodeBase64(encodedTransitionPath));
    }

    private String maxDigits(Map<String, ITransition> transitions) {
        int maxDigits = 1;
        for (String key : transitions.keySet()) {
            if (anyInput(key)) {
                return "" + MAX_INPUT_DIGITS;
            }
            if (maxDigits < key.length()) {
                maxDigits = key.length();
            }
        }
        return Integer.toString(maxDigits);
    }

    private String templateNameFor(String type, String templateName) {
        return templateName + "-" + type;
    }

    private ModelAndView getErrorModelAndView(Errors errorCode, String language) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName(ERROR_MESSAGE_TEMPLATE_NAME);
        mav.addObject("errorCode", errorCode);
        mav.addObject("language", language);
        return mav;
    }

    private class DecisionTreeException extends RuntimeException {
        private Errors subject;

        public DecisionTreeException(Errors subject, String message) {
            super(message);
            this.subject = subject;
        }
    }
}
