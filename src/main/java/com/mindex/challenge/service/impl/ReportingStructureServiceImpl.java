package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;

import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import com.mindex.challenge.service.ReportingStructureService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {

    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);

    @Autowired
    private EmployeeService employeeService;

    @Override
    public ReportingStructure read(Employee employee) {
        LOG.debug("Creating reporting structure for employee [{}]", employee.getEmployeeId());

        ReportingStructure reportingStructure = new ReportingStructure();
        reportingStructure.setEmployee(employee);

        int numberOfReports = calcNumberOfReports(employee);
        reportingStructure.setNumberOfReports(numberOfReports);

        return reportingStructure;
    }

    /**
     * Iterates through direct and indirect reports recursively
     *
     * @param employee Employee we want to calculate from
     * @return The number of reports or 0 if no reports
     */
    private int calcNumberOfReports(Employee employee) {

        // If employee's name is null, use read employee service to get info
        if (employee.getFirstName() == null) {
            employee = employeeService.read(employee.getEmployeeId());
        }

        int numberOfReports = 0; //initialize. If direct reports null then will stay 0.

        //Recursively go through direct reports
        if (employee.getDirectReports() != null) {
            for (Employee reports : employee.getDirectReports()) {
                numberOfReports++;
                numberOfReports += calcNumberOfReports(reports);
            }
        }

        return numberOfReports;
    }
}
