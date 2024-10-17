package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDate;
import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String reportingStructureUrl;
    private String compensationUrl;
    private String compensationByEmployeeUrl;

    @Autowired
    private EmployeeService employeeService;

    @org.springframework.boot.test.web.server.LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        reportingStructureUrl = "http://localhost:" + port + "/reporting-structure/{id}";
        compensationUrl = "http://localhost:" + port + "/compensation";
        compensationByEmployeeUrl = "http://localhost:" + port + "/compensation/{id}";
    }

    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);


        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);


        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(readEmployee, headers),
                        Employee.class,
                        readEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }
    
    @Test
    public void testGetReportingStructure() {
        // Setup Employees with reporting structure
        Employee manager = new Employee();
        manager.setFirstName("Jane");
        manager.setLastName("Smith");
        manager.setDepartment("HR");
        manager.setPosition("Manager");

        Employee directReport1 = new Employee();
        directReport1.setFirstName("John");
        directReport1.setLastName("Doe");
        directReport1.setDepartment("Engineering");
        directReport1.setPosition("Developer");

        Employee directReport2 = new Employee();
        directReport2.setFirstName("Alice");
        directReport2.setLastName("Johnson");
        directReport2.setDepartment("Engineering");
        directReport2.setPosition("Tester");

        // Set direct reports
        manager.setDirectReports(Arrays.asList(directReport1, directReport2));

        // Save employees
        Employee createdManager = restTemplate.postForEntity(employeeUrl, manager, Employee.class).getBody();
        restTemplate.postForEntity(employeeUrl, directReport1, Employee.class);
        restTemplate.postForEntity(employeeUrl, directReport2, Employee.class);

        // Get Reporting Structure
        ReportingStructure reportingStructure = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, createdManager.getEmployeeId()).getBody();

        assertNotNull(reportingStructure);
        assertEquals(createdManager.getEmployeeId(), reportingStructure.getEmployee().getEmployeeId());
        assertEquals(2, reportingStructure.getNumberOfReports());
    }

    @SuppressWarnings({ "deprecation", "removal" })
	@Test
    public void testCreateAndReadCompensation() {
        // Setup Employee
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("Sarah");
        testEmployee.setLastName("Connor");
        testEmployee.setDepartment("IT");
        testEmployee.setPosition("CTO");

        // Create Employee
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();
        assertNotNull(createdEmployee.getEmployeeId());

        // Create Compensation
        Compensation compensation = new Compensation();
        compensation.setEmployee(createdEmployee);
        compensation.setSalary(new Double("120000"));
        compensation.setEffectiveDate(LocalDate.now());

        Compensation createdCompensation = restTemplate.postForEntity(compensationUrl, compensation, Compensation.class).getBody();
        assertNotNull(createdCompensation);

        // Read Compensation by Employee ID
        Compensation readCompensation = restTemplate.getForEntity(compensationByEmployeeUrl, Compensation.class, createdEmployee.getEmployeeId()).getBody();
        assertNotNull(readCompensation);
        assertEquals(createdEmployee.getEmployeeId(), readCompensation.getEmployee().getEmployeeId());
        assertEquals(createdCompensation.getSalary(), readCompensation.getSalary());
        assertEquals(createdCompensation.getEffectiveDate(), readCompensation.getEffectiveDate());
    }
}
