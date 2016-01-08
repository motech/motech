package org.motechproject.mds.test.service.transactions;

import org.motechproject.mds.test.domain.transactions.Employee;

import java.util.Set;

public interface OfficeService {

    void saveEmployees(Long departmentId, Set<Employee> employees);
}
