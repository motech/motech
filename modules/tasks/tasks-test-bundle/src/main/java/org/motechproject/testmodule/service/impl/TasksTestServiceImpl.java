package org.motechproject.testmodule.service.impl;

import org.motechproject.testmodule.domain.TaskTestObject;
import org.motechproject.testmodule.service.TasksTestService;
import org.springframework.stereotype.Service;

/**
 * Default implementation of the {@link TasksTestService} interface.
 */
@Service("tasksTestService")
public class TasksTestServiceImpl implements TasksTestService {

    @Override
    public TaskTestObject createTestObjectWithPostActionParameter(String name) {
        TaskTestObject object = new TaskTestObject(name);

        object.setTestNameWithPrefix(name + " - postActionParameter");
        return object;
    }

    @Override
    public TaskTestObject createTestObject(String name, String nameWithPrefix) {
        return new TaskTestObject(name, nameWithPrefix);
    }
}