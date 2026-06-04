package com.employee.service;

import com.employee.model.Employee;
import com.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Employee Service Tests")
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee1;
    private Employee employee2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        employee1 = new Employee(1L, "John Doe", "john@example.com", "IT", 75000.0, "123-456-7890", "Senior Developer");
        employee2 = new Employee(2L, "Jane Smith", "jane@example.com", "HR", 65000.0, "098-765-4321", "HR Manager");
    }

    // ========== GET ALL EMPLOYEES ==========
    @Test
    @DisplayName("Should return all employees")
    void testGetAllEmployees() {
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employee1, employee2));

        List<Employee> employees = employeeService.getAllEmployees();

        assertNotNull(employees);
        assertEquals(2, employees.size());
        assertEquals("John Doe", employees.get(0).getName());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no employees exist")
    void testGetAllEmployeesEmpty() {
        when(employeeRepository.findAll()).thenReturn(Arrays.asList());

        List<Employee> employees = employeeService.getAllEmployees();

        assertNotNull(employees);
        assertTrue(employees.isEmpty());
        verify(employeeRepository, times(1)).findAll();
    }

    // ========== GET EMPLOYEE BY ID ==========
    @Test
    @DisplayName("Should return employee when ID exists")
    void testGetEmployeeByIdSuccess() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));

        Optional<Employee> employee = employeeService.getEmployeeById(1L);

        assertTrue(employee.isPresent());
        assertEquals("John Doe", employee.get().getName());
        assertEquals("john@example.com", employee.get().getEmail());
        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when employee ID doesn't exist")
    void testGetEmployeeByIdNotFound() {
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Employee> employee = employeeService.getEmployeeById(999L);

        assertFalse(employee.isPresent());
        verify(employeeRepository, times(1)).findById(999L);
    }

    // ========== GET EMPLOYEES BY DEPARTMENT ==========
    @Test
    @DisplayName("Should return employees by department")
    void testGetEmployeesByDepartment() {
        when(employeeRepository.findByDepartment("IT")).thenReturn(Arrays.asList(employee1));

        List<Employee> employees = employeeService.getEmployeesByDepartment("IT");

        assertNotNull(employees);
        assertEquals(1, employees.size());
        assertEquals("IT", employees.get(0).getDepartment());
        verify(employeeRepository, times(1)).findByDepartment("IT");
    }

    @Test
    @DisplayName("Should return empty list for non-existent department")
    void testGetEmployeesByDepartmentEmpty() {
        when(employeeRepository.findByDepartment("Finance")).thenReturn(Arrays.asList());

        List<Employee> employees = employeeService.getEmployeesByDepartment("Finance");

        assertTrue(employees.isEmpty());
    }

    // ========== SEARCH EMPLOYEES BY NAME ==========
    @Test
    @DisplayName("Should search employees by name")
    void testSearchEmployeesByName() {
        when(employeeRepository.findByNameContainingIgnoreCase("John")).thenReturn(Arrays.asList(employee1));

        List<Employee> employees = employeeService.searchEmployeesByName("John");

        assertEquals(1, employees.size());
        assertEquals("John Doe", employees.get(0).getName());
        verify(employeeRepository, times(1)).findByNameContainingIgnoreCase("John");
    }

    @Test
    @DisplayName("Should search case-insensitive")
    void testSearchEmployeesByNameCaseInsensitive() {
        when(employeeRepository.findByNameContainingIgnoreCase("john")).thenReturn(Arrays.asList(employee1));

        List<Employee> employees = employeeService.searchEmployeesByName("john");

        assertEquals(1, employees.size());
    }

    // ========== GET EMPLOYEE BY EMAIL ==========
    @Test
    @DisplayName("Should return employee by email")
    void testGetEmployeeByEmail() {
        when(employeeRepository.findByEmail("john@example.com")).thenReturn(Optional.of(employee1));

        Optional<Employee> employee = employeeService.getEmployeeByEmail("john@example.com");

        assertTrue(employee.isPresent());
        assertEquals("john@example.com", employee.get().getEmail());
    }

    // ========== CREATE EMPLOYEE ==========
    @Test
    @DisplayName("Should create new employee")
    void testCreateEmployee() {
        when(employeeRepository.save(employee1)).thenReturn(employee1);

        Employee created = employeeService.createEmployee(employee1);

        assertNotNull(created);
        assertEquals("John Doe", created.getName());
        verify(employeeRepository, times(1)).save(employee1);
    }

    @Test
    @DisplayName("Should create employee with ID auto-generated")
    void testCreateEmployeeWithAutoGeneratedId() {
        Employee newEmployee = new Employee(null, "Alice Brown", "alice@example.com", "Finance", 70000.0, "111-222-3333", "Accountant");
        Employee savedEmployee = new Employee(3L, "Alice Brown", "alice@example.com", "Finance", 70000.0, "111-222-3333", "Accountant");
        
        when(employeeRepository.save(newEmployee)).thenReturn(savedEmployee);

        Employee created = employeeService.createEmployee(newEmployee);

        assertNotNull(created.getId());
        assertEquals(3L, created.getId());
    }

    // ========== UPDATE EMPLOYEE ==========
    @Test
    @DisplayName("Should update employee successfully")
    void testUpdateEmployeeSuccess() {
        Employee updatedDetails = new Employee(null, "John Updated", "john@example.com", "IT", 80000.0, null, null);
        Employee updated = new Employee(1L, "John Updated", "john@example.com", "IT", 80000.0, "123-456-7890", "Senior Developer");
        
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));
        when(employeeRepository.save(any(Employee.class))).thenReturn(updated);

        Optional<Employee> result = employeeService.updateEmployee(1L, updatedDetails);

        assertTrue(result.isPresent());
        assertEquals("John Updated", result.get().getName());
        assertEquals(80000.0, result.get().getSalary());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should return empty when updating non-existent employee")
    void testUpdateEmployeeNotFound() {
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Employee> result = employeeService.updateEmployee(999L, employee1);

        assertFalse(result.isPresent());
        verify(employeeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update only provided fields")
    void testUpdateEmployeePartialUpdate() {
        Employee updateDetails = new Employee(null, "John Updated", null, null, null, null, null);
        Employee updated = new Employee(1L, "John Updated", "john@example.com", "IT", 75000.0, "123-456-7890", "Senior Developer");
        
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));
        when(employeeRepository.save(any(Employee.class))).thenReturn(updated);

        Optional<Employee> result = employeeService.updateEmployee(1L, updateDetails);

        assertTrue(result.isPresent());
        assertEquals("John Updated", result.get().getName());
        assertEquals("john@example.com", result.get().getEmail()); // Unchanged
    }

    // ========== DELETE EMPLOYEE ==========
    @Test
    @DisplayName("Should delete employee successfully")
    void testDeleteEmployeeSuccess() {
        when(employeeRepository.existsById(1L)).thenReturn(true);

        boolean deleted = employeeService.deleteEmployee(1L);

        assertTrue(deleted);
        verify(employeeRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should return false when deleting non-existent employee")
    void testDeleteEmployeeNotFound() {
        when(employeeRepository.existsById(999L)).thenReturn(false);

        boolean deleted = employeeService.deleteEmployee(999L);

        assertFalse(deleted);
        verify(employeeRepository, never()).deleteById(any());
    }
}
