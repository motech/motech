package org.motechproject.admin.web.controller;

import org.motechproject.admin.domain.NotificationRule;
import org.motechproject.admin.domain.StatusMessage;
import org.motechproject.admin.messages.Level;
import org.motechproject.admin.service.StatusMessageService;
import org.motechproject.osgi.web.UIFrameworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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

    @Autowired
    private UIFrameworkService uiFrameworkService;

    @RequestMapping(value = "/messages", method = RequestMethod.GET)
    @ResponseBody public List<StatusMessage> getMessages(@RequestParam(defaultValue = "false") boolean all) {
        uiFrameworkService.moduleBackToNormal("admin", "messages");
        return (all ? statusMessageService.getAllMessages() : statusMessageService.getActiveMessages());
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "messages", method = RequestMethod.POST)
    public void postMessage(@RequestParam(required = true) String text, @RequestParam(required = true) String moduleName,
                            @RequestParam String level) {
        Level levelEnum = Level.fromString(level);
        if (levelEnum == null) {
            statusMessageService.info(text, moduleName);
        } else {
            statusMessageService.postMessage(text, moduleName, levelEnum);
        }
    }

    @RequestMapping(value = "/messages/rules", method = RequestMethod.GET)
    @ResponseBody public List<NotificationRule> getNotificationRules() {
        return statusMessageService.getNotificationRules();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/messages/rules/dto", method = RequestMethod.POST)
    public void saveNotificationRules(@RequestBody NotificationRuleDto notificationRuleDto) {
        for (String id : notificationRuleDto.getIdsToRemove()) {
            statusMessageService.removeNotificationRule(id);
        }

        statusMessageService.saveNotificationRules(notificationRuleDto.getNotificationRules());
    }

    @RequestMapping(value = "/messages/rules/{ruleId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteRule(@PathVariable String ruleId) {
        statusMessageService.removeNotificationRule(ruleId);
    }

    @RequestMapping(value = "/messages/rules", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void addRule(@RequestBody NotificationRule notificationRule) {
        statusMessageService.saveRule(notificationRule);
    }
}
