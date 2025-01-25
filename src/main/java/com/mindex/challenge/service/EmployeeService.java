package com.mindex.challenge.service;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;

public interface EmployeeService {
    Employee create(Employee employee);
    Employee read(String id);
    ReportingStructure getEmployeeStructure(String id);
    Employee update(Employee employee);
    Compensation getCompensation(String employeeId);

    Compensation createCompensation(Compensation compensation);
}
