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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Employee Integration Tests")
class EmployeeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setUp() {
        // Clear all employees before each test
        employeeRepository.deleteAll();
    }

    // ========== CREATE AND GET ==========
    @Test
    @DisplayName("Integration: Create employee and retrieve it")
    void testCreateAndRetrieveEmployee() throws Exception {
        Employee newEmployee = new Employee(null, "Integration Test", "integration@test.com", "IT", 75000.0, "123-456-7890", "Tester");

        // Create employee
        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEmployee)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Integration Test"));

        // Verify it exists
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Integration Test"));
    }

    // ========== CREATE MULTIPLE AND FILTER ==========
    @Test
    @DisplayName("Integration: Create multiple employees and filter by department")
    void testCreateMultipleAndFilterByDepartment() throws Exception {
        Employee itEmployee = new Employee(null, "John IT", "john@example.com", "IT", 75000.0, "123-456-7890", "Developer");
        Employee hrEmployee = new Employee(null, "Jane HR", "jane@example.com", "HR", 65000.0, "098-765-4321", "Manager");

        // Create IT employee
        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itEmployee)))
                .andExpect(status().isCreated());

        // Create HR employee
        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hrEmployee)))
                .andExpect(status().isCreated());

        // Filter by IT department
        mockMvc.perform(get("/api/employees/department/IT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("John IT"));

        // Filter by HR department
        mockMvc.perform(get("/api/employees/department/HR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Jane HR"));
    }

    // ========== FULL CRUD CYCLE ==========
    @Test
    @DisplayName("Integration: Full CRUD cycle")
    void testFullCRUDCycle() throws Exception {
        Employee newEmployee = new Employee(null, "CRUD Test", "crud@test.com", "QA", 60000.0, "555-666-7777", "QA Engineer");

        // CREATE
        String response = mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEmployee)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Employee createdEmployee = objectMapper.readValue(response, Employee.class);
        Long employeeId = createdEmployee.getId();

        // READ
        mockMvc.perform(get("/api/employees/" + employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("CRUD Test"));

        // UPDATE
        Employee updateDetails = new Employee(null, "CRUD Test Updated", null, null, 65000.0, null, null);
        mockMvc.perform(put("/api/employees/" + employeeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("CRUD Test Updated"))
                .andExpect(jsonPath("$.salary").value(65000.0));

        // DELETE
        mockMvc.perform(delete("/api/employees/" + employeeId))
                .andExpect(status().isNoContent());

        // VERIFY DELETED
        mockMvc.perform(get("/api/employees/" + employeeId))
                .andExpect(status().isNotFound());
    }

    // ========== SEARCH FUNCTIONALITY ==========
    @Test
    @DisplayName("Integration: Search employees by name")
    void testSearchEmployeesByName() throws Exception {
        Employee emp1 = new Employee(null, "John Smith", "john@test.com", "IT", 75000.0, "123-456-7890", "Developer");
        Employee emp2 = new Employee(null, "Jane Doe", "jane@test.com", "HR", 65000.0, "098-765-4321", "Manager");
        Employee emp3 = new Employee(null, "Johnny Brown", "johnny@test.com", "Finance", 70000.0, "111-222-3333", "Accountant");

        // Create employees
        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emp1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emp2)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emp3)))
                .andExpect(status().isCreated());

        // Search for "John"
        mockMvc.perform(get("/api/employees/search?name=John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("John Smith", "Johnny Brown")));
    }

    // ========== EMPTY DATABASE ==========
    @Test
    @DisplayName("Integration: Get all employees from empty database")
    void testGetAllEmployeesEmptyDatabase() throws Exception {
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ========== PAGINATION SCENARIO ==========
    @Test
    @DisplayName("Integration: Create 10 employees and retrieve all")
    void testMultipleEmployeesCreation() throws Exception {
        // Create 10 employees
        for (int i = 1; i <= 10; i++) {
            Employee employee = new Employee(null, "Employee " + i, "emp" + i + "@test.com", "Dept" + (i % 3), 50000.0 + (i * 1000), "111-" + String.format("%03d", i) + "-2222", "Role" + i);
            
            mockMvc.perform(post("/api/employees")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(employee)))
                    .andExpect(status().isCreated());
        }

        // Retrieve all
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)));
    }
}
