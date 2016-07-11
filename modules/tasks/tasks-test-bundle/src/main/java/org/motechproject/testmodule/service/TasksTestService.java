package org.motechproject.testmodule.service;

import org.motechproject.testmodule.domain.TaskTestObject;

public interface TasksTestService {

     TaskTestObject createTestObjectWithPostActionParameter(String name);

    TaskTestObject createTestObject(String name, String nameWithPrefix);
}