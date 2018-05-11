package org.motechproject.tasks.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.motechproject.tasks.dto.TaskDataProviderDto;
import org.motechproject.tasks.service.TaskWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Controller for managing Data Providers.
 */
@Controller
@Api(value="TaskDataProviderController", description = "Controller for managing Data Providers")
public class TaskDataProviderController {

    private TaskWebService taskWebService;

    /**
     * Controller constructor.
     *
     */
    @Autowired
    public TaskDataProviderController(TaskWebService taskWebService) {
        this.taskWebService = taskWebService;
    }

    /**
     * Returns the list of all Data Providers.
     *
     * @return  the list of all Data Providers
     */
    @RequestMapping(value = "datasource", method = RequestMethod.GET)
    @ApiOperation(value="Returns the list of all Data Providers")
    @ResponseBody
    public List<TaskDataProviderDto> getAllDataProviders() {
        return taskWebService.getProviders();
    }
}
