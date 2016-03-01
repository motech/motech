package org.motechproject.tasks.web;

import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.domain.TaskTriggerInformation;
import org.motechproject.tasks.domain.TriggerEvent;
import org.motechproject.tasks.domain.TriggersList;
import org.motechproject.tasks.domain.TriggersLists;
import org.motechproject.tasks.service.ChannelService;
import org.motechproject.tasks.service.TriggerEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Controller for managing channels.
 */
@Controller
public class ChannelController {

    private static final int PAGE_SIZE = 10;

    private ChannelService channelService;
    private TriggerEventService triggerEventService;

    /**
     * Controller constructor.
     *
     * @param channelService  the channel service, not null
     */
    @Autowired
    public ChannelController(ChannelService channelService, TriggerEventService triggerEventService) {
        this.channelService = channelService;
        this.triggerEventService = triggerEventService;
    }

    /**
     * Returns the list of all channels.
     *
     * @return  the list of all channels
     */
    @RequestMapping(value = "channel", method = RequestMethod.GET)
    @ResponseBody
    public List<Channel> getAllChannels() {
        return channelService.getAllChannels();
    }

    @RequestMapping(value = "channel/triggers", method = RequestMethod.GET)
    @ResponseBody
    public TriggersLists getTriggers(@RequestParam String moduleName,
                                     @RequestParam int staticTriggersPage,
                                     @RequestParam int dynamicTriggersPage) {
        TriggersList staticTriggers = new TriggersList();
        long staticTriggersCount = triggerEventService.countStaticTriggers(moduleName);
        staticTriggers.addTriggers(triggerEventService.getStaticTriggers(moduleName, staticTriggersPage, PAGE_SIZE));
        staticTriggers.setPage(staticTriggersPage);

        int staticTriggersPages = (int) staticTriggersCount / PAGE_SIZE;
        if (staticTriggersCount % PAGE_SIZE > 0) {
            staticTriggersPages++;
        }
        staticTriggers.setTotal(staticTriggersPages);

        TriggersList dynamicTriggers = new TriggersList();

        if (triggerEventService.providesDynamicTriggers(moduleName)) {
            dynamicTriggers = new TriggersList();
            long dynamicTriggersCount = triggerEventService.countDynamicTriggers(moduleName);
            dynamicTriggers.addTriggers(triggerEventService.getDynamicTriggers(moduleName, dynamicTriggersPage, PAGE_SIZE));
            dynamicTriggers.setPage(dynamicTriggersPage);

            int dynamicTriggersPages = (int) dynamicTriggersCount / PAGE_SIZE;
            if (dynamicTriggersCount % PAGE_SIZE > 0) {
                dynamicTriggersPages++;
            }
            dynamicTriggers.setTotal(dynamicTriggersPages);
        }

        return new TriggersLists(staticTriggers, dynamicTriggers);
    }

    /**
     * Returns a trigger matching the given instance of the {@link TaskTriggerInformation} class.
     *
     * @param info  the information about the trigger
     * @return the trigger matching given information
     */
    @RequestMapping(value = "channel/trigger", method = RequestMethod.POST)
    @ResponseBody
    public TriggerEvent getTrigger(@RequestBody TaskTriggerInformation info) {
        return triggerEventService.getTrigger(info);
    }
}
