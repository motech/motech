package org.motechproject.tasks.web;

import org.motechproject.tasks.domain.TaskDataProvider;
import org.motechproject.tasks.service.TaskDataProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class TaskDataProviderController {
    private TaskDataProviderService taskDataProviderService;

    @Autowired
    public TaskDataProviderController(TaskDataProviderService taskDataProviderService) {
        this.taskDataProviderService = taskDataProviderService;
    }

    @RequestMapping(value = "datasource", method = RequestMethod.GET)
    @ResponseBody
    public List<TaskDataProvider> getAllDataProviders() {
        return taskDataProviderService.getProviders();
    }
}
