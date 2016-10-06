package org.motechproject.admin.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.motechproject.admin.domain.NotificationRule;
import org.motechproject.admin.domain.StatusMessage;
import org.motechproject.admin.messages.Level;
import org.motechproject.admin.service.StatusMessageService;
import org.motechproject.admin.web.dto.NotificationRuleDto;
import org.motechproject.osgi.web.service.UIFrameworkService;
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

/**
 * The controller that handles the status message UI.
 * @see org.motechproject.admin.domain.StatusMessage
 */
@Controller
@Api(value="MessageController", description = "The controller that handles the status message UI")
public class MessageController {

    @Autowired
    private StatusMessageService statusMessageService;

    @Autowired
    private UIFrameworkService uiFrameworkService;

    /**
     * Retrieves a list status messages.
     * @param all true if we want to retrieve all messages, regardless of their timeout value
     * @return the list of messages
     */
    @RequestMapping(value = "/messages", method = RequestMethod.GET)
    @ApiOperation(value="Retrieves a list status messages")
    @ResponseBody public List<StatusMessage> getMessages(@RequestParam(defaultValue = "false") boolean all) {
        uiFrameworkService.moduleBackToNormal("admin", "admin.messages");
        return (all ? statusMessageService.getAllMessages() : statusMessageService.getActiveMessages());
    }

    /**
     * Used for posting a new message in the system.
     * @param text the text of the new message
     * @param moduleName the module name to which this message relates to
     * @param level the level of the message, must match the enum values from {@link org.motechproject.admin.messages.Level}
     * @see org.motechproject.admin.service.StatusMessageService#postMessage(String, String, org.motechproject.admin.messages.Level)
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "messages", method = RequestMethod.POST)
    @ApiOperation(value="Used for posting a new message in the system")
    public void postMessage(@RequestParam(required = true) String text, @RequestParam(required = true) String moduleName,
                            @RequestParam String level) {
        Level levelEnum = Level.fromString(level);
        if (levelEnum == null) {
            statusMessageService.info(text, moduleName);
        } else {
            statusMessageService.postMessage(text, moduleName, levelEnum);
        }
    }

    /**
     * Returns all notification rules defined in the system
     * @return all notification rules defined
     * @see org.motechproject.admin.service.StatusMessageService#getNotificationRules()
     */
    @RequestMapping(value = "/messages/rules", method = RequestMethod.GET)
    @ApiOperation(value="Returns all notification rules defined in the system")
    @ResponseBody public List<NotificationRule> getNotificationRules() {
        return statusMessageService.getNotificationRules();
    }

    /**
     * Used for posting a request with changes to existing notification rules. It also deletes rules
     * removed before pressing the save button on the UI.
     * @param notificationRuleDto new rules and ids of the ones to delete
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/messages/rules/dto", method = RequestMethod.POST)
    @ApiOperation(value="Used for posting a request with changes to existing notification rules. It also deletes rules\n" +
            "removed before pressing the save button on the UI.")
    public void saveNotificationRules(@RequestBody NotificationRuleDto notificationRuleDto) {
        for (String id : notificationRuleDto.getIdsToRemove()) {
            statusMessageService.removeNotificationRule(id);
        }

        statusMessageService.saveNotificationRules(notificationRuleDto.getNotificationRules());
    }

    /**
     * Retrieves a single notification rule with the id provided in the path.
     * @param ruleId the id of the rule to retrieve
     */
    @RequestMapping(value = "/messages/rules/{ruleId}", method = RequestMethod.DELETE)
    @ApiOperation(value="Retrieves a single notification rule with the id provided in the path")
    @ResponseStatus(HttpStatus.OK)
    public void deleteRule(@PathVariable String ruleId) {
        statusMessageService.removeNotificationRule(ruleId);
    }

    /**
     * Adds a new notification rule to the system.
     * @param notificationRule the new notification rule
     * @see org.motechproject.admin.service.StatusMessageService#saveRule(org.motechproject.admin.domain.NotificationRule)
     */
    @RequestMapping(value = "/messages/rules", method = RequestMethod.POST)
    @ApiOperation(value="Adds a new notification rule to the system")
    @ResponseStatus(HttpStatus.OK)
    public void addRule(@RequestBody NotificationRule notificationRule) {
        statusMessageService.saveRule(notificationRule);
    }
}
