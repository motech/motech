package org.motechproject.ivr.controller;

import org.apache.log4j.Logger;
import org.motechproject.ivr.IVRException;
import org.motechproject.ivr.action.Actions;
import org.motechproject.ivr.action.event.BaseEventAction;
import org.motechproject.ivr.kookoo.KookooRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/ivr")
public class IVRController {
    Logger logger = Logger.getLogger(this.getClass());
    private Actions actions;

    @Autowired
    public IVRController(Actions actions) {
        this.actions = actions;
    }

    @RequestMapping(value = "reply", method = RequestMethod.GET)
    @ResponseBody
    public String reply(@ModelAttribute KookooRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            BaseEventAction action = actions.findFor(ivrRequest.callEvent());
            final String xmlResponse = action.handleInternal(ivrRequest, request, response);
            logger.info(String.format(" XML returned: %s", response));
            return xmlResponse;
        } catch (Exception e) {
            logger.error("Failed to handled incoming request", e);
            throw new IVRException("Failed to handled incoming request", e);
        }
    }
}
