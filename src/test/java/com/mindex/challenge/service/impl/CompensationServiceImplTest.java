package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {

    private String employeeUrl;
    private String compensationUrl;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        compensationUrl = "http://localhost:" + port +"/employee/{id}/compensation";
    }

    /**
     * Creates test data for Employee and Compensation. Uses Method: assertCompensationEquivalence to check for
     * compensation equivalence between fields employeeId, salary, and effectiveDate.
     */
    @Test
    public void testCompensationWorkflow() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("testEmployee");
        testEmployee.setLastName("testEmployeeLastName");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Dev");


        // Create testEmployee
        testEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();
        assertNotNull(testEmployee);

        // Set compensation test data
        Compensation testEmployeesSalary = new Compensation();
        testEmployeesSalary.setEmployeeId(testEmployee.getEmployeeId());
        testEmployeesSalary.setEffectiveDate(new Date());
        testEmployeesSalary.setSalary(new BigDecimal(3000000));


        // Create checks
        Compensation testEmployeesNewSalary = restTemplate.postForEntity(compensationUrl, testEmployeesSalary, Compensation.class, testEmployee.getEmployeeId()).getBody();
        assertNotNull(testEmployeesNewSalary);
        assertCompensationEquivalence(testEmployeesSalary, testEmployeesNewSalary);


        // Read checks
        Compensation readSalary = restTemplate.getForEntity(compensationUrl, Compensation.class, testEmployee.getEmployeeId()).getBody();
        assertNotNull(readSalary);
        assertCompensationEquivalence(testEmployeesSalary, readSalary);
    }

    private static void assertCompensationEquivalence(Compensation expected, Compensation actual) {
        assertEquals(expected.getSalary(), actual.getSalary());
        assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
        assertEquals(expected.getEmployeeId(), actual.getEmployeeId());
    }

}