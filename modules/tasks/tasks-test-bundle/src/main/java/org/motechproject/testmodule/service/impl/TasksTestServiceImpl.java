package org.motechproject.testmodule.service.impl;

import org.motechproject.testmodule.domain.TaskTestObject;
import org.motechproject.testmodule.service.TasksTestService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of the {@link TasksTestService} interface.
 */
@Service("tasksTestService")
public class TasksTestServiceImpl implements TasksTestService {

    private List<TaskTestObject> taskTestObjects = new ArrayList<>();

    @Override
    public TaskTestObject createTestObjectWithPostActionParameter(String name) {
        TaskTestObject object = new TaskTestObject(name);

        object.setTestNameWithPrefix(name + " - postActionParameter");

        taskTestObjects.add(object);
        return object;
    }

    @Override
    public List<TaskTestObject> getTaskTestObjects() {
        return taskTestObjects;
    }
}
