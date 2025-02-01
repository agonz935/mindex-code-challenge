package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee.getEmployeeId());

        employee.setEmployeeId(UUID.randomUUID().toString());

        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Retrieving info for employee with id [{}]", id);

        //PUT was creating another document of same employee. Used this method to get most recent
        //but then realized if PUT is creating and not updating then it *should* break
        //Employee employee = employeeRepository.findTopByEmployeeIdOrderByUpdateDateDesc(id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    // PUT (update) not updating and is creating another document. Causes read error.
    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee.getEmployeeId());

        return employeeRepository.save(employee);
    }
}
