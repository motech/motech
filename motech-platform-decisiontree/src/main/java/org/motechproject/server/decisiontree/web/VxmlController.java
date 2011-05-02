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

import org.motechproject.decisiontree.model.Node;
import org.motechproject.server.decisiontree.service.DecisionTreeService;
import org.motechproject.server.decisiontree.service.TreeNodeLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Spring MVC controller implementation provides method to handle HTTP requests and generate
 * VXML documents based on a Decision Tree Node model object and the corresponding Velocity template
 *
 * @author Igor (iopushnyev@2paths.com)
 */
public class VxmlController extends MultiActionController {

    private Logger logger = LoggerFactory.getLogger((this.getClass()));

    public static final String TREE_NAME_PARAM = "treeName";
    public static final String TRANSITION_PATH_PARAM = "transitionPath";
    public static final String PATIENT_ID_PARAM = "patientId";

    public static final String MESSAGE_TEMPLATE_NAME = "node";
    public static final String TTS_MESSAGE_TEMPLATE_NAME = "ttsnode";
    public static final String ERROR_MESSAGE_TEMPLATE_NAME = "node_error";

    @Autowired
    DecisionTreeService decisionTreeService;

    /**
     * Handles Decision Tree Node HTTP requests and generates a VXML document based on a Velocity template.
     * The HTTP request should contain the Tree ID, Node ID, Patient ID and Selected Transition Key (optional) parameters
     *
     */
    public ModelAndView node(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Generating decision tree node VXML");

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        String patientId = request.getParameter(PATIENT_ID_PARAM);
        String treeName = request.getParameter(TREE_NAME_PARAM);
        String transitionPath = request.getParameter(TRANSITION_PATH_PARAM);

        logger.info(" Node HTTP  request parameters: " + PATIENT_ID_PARAM + ": " + patientId + ", "
                + TREE_NAME_PARAM + ": " + treeName + ", "
                + TRANSITION_PATH_PARAM + ": " + transitionPath);

        Node node = null;
        if (transitionPath == null) {  // get root node
            try {
                node = decisionTreeService.getNode(treeName, TreeNodeLocator.PATH_DELIMITER);
            } catch (Exception e) {
                logger.error("Can not get node by Tree Name: " + treeName + " and Patient ID: " + patientId, e);
            }
        } else { // get not root node
            try {
                node = decisionTreeService.getNode(treeName, transitionPath);
            } catch (Exception e) {
                 logger.error("Can not get node by Tree ID : " + treeName+ 
                         " and Transition Key: " + transitionPath, e);
            }
        }

        ModelAndView mav = new ModelAndView();

        if (node != null) {
            mav.setViewName(MESSAGE_TEMPLATE_NAME);
            mav.addObject("node", node);
            mav.addObject("patientId", patientId);
        } else {
            mav.setViewName(ERROR_MESSAGE_TEMPLATE_NAME);
        }

        return mav;
    }


}
