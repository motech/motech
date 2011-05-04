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

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.server.tama.service.AuthenticationService;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class LoginControllerTest {


    @InjectMocks
    LoginController loginController = new LoginController();

    @Mock
    private AuthenticationService securityService;

    @Mock
    private HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        loginController.setFormView("login");
        loginController.setSuccessView("mainMenu");
     }

    @Test
    public void testHandleRequest() throws Exception {
    	String phoneNumber = "1234";
    	String patientId = UUID.randomUUID().toString();
        ModelAndView modelAndView = loginController.handleRequest(request, response);
        Assert.assertEquals("login", modelAndView.getViewName());
        
        Mockito.when(request.getParameter("callerId")).thenReturn(phoneNumber);
        modelAndView = loginController.handleRequest(request, response);
        Assert.assertEquals("login", modelAndView.getViewName());

        Mockito.when(securityService.getPatientIdByPhoneNumber(phoneNumber)).thenReturn(patientId);
        Mockito.when(securityService.verifyPasscode(patientId, "1111")).thenReturn(true);
        Mockito.when(request.getParameter("callerId")).thenReturn("1234");
        Mockito.when(request.getParameter("passcode")).thenReturn("1111");

        modelAndView = loginController.handleRequest(request, response);
        Assert.assertEquals("mainMenu?pId="+patientId, modelAndView.getViewName());

        Mockito.when(request.getParameter("passcode")).thenReturn("wrong");
        modelAndView = loginController.handleRequest(request, response);
        Assert.assertEquals("login", modelAndView.getViewName());
    }
}
