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
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String compensationUrl;
    private String compensationByIdUrl;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        compensationByIdUrl = "http://localhost:" + port + "/compensation/{id}";
        compensationUrl = "http://localhost:" + port + "/compensation";

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
    private static void assertCompensationEquivalence(Compensation expected, Compensation actual) {
        assertEquals(expected.getSalary(), actual.getSalary());
        assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
        assertEquals(expected.getEmployeeId(), actual.getEmployeeId());
    }

    @Test
    public void assertCreateReadGetNewReportingSturcture() {


        Employee johnnyEmployee = new Employee();
        johnnyEmployee.setFirstName("Johnny");
        johnnyEmployee.setLastName("Cash");
        johnnyEmployee.setDepartment("Engineering");
        johnnyEmployee.setPosition("Developer");

        List<Employee> newEmployeeList = new ArrayList<>();
        newEmployeeList.add(johnnyEmployee);
        Employee mockEmployee = new Employee();
        mockEmployee.setFirstName("Bob");
        mockEmployee.setLastName("Dylan");
        mockEmployee.setDepartment("Engineering");
        mockEmployee.setPosition("Developer");
        mockEmployee.setDirectReports(newEmployeeList);


        ReportingStructure expectedReportingStructure = new ReportingStructure();
        expectedReportingStructure.setEmployee("Bob Dylan");
        expectedReportingStructure.setNumberOfReports(1);




        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, mockEmployee, Employee.class).getBody();
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();


        String employeeName = mockEmployee.getFirstName().concat(" ").concat(mockEmployee.getLastName());
        ReportingStructure reportingStructure =  new ReportingStructure();
        reportingStructure.setEmployee(employeeName);
        reportingStructure.setNumberOfReports(mockEmployee.getDirectReports().size());


        assertEquals(expectedReportingStructure, reportingStructure);

    }
    public void assertInsertedCompenstion(){
        String localDate = LocalDate.now().toString();

        Compensation compensation = new Compensation();
        compensation.setEmployeeId("2342342");
        compensation.setSalary("1200000");
        compensation.setEffectiveDate(localDate);

        Compensation createCompensation = restTemplate.postForEntity(compensationUrl, compensation, Compensation.class).getBody();


        assertCompensationEquivalence(compensation, createCompensation);
    }

public void assertRetriveCompenstion(){
    String localDate = LocalDate.now().toString();


    Compensation compensation = new Compensation();
    compensation.setEmployeeId("2342342");
    compensation.setSalary("1200000");
    compensation.setEffectiveDate(localDate);

    Compensation readCompensation = restTemplate.getForEntity(compensationByIdUrl, Compensation.class, "2342342").getBody();


    assertCompensationEquivalence(compensation, readCompensation);
}

}
