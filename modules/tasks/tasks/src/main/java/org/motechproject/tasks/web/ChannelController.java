package org.motechproject.tasks.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.motechproject.tasks.domain.mds.task.TaskTriggerInformation;
import org.motechproject.tasks.web.domain.TriggersList;
import org.motechproject.tasks.web.domain.TriggersLists;
import org.motechproject.tasks.dto.ChannelDto;
import org.motechproject.tasks.dto.TaskTriggerInformationDto;
import org.motechproject.tasks.dto.TriggerEventDto;
import org.motechproject.tasks.service.TaskWebService;
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
@Api(value="ChannelController", description = "Controller for managing channels")
public class ChannelController {

    private static final int PAGE_SIZE = 10;

    private TriggerEventService triggerEventService;
    private TaskWebService taskWebService;

    /**
     * Controller constructor.
     *
     */
    @Autowired
    public ChannelController(TaskWebService taskWebService, TriggerEventService triggerEventService) {
        this.taskWebService = taskWebService;
        this.triggerEventService = triggerEventService;
    }

    /**
     * Returns the list of all channels.
     *
     * @return  the list of all channels
     */
    @RequestMapping(value = "channel", method = RequestMethod.GET)
    @ApiOperation(value="Returns the list of all channels")
    @ResponseBody
    public List<ChannelDto> getAllChannels() {
        return taskWebService.getAllChannels();
    }

    /**
     * Returns the list of triggers provider by the module with the given {@code moduleName}.
     *
     * @param moduleName  the name of the module
     * @param staticTriggersPage  the number of the page of static trigger
     * @param dynamicTriggersPage  the number of the page of dynamic trigger
     * @return the list of both static and dynamic triggers
     */
    @RequestMapping(value = "channel/triggers", method = RequestMethod.GET)
    @ApiOperation(value="Returns the list of triggers provider by the module with the given moduleName")
    @ResponseBody
    public TriggersLists getTriggers(@RequestParam String moduleName,
                                     @RequestParam int staticTriggersPage,
                                     @RequestParam int dynamicTriggersPage) {
        TriggersList staticTriggers = new TriggersList();
        long staticTriggersCount = triggerEventService.countStaticTriggers(moduleName);
        staticTriggers.addTriggers(taskWebService.getStaticTriggers(moduleName, staticTriggersPage, (int) staticTriggersCount));
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
            dynamicTriggers.addTriggers(taskWebService.getDynamicTriggers(moduleName, dynamicTriggersPage, (int) dynamicTriggersCount));
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
     * Returns a trigger matching the given instance of the {@link TaskTriggerInformationDto} class.
     *
     * @param info  the information about the trigger
     * @return the trigger matching given information
     */
    @RequestMapping(value = "channel/trigger", method = RequestMethod.POST)
    @ApiOperation(value="Returns a trigger matching the given instance of the TaskTriggerInformationDto class")
    @ResponseBody
    public TriggerEventDto getTrigger(@RequestBody TaskTriggerInformation info) {
        return taskWebService.getTrigger(info);
    }
}
