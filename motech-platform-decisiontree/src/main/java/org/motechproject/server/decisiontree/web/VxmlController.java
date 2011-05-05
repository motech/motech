/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2011 Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, nor its respective contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA AND ITS CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION USA OR ITS CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package org.motechproject.server.decisiontree.web;

import org.apache.commons.codec.binary.Base64;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.server.decisiontree.service.DecisionTreeService;
import org.motechproject.server.decisiontree.service.TreeEventProcessor;
import org.motechproject.server.decisiontree.service.TreeNodeLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Spring MVC controller implementation provides method to handle HTTP requests and generate
 * VXML documents based on a Decision Tree Node model object and the corresponding Velocity template
 *
 * @author Igor (iopushnyev@2paths.com)
 */
public class VxmlController extends MultiActionController {

    private Logger logger = LoggerFactory.getLogger((this.getClass()));

    public static final String TREE_NAME_PARAM = "tNm";
    public static final String TRANSITION_KEY_PARAM = "trK";
    public static final String TRANSITION_PATH_PARAM = "trP";
    public static final String PATIENT_ID_PARAM = "pId";
    public static final String LANGUAGE_PARAM = "ln";

    public static final String MESSAGE_TEMPLATE_NAME = "node";
    public static final String ERROR_MESSAGE_TEMPLATE_NAME = "node_error";

    @Autowired
    DecisionTreeService decisionTreeService;

    @Autowired
    TreeEventProcessor treeEventProcessor;

    enum Errors {
        NULL_PATIENTID_LANGUAGE_OR_TREENAME_PARAM,
        NULL_TRANSITION_PATH_PARAM,
        NULL_DESTINATION_NODE,
        INVALID_TRANSITION_KEY,
        INVALID_TRANSITION_KEY_TYPE,
        GET_NODE_ERROR
    }

    /**
     * Handles Decision Tree Node HTTP requests and generates a VXML document based on a Velocity template.
     * The HTTP request should contain the Tree ID, Node ID, Patient ID and Selected Transition Key (optional) parameters
     *
     */
    public ModelAndView node(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Generating decision tree node VXML");

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        Node node = null;
        String transitionPath = null;

        String patientId = request.getParameter(PATIENT_ID_PARAM);
        String language = request.getParameter(LANGUAGE_PARAM);
        String treeName = request.getParameter(TREE_NAME_PARAM);
        String encodedParentTransitionPath = request.getParameter(TRANSITION_PATH_PARAM);
        String transitionKey = request.getParameter(TRANSITION_KEY_PARAM);



        logger.info(" Node HTTP  request parameters: "
                + PATIENT_ID_PARAM + ": " + patientId + ", "
                + LANGUAGE_PARAM + ": " + language + ", "
                + TREE_NAME_PARAM + ": " + treeName + ", "
                + TRANSITION_PATH_PARAM + ": " + encodedParentTransitionPath + ", "
                + TRANSITION_KEY_PARAM + ": " + transitionKey);




        if (patientId == null || language == null || treeName == null) {

            logger.error("Invalid HTTP request - the following parameters: "
                    + PATIENT_ID_PARAM + ", " + LANGUAGE_PARAM + " and " + TREE_NAME_PARAM + " are mandatory");
            return getErrorModelAndView(Errors.NULL_PATIENTID_LANGUAGE_OR_TREENAME_PARAM);
        }


        if (transitionKey == null) {  // get root node
            try {
                String rootTransitionPath =  TreeNodeLocator.PATH_DELIMITER;
                node = decisionTreeService.getNode(treeName, rootTransitionPath);
                transitionPath = rootTransitionPath;
            } catch (Exception e) {
                logger.error("Can not get node by Tree Name: " + treeName + " transition path: " + patientId, e);
            }
        } else { // get not root node
            String parentTransitionPath = null;
            try {
                if (encodedParentTransitionPath == null) {

                    logger.error("Invalid HTTP request - the  " + TRANSITION_PATH_PARAM + " parameter is mandatory");
                    return getErrorModelAndView(Errors.NULL_TRANSITION_PATH_PARAM);
                }

                parentTransitionPath = new String(Base64.decodeBase64(encodedParentTransitionPath));
                Node parentNode = decisionTreeService.getNode(treeName, parentTransitionPath);

                Transition transition = parentNode.getTransitions().get(transitionKey);

                if (transition == null) {
                    logger.error("Invalid Transition Key. There is no transition with key: "+transitionKey+" in the Node: " + parentNode);
                    return getErrorModelAndView(Errors.INVALID_TRANSITION_KEY);
                }

                node = transition.getDestinationNode();

                if (node == null) {
                    logger.error("Transition: " + transition + " invalid - no Destination Node");
                    return getErrorModelAndView(Errors.NULL_DESTINATION_NODE);
                }

                transitionPath = parentTransitionPath +
                        (TreeNodeLocator.PATH_DELIMITER.equals(parentTransitionPath) ? "": TreeNodeLocator.PATH_DELIMITER)
                        +  transitionKey;


                treeEventProcessor.sendActionsAfter(parentNode, parentTransitionPath, patientId);

                treeEventProcessor.sendTransitionActions(transition, patientId);


            } catch (Exception e) {
                logger.error("Can not get node by Tree ID : " + treeName +
                        " and Transition Path: " + parentTransitionPath, e);
            }
        }



        if (node != null) {

            //validate node
            for (Map.Entry<String, Transition> transitionEntry: node.getTransitions().entrySet()) {

                try {
                    Integer.parseInt(transitionEntry.getKey());
                } catch (NumberFormatException e) {
                    logger.error("Invalid node: " + node
                            + "\n In order  to be used in VXML transition keys should be an Integer");
                    return getErrorModelAndView(Errors.INVALID_TRANSITION_KEY_TYPE);
                }

                Transition transition = transitionEntry.getValue();
                if (transition .getDestinationNode() == null) {
                     logger.error("Invalid node: " + node + "\n Null Destination Node in the Transition: " +   transition);
                    return getErrorModelAndView(Errors.NULL_DESTINATION_NODE);
                }
            }

            treeEventProcessor.sendActionsBefore(node, transitionPath, patientId);


            ModelAndView mav = new ModelAndView();
            mav.setViewName(MESSAGE_TEMPLATE_NAME);
            mav.addObject("contentPath", request.getContextPath());
            mav.addObject("node", node);
            mav.addObject("patientId",  patientId);
            mav.addObject("language", language);
            mav.addObject("transitionPath", Base64.encodeBase64URLSafeString(transitionPath.getBytes()));

            return mav;
        } else {
            return getErrorModelAndView(Errors.GET_NODE_ERROR);
        }


    }

    private ModelAndView getErrorModelAndView (Errors errorCode) {

        ModelAndView mav = new ModelAndView();
        mav.setViewName(ERROR_MESSAGE_TEMPLATE_NAME);
        mav.addObject("errorCode", errorCode);
        return mav;
    }


}
