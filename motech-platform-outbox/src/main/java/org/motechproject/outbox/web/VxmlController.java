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
package org.motechproject.outbox.web;

import org.motechproject.outbox.model.OutboundVoiceMessage;
import org.motechproject.server.service.VoiceOutboxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Spring MVC controller implementation provides method to handle HTTP requests and generate
 * Appointment Reminder related VXML documents
 *
 *
 * @author Igor (iopushnyev@2paths.com)
 */
public class VxmlController extends MultiActionController {

    private Logger logger = LoggerFactory.getLogger((this.getClass()));

    @Autowired
    VoiceOutboxService voiceOutboxService;

    /**
     * Handles Appointment Reminder HTTP requests and generates a VXML document based on a Velocity template.
     * The HTTP request may contain an optional 'mId' parameter with value of ID of the message for which
     *  VXML document will be generated. If the "mId" parameter is not passed the next pending voice message
     *  will be obtained from the outbox and a VXML document will be generated for that message
     *
     *
	 * URL to request appointment reminder VoiceXML:
	 * http://<host>:<port>/<motech-platform-server>/module/outbox/vxml/msg?mId=<messageId>
	 */
	public ModelAndView msg(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Generate appointment reminder VXML");

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        ModelAndView mav = new ModelAndView();

        //Interim implementation. Party ID will be obtained from the Authentication context
        String partyId = "1";


        String messageId = request.getParameter("mId");

        logger.debug("Message ID: " + messageId );

        if (messageId  != null) {
            logger.info("Generating VXML for the voice message ID: " + messageId);

            mav.setViewName("msg");
		    return mav;
        }

        OutboundVoiceMessage voiceMessage = null;

        try {
            voiceOutboxService.getNextPendingMessage(partyId);
        } catch (Exception e) {
            logger.error("Can not obtain next message from the outbox of the party ID: " + partyId +
                    " " + e.getMessage(), e);
            logger.warn("Generating a VXML with the error message...");
            mav.setViewName("msg_error");
            return mav;
        }


        if (voiceMessage == null) {

            logger.info("There are no more messages in the outbox of the party ID: " + partyId);
            mav.setViewName("nomsg");
             return mav;
        }

         return mav;

	}
}
