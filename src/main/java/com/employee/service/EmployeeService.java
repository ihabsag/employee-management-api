package com.employee.service;

import com.employee.model.Employee;
import com.employee.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    // Get all employees
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // Get employee by ID
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    // Get employees by department
    public List<Employee> getEmployeesByDepartment(String department) {
        return employeeRepository.findByDepartment(department);
    }

    // Search employees by name
    public List<Employee> searchEmployeesByName(String name) {
        return employeeRepository.findByNameContainingIgnoreCase(name);
    }

    // Get employee by email
    public Optional<Employee> getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

    // Create new employee
    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    // Update employee
    public Optional<Employee> updateEmployee(Long id, Employee employeeDetails) {
        Optional<Employee> employeeOptional = employeeRepository.findById(id);
        if (employeeOptional.isPresent()) {
            Employee employee = employeeOptional.get();
            if (employeeDetails.getName() != null) {
                employee.setName(employeeDetails.getName());
            }
            if (employeeDetails.getEmail() != null) {
                employee.setEmail(employeeDetails.getEmail());
            }
            if (employeeDetails.getDepartment() != null) {
                employee.setDepartment(employeeDetails.getDepartment());
            }
            if (employeeDetails.getSalary() != null) {
                employee.setSalary(employeeDetails.getSalary());
            }
            if (employeeDetails.getPhoneNumber() != null) {
                employee.setPhoneNumber(employeeDetails.getPhoneNumber());
            }
            if (employeeDetails.getJobTitle() != null) {
                employee.setJobTitle(employeeDetails.getJobTitle());
            }
            return Optional.of(employeeRepository.save(employee));
        }
        return Optional.empty();
    }

    // Delete employee
    public boolean deleteEmployee(Long id) {
        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
            return true;
        }
        return false;
    }
}