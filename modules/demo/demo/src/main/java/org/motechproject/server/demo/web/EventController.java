package org.motechproject.server.demo.web;

import org.apache.log4j.Logger;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class EventController {

    private static final Logger LOGGER = Logger.getLogger(EventController.class);

    @Autowired
    private EventRelay eventRelay;

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/event", method = RequestMethod.POST)
    public void raiseEvent(@RequestBody MotechEvent event) {
        LOGGER.debug("Raising event [" + event + "]");
        eventRelay.sendEventMessage(event);
    }
}
