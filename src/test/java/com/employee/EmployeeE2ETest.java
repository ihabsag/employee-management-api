package com.employee;

import com.employee.model.Employee;
import com.employee.repository.EmployeeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-End Tests for Employee Management API
 * These tests verify complete API workflows and business scenarios
 * Can be run with or without Docker
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DisplayName("Employee Management API - End-to-End Tests")
class EmployeeE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * Clear database before each test to ensure test isolation
     */
    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
    }

    // ========== SCENARIO 1: NEW COMPANY ONBOARDING ==========
    @Test
    @DisplayName("E2E: New company onboarding - Create employees for multiple departments")
    void testCompanyOnboarding() throws Exception {
        // Scenario: A company is onboarding 3 employees from different departments
        
        // Create IT Employee
        Employee itEmp = new Employee(null, "Alice Johnson", "alice.johnson@company.com", "IT", 85000.0, "555-0101", "Senior Developer");
        MvcResult itResult = mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itEmp)))
                .andExpect(status().isCreated())
                .andReturn();

        Employee savedItEmp = objectMapper.readValue(itResult.getResponse().getContentAsString(), Employee.class);
        Long itEmpId = savedItEmp.getId();

        // Create HR Employee
        Employee hrEmp = new Employee(null, "Bob Smith", "bob.smith@company.com", "HR", 65000.0, "555-0102", "HR Manager");
        MvcResult hrResult = mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hrEmp)))
                .andExpect(status().isCreated())
                .andReturn();

        Employee savedHrEmp = objectMapper.readValue(hrResult.getResponse().getContentAsString(), Employee.class);
        Long hrEmpId = savedHrEmp.getId();

        // Create Finance Employee
        Employee finEmp = new Employee(null, "Charlie Davis", "charlie.davis@company.com", "Finance", 75000.0, "555-0103", "Accountant");
        MvcResult finResult = mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(finEmp)))
                .andExpect(status().isCreated())
                .andReturn();

        Employee savedFinEmp = objectMapper.readValue(finResult.getResponse().getContentAsString(), Employee.class);
        Long finEmpId = savedFinEmp.getId();

        // Verify all employees exist
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Alice Johnson", "Bob Smith", "Charlie Davis")));

        // Verify department filtering
        mockMvc.perform(get("/api/employees/department/IT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Alice Johnson"));

        mockMvc.perform(get("/api/employees/department/HR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Bob Smith"));

        mockMvc.perform(get("/api/employees/department/Finance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Charlie Davis"));
    }

    // ========== SCENARIO 2: EMPLOYEE PROMOTION & RAISE ==========
    @Test
    @DisplayName("E2E: Employee promotion scenario - Update department and salary")
    void testEmployeePromotion() throws Exception {
        // Scenario: Employee gets promoted from Developer to Manager with salary increase

        // Create initial employee (Developer)
        Employee developer = new Employee(null, "Diana Wilson", "diana@company.com", "IT", 70000.0, "555-0201", "Developer");
        MvcResult createResult = mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(developer)))
                .andExpect(status().isCreated())
                .andReturn();

        Employee savedDeveloper = objectMapper.readValue(createResult.getResponse().getContentAsString(), Employee.class);
        Long employeeId = savedDeveloper.getId();

        // Verify initial state
        mockMvc.perform(get("/api/employees/" + employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobTitle").value("Developer"))
                .andExpect(jsonPath("$.salary").value(70000.0))
                .andExpect(jsonPath("$.department").value("IT"));

        // Promote employee (update department, salary, and job title)
        Employee promotion = new Employee(null, "Diana Wilson", null, "IT", 90000.0, null, "Senior Manager");
        mockMvc.perform(put("/api/employees/" + employeeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(promotion)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.salary").value(90000.0))
                .andExpect(jsonPath("$.jobTitle").value("Senior Manager"));

        // Verify promotion was applied
        mockMvc.perform(get("/api/employees/" + employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobTitle").value("Senior Manager"))
                .andExpect(jsonPath("$.salary").value(90000.0));
    }

    // ========== SCENARIO 3: EMPLOYEE TRANSFER & SEARCH ==========
    @Test
    @DisplayName("E2E: Employee transfer - Create, search, update department")
    void testEmployeeTransfer() throws Exception {
        // Scenario: An employee transfers from one department to another

        // Create employee in Marketing
        Employee marketingEmp = new Employee(null, "Eve Martinez", "eve@company.com", "Marketing", 60000.0, "555-0301", "Marketing Manager");
        MvcResult createResult = mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(marketingEmp)))
                .andExpect(status().isCreated())
                .andReturn();

        Employee savedEmp = objectMapper.readValue(createResult.getResponse().getContentAsString(), Employee.class);
        Long employeeId = savedEmp.getId();

        // Search for employee
        mockMvc.perform(get("/api/employees/search?name=Eve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].department").value("Marketing"));

        // Transfer to Sales department with salary increase
        Employee transfer = new Employee(null, null, null, "Sales", 65000.0, null, null);
        mockMvc.perform(put("/api/employees/" + employeeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transfer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.department").value("Sales"))
                .andExpect(jsonPath("$.salary").value(65000.0));

        // Verify transfer by searching
        mockMvc.perform(get("/api/employees/search?name=Eve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].department").value("Sales"));

        // Verify no longer in Marketing
        mockMvc.perform(get("/api/employees/department/Marketing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ========== SCENARIO 4: DEPARTMENT TEAM MANAGEMENT ==========
    @Test
    @DisplayName("E2E: Department team management - Create team, add members, retrieve by department")
    void testDepartmentTeamManagement() throws Exception {
        // Scenario: Building an IT department team with multiple employees

        String[] itTeamNames = {"Frank Adams", "Grace Brown", "Henry Clark"};
        String[] emails = {"frank@company.com", "grace@company.com", "henry@company.com"};
        String[] jobTitles = {"Lead Architect", "Senior Developer", "Junior Developer"};
        double[] salaries = {95000.0, 80000.0, 55000.0};

        // Create IT team members
        for (int i = 0; i < itTeamNames.length; i++) {
            Employee teamMember = new Employee(null, itTeamNames[i], emails[i], "IT", salaries[i], "555-040" + i, jobTitles[i]);
            mockMvc.perform(post("/api/employees")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(teamMember)))
                    .andExpect(status().isCreated());
        }

        // Retrieve entire IT department
        mockMvc.perform(get("/api/employees/department/IT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].department", everyItem(equalTo("IT"))))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Frank Adams", "Grace Brown", "Henry Clark")));

        // Verify total count
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    // ========== SCENARIO 5: EMPLOYEE TERMINATION & CLEANUP ==========
    @Test
    @DisplayName("E2E: Employee termination workflow - Create, update, verify, delete")
    void testEmployeeTermination() throws Exception {
        // Scenario: Employee is being terminated - create, mark as inactive, delete

        // Create employee
        Employee employee = new Employee(null, "Iris Thompson", "iris@company.com", "Operations", 58000.0, "555-0501", "Operations Coordinator");
        MvcResult createResult = mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isCreated())
                .andReturn();

        Employee savedEmp = objectMapper.readValue(createResult.getResponse().getContentAsString(), Employee.class);
        Long employeeId = savedEmp.getId();

        // Verify employee exists
        mockMvc.perform(get("/api/employees/" + employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Iris Thompson"));

        // Delete employee
        mockMvc.perform(delete("/api/employees/" + employeeId))
                .andExpect(status().isNoContent());

        // Verify employee is deleted
        mockMvc.perform(get("/api/employees/" + employeeId))
                .andExpect(status().isNotFound());

        // Verify not in system
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ========== SCENARIO 6: BULK OPERATIONS & REPORTING ==========
    @Test
    @DisplayName("E2E: Bulk operations - Create multiple employees and generate reports")
    void testBulkOperationsAndReporting() throws Exception {
        // Scenario: Company creates 20 employees and needs various reports

        // Create 20 employees across 4 departments
        String[] departments = {"IT", "HR", "Finance", "Sales"};
        
        for (int i = 1; i <= 20; i++) {
            String dept = departments[(i - 1) % 4];
            Employee emp = new Employee(
                null,
                "Employee " + i,
                "emp" + i + "@company.com",
                dept,
                50000.0 + (i * 1000),
                "555-" + String.format("%04d", i),
                "Role " + i
            );
            
            mockMvc.perform(post("/api/employees")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(emp)))
                    .andExpect(status().isCreated());
        }

        // Report 1: Total employees
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(20)));

        // Report 2: IT department size
        mockMvc.perform(get("/api/employees/department/IT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5))); // 20 / 4 = 5 per dept

        // Report 3: Search for specific employees
        mockMvc.perform(get("/api/employees/search?name=Employee%201"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(11))); // Matches 1, 10, 11-19

        // Report 4: Each department
        for (String dept : departments) {
            mockMvc.perform(get("/api/employees/department/" + dept))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(5)));
        }
    }

    // ========== SCENARIO 7: DATA VALIDATION & ERROR HANDLING ==========
    @Test
    @DisplayName("E2E: Data validation and error handling scenarios")
    void testDataValidationAndErrorHandling() throws Exception {
        // Scenario: Test various validation and error scenarios

        // Create valid employee
        Employee validEmp = new Employee(null, "Jack Miller", "jack@company.com", "IT", 75000.0, "555-0701", "Developer");
        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validEmp)))
                .andExpect(status().isCreated());

        // Try to create duplicate email (should fail with bad request)
        Employee duplicateEmp = new Employee(null, "Jack Miller 2", "jack@company.com", "HR", 60000.0, "555-0702", "Manager");
        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateEmp)))
                .andExpect(status().isBadRequest());

        // Try to get non-existent employee
        mockMvc.perform(get("/api/employees/99999"))
                .andExpect(status().isNotFound());

        // Try to update non-existent employee
        Employee updateData = new Employee(null, "Updated", null, null, 80000.0, null, null);
        mockMvc.perform(put("/api/employees/99999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isNotFound());

        // Try to delete non-existent employee
        mockMvc.perform(delete("/api/employees/99999"))
                .andExpect(status().isNotFound());
    }

    // ========== SCENARIO 8: COMPLETE LIFECYCLE ==========
    @Test
    @DisplayName("E2E: Complete employee lifecycle - Create, Read, Update, Delete with verification")
    void testCompleteEmployeeLifecycle() throws Exception {
        // Scenario: Track an employee through their entire lifecycle

        // Step 1: Create (Onboarding)
        Employee newHire = new Employee(null, "Karen Young", "karen@company.com", "IT", 60000.0, "555-0801", "Junior Developer");
        MvcResult createResult = mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newHire)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Karen Young"))
                .andExpect(jsonPath("$.salary").value(60000.0))
                .andReturn();

        Employee createdEmp = objectMapper.readValue(createResult.getResponse().getContentAsString(), Employee.class);
        Long employeeId = createdEmp.getId();

        // Step 2: Read (Verify hiring)
        mockMvc.perform(get("/api/employees/" + employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Karen Young"))
                .andExpect(jsonPath("$.jobTitle").value("Junior Developer"));

        // Step 3: Update - Year 1 (Promotion)
        Employee yearOneUpdate = new Employee(null, null, null, null, 65000.0, null, "Mid-Level Developer");
        mockMvc.perform(put("/api/employees/" + employeeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(yearOneUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.salary").value(65000.0))
                .andExpect(jsonPath("$.jobTitle").value("Mid-Level Developer"));

        // Step 4: Update - Year 2 (Another raise)
        Employee yearTwoUpdate = new Employee(null, null, null, null, 75000.0, null, "Senior Developer");
        mockMvc.perform(put("/api/employees/" + employeeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(yearTwoUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.salary").value(75000.0))
                .andExpect(jsonPath("$.jobTitle").value("Senior Developer"));

        // Step 5: Read (Verify career progress)
        mockMvc.perform(get("/api/employees/" + employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Karen Young"))
                .andExpect(jsonPath("$.salary").value(75000.0))
                .andExpect(jsonPath("$.jobTitle").value("Senior Developer"));

        // Step 6: Delete (Retirement/Termination)
        mockMvc.perform(delete("/api/employees/" + employeeId))
                .andExpect(status().isNoContent());

        // Step 7: Verify deletion
        mockMvc.perform(get("/api/employees/" + employeeId))
                .andExpect(status().isNotFound());
    }
}
