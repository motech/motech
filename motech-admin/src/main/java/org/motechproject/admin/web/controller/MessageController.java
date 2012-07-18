package org.motechproject.admin.web.controller;

import org.motechproject.admin.domain.StatusMessage;
import org.motechproject.admin.messages.Level;
import org.motechproject.admin.service.StatusMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class MessageController {

    @Autowired
    StatusMessageService statusMessageService;

    @RequestMapping(value = "/messages", method = RequestMethod.GET)
    public @ResponseBody List<StatusMessage> getMessages(@RequestParam(defaultValue = "false") boolean all) {
        return (all ? statusMessageService.getAllMessages() : statusMessageService.getActiveMessages());
    }
}
