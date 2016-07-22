package org.motechproject.testmodule.service;

import org.motechproject.testmodule.domain.TaskTestObject;

import java.util.List;

public interface TasksTestService {

     TaskTestObject createTestObjectWithPostActionParameter(String name);

     List<TaskTestObject> getTaskTestObjects();
}
