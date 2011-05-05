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
package org.motechproject.server.outbox.web;

import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.server.outbox.service.VoiceOutboxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Spring MVC controller implementation provides method to handle HTTP requests and generate
 * VXML documents based on stored in outbox objects and the corresponding Velocity template
 *
 * @author Igor (iopushnyev@2paths.com)
 */
public class VxmlController extends MultiActionController {

    private Logger logger = LoggerFactory.getLogger((this.getClass()));

    public static final String NO_MESSAGE_TEMPLATE_NAME = "nonsg";
    public static final String ERROR_MESSAGE_TEMPLATE_NAME = "msg_error";
    public static final String MESSAGE_MENU_TEMPLATE_NAME = "msgMenu";

    @Autowired
    VoiceOutboxService voiceOutboxService;

    /**
     * Handles Appointment Reminder HTTP requests and generates a VXML document based on a Velocity template.
     * The HTTP request may contain an optional 'mId' parameter with value of ID of the message for which
     * VXML document will be generated. If the "mId" parameter is not passed the next pending voice message
     * will be obtained from the outbox and a VXML document will be generated for that message
     * <p/>
     * <p/>
     * URL to request appointment reminder VoiceXML:
     * http://<host>:<port>/<motech-platform-server>/module/outbox/vxml/outboxMessage?mId=<messageId>
     */
    public ModelAndView outboxMessage(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Generate appointment reminder VXML");

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        ModelAndView mav = new ModelAndView();

        //Interim implementation. Party ID will be obtained from the Authentication context
        String partyId = "1";


        String messageId = request.getParameter("mId");

        logger.debug("Message ID: " + messageId);

        OutboundVoiceMessage voiceMessage = null;

        if (messageId != null) {
            logger.info("Generating VXML for the voice message ID: " + messageId);

            try {
                voiceMessage = voiceOutboxService.getMessageById(messageId);
            } catch (Exception e) {
                logger.error("Can not get message by ID: " + messageId +
                        " " + e.getMessage(), e);
                logger.warn("Generating a VXML with the error message...");
                mav.setViewName(ERROR_MESSAGE_TEMPLATE_NAME);
                return mav;
            }
        } else {
            logger.info("Generating VXML for the next voice message in outbox... ");
            try {
                voiceMessage = voiceOutboxService.getNextPendingMessage(partyId);
            } catch (Exception e) {
                logger.error("Can not obtain next message from the outbox of the party ID: " + partyId +
                        " " + e.getMessage(), e);
                logger.warn("Generating a VXML with the error message...");
                mav.setViewName(ERROR_MESSAGE_TEMPLATE_NAME);
                return mav;
            }
        }

        if (voiceMessage == null) {

            logger.info("There are no more messages in the outbox of the party ID: " + partyId);
            mav.setViewName(NO_MESSAGE_TEMPLATE_NAME);
            return mav;
        }

        mav.setViewName(voiceMessage.getVoiceMessageType().getVoiceMessageTypeName());
        return mav;

    }

    public ModelAndView messageMenu(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Generating the message menu VXML...");

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        String messageId = request.getParameter("mId");

        logger.info("Message ID: " + messageId);

        //TODO - set message status Played

        request.getContextPath();

        ModelAndView mav = new ModelAndView();
        mav.setViewName(MESSAGE_MENU_TEMPLATE_NAME);
        mav.addObject("messageId", messageId);
        return mav;

    }
}
