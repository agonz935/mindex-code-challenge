package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportingStructureServiceImplTest {

    private String employeeUrl;
    private String reportingStructureUrl;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        reportingStructureUrl = "http://localhost:" + port + "/employee/{id}/reporting-structure";
    }

    /**
     * Creating and testing reporting structure with mock employee data.
     * I thought about incorporating this code into the EmployeeServiceImplTest since a lot of it is the same, but ultimately decided against it
     * in order to tell one story at a time in each class.
     */
    @Test
    public void testReportingStructure() {
        // Create Mock Data
        Employee director = new Employee();
        director.setFirstName("Employee 1");
        director.setLastName("Last Name 1");
        director.setDepartment("Engineering");
        director.setPosition("Director");

        Employee manager1 = new Employee();
        manager1.setFirstName("Manager 1");
        manager1.setLastName("Manager Last Name 1");
        manager1.setDepartment("Engineering");
        manager1.setPosition("Manager1");

        Employee manager2 = new Employee();
        manager2.setFirstName("Manager 2");
        manager2.setLastName("Manager Last Name 2");
        manager2.setDepartment("Engineering");
        manager2.setPosition("Manager2");

        Employee employee1 = new Employee();
        employee1.setFirstName("Contributor 1");
        employee1.setLastName("Contributor Last Name 1");
        employee1.setDepartment("Engineering");
        employee1.setPosition("employee1");

        Employee employee2 = new Employee();
        employee2.setFirstName("Contributor 2");
        employee2.setLastName("Contributor Last Name 2");
        employee2.setDepartment("Engineering");
        employee2.setPosition("employee2");


        // Create two employees with no direct reports
        employee1 = restTemplate.postForEntity(employeeUrl, employee1, Employee.class).getBody();
        employee2 = restTemplate.postForEntity(employeeUrl, employee2, Employee.class).getBody();

        // Assert Employee IDs were created
        assertNotNull(employee1.getEmployeeId());
        assertNotNull(employee2.getEmployeeId());


        //Added individual employees to List, will be direct reports to Manager 1
        List<Employee> ManagerReports = new ArrayList<>();
        ManagerReports.add(employee1);
        ManagerReports.add(employee2);
        manager1.setDirectReports(ManagerReports);

        // Create two Managers. Manager1 will have two direct reports, manager2 will have none
        manager1 = restTemplate.postForEntity(employeeUrl, manager1, Employee.class).getBody();
        manager2 = restTemplate.postForEntity(employeeUrl, manager2, Employee.class).getBody();

        // Check to make sure created Manager IDs were created
        assertNotNull(manager1.getEmployeeId());
        assertNotNull(manager2.getEmployeeId());

        // Added managers to list, will be direct reports to Director
        List<Employee> directorReports = new ArrayList<>();
        directorReports.add(manager1);
        directorReports.add(manager2);
        director.setDirectReports(directorReports);

        // Create Director
        director = restTemplate.postForEntity(employeeUrl, director, Employee.class).getBody();

        // Assert director ID was created
        assertNotNull(director.getEmployeeId());


        //Testing total reports for employees, read checks
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Director is expected to have 4 total reports. Two direct reports and two indirect reports from manager1.
        ReportingStructure bossReports = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, director.getEmployeeId()).getBody();
        assertEquals(4, bossReports.getNumberOfReports());

        // Manager1 is expected to have 2 total reports.
        ReportingStructure managerReports = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, manager1.getEmployeeId()).getBody();
        assertEquals(2, managerReports.getNumberOfReports());

        // Individual employees expected to have no reports.
        ReportingStructure individualReports = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, employee1.getEmployeeId()).getBody();
        assertEquals(0, individualReports.getNumberOfReports());
    }
}
