package org.motechproject.ivr.kookoo.controller;

import org.apache.log4j.Logger;
import org.motechproject.ivr.kookoo.IVRException;
import org.motechproject.ivr.kookoo.KookooCallServiceImpl;
import org.motechproject.ivr.kookoo.KookooRequest;
import org.motechproject.ivr.kookoo.action.Actions;
import org.motechproject.ivr.kookoo.action.event.BaseEventAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/ivr")
public class IVRController {

    Logger logger = Logger.getLogger(this.getClass());

    private Actions actions;

    private KookooCallServiceImpl kookooCallService;

    @Autowired
    public IVRController(Actions actions, KookooCallServiceImpl kookooCallService) {
        this.actions = actions;
        this.kookooCallService = kookooCallService;
    }

    @RequestMapping(value = "reply", method = RequestMethod.GET)
    @ResponseBody
    public String reply(@ModelAttribute KookooRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            BaseEventAction action = actions.findFor(ivrRequest.callEvent());
            String callId = getCallId(ivrRequest, request);
            final String xmlResponse = action.handle(callId, ivrRequest, request, response);
            setCallIdCookie(callId, request, response);
            logger.info(String.format(" XML returned: %s", response));
            return xmlResponse;
        } catch (Exception e) {
            logger.error("Failed to handled incoming request", e);
            throw new IVRException("Failed to handled incoming request", e);
        }
    }

    private String getCallId(KookooRequest ivrRequest, HttpServletRequest request) {
        if(getCallIdFromCookie(request) != null){
            return getCallIdFromCookie(request);
        }else if(ivrRequest.getParameter("CallId") != null){
            return ivrRequest.getParameter("CallId");
        }
        return kookooCallService.generateCallId(ivrRequest);
    }

    private String getCallIdFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies){
            if("CallId".equals(cookie.getName())){
                return cookie.getValue();
            }
        }
        return null;
    }

    private void setCallIdCookie(String callId, HttpServletRequest request, HttpServletResponse response) {
        if(getCallIdFromCookie(request) == null){
            response.addCookie(new Cookie("CallId",callId));
        }
    }
}
