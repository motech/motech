package org.motechproject.mds.test.service.impl;

import org.motechproject.mds.test.domain.transactions.Department;
import org.motechproject.mds.test.domain.transactions.Employee;
import org.motechproject.mds.test.service.transactions.DepartmentDataService;
import org.motechproject.mds.test.service.transactions.OfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service("officeService")
public class OfficeServiceImpl implements OfficeService {

    @Autowired
    private DepartmentDataService departmentDataService;

    @Override
    @Transactional
    public void saveEmployees(Long departmentId, Set<Employee> employees) {
        Department existingDepartment = departmentDataService.findById(departmentId);

        if (null != existingDepartment) {
            for (Employee employee : employees) {
                employee.setDepartment(existingDepartment);
            }

            existingDepartment.setEmployees(employees);
            departmentDataService.update(existingDepartment);
        } else {
            throw new IllegalArgumentException("Department with id " + departmentId + " doesn't exist");
        }
    }
}