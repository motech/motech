package org.motechproject.admin.web.controller;

import org.motechproject.admin.domain.StatusMessage;
import org.motechproject.admin.messages.Level;
import org.motechproject.admin.service.StatusMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Controller
public class MessageController {

    @Autowired
    private StatusMessageService statusMessageService;

    @RequestMapping(value = "/messages", method = RequestMethod.GET)
    @ResponseBody public List<StatusMessage> getMessages(@RequestParam(defaultValue = "false") boolean all) {
        return (all ? statusMessageService.getAllMessages() : statusMessageService.getActiveMessages());
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "messages", method = RequestMethod.POST)
    public void postMessage(@RequestParam(required = true) String text, @RequestParam String level) {
        Level levelEnum = Level.fromString(level);
        if (levelEnum == null) {
            statusMessageService.info(text);
        } else {
            statusMessageService.postMessage(text, levelEnum);
        }
    }
}
