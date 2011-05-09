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
package org.motechproject.server.tama.web;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * Spring MVC controller responsible for handling main menu selection
 * @author yyonkov
 *
 * {@code http://localhost:8081/motech-platform-server/module/tama/menu/select?pId=10 }
 */
public class MenuController extends MultiActionController {
    public static final String PATIENT_ID_PARAM = "pId";
    public static final String MENU_TEMPLATE_NAME = "menu";
    public static final String REDIRECT_LOGIN = "redirect:/tama/login";
    private Logger logger = LoggerFactory.getLogger((this.getClass()));    
    private Map<String, String> successViews;

	public Map<String, String> getSuccessViews() {
		return successViews!=null?successViews:Collections.<String, String>emptyMap();
	}

	public void setSuccessViews(Map<String, String> successViews) {
		this.successViews = successViews;
	}

	public ModelAndView select(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        
        ModelAndView mav = new ModelAndView();
        
        String patientId = request.getParameter(PATIENT_ID_PARAM);        
        if(patientId==null) {
        	logger.error("Invalid HTTP request - "+PATIENT_ID_PARAM+" is mandatory");
        	mav.setViewName(REDIRECT_LOGIN);
        	return mav;
        }
        
        logger.info("Generating main menu for pId: "+ patientId);
        
        mav.setViewName(MENU_TEMPLATE_NAME);
        mav.addObject("contentPath", request.getContextPath());
        mav.addObject("patientId",  patientId);		
		mav.addObject("views", getSuccessViews().entrySet());
        mav.addObject("escape", new StringEscapeUtils());

		return mav;
	}
}
