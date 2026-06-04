# Employee Management API - E2E Testing Guide

## 🚀 End-to-End (E2E) Tests

Comprehensive E2E tests that simulate real-world business scenarios with Docker support.

## 📋 Test Scenarios Covered

### 1. **Company Onboarding**
- Create employees from multiple departments
- Verify department filtering
- Validate all employees are properly stored

### 2. **Employee Promotion**
- Create employee with initial details
- Update department, salary, and job title
- Verify promotion was applied correctly

### 3. **Employee Transfer**
- Create employee in one department
- Search for employee by name
- Transfer to another department
- Verify transfer completion

### 4. **Department Team Management**
- Create multiple team members
- Retrieve entire department
- Verify team size and member details

### 5. **Employee Termination**
- Create employee
- Verify employee exists
- Delete employee
- Confirm deletion

### 6. **Bulk Operations & Reporting**
- Create 20 employees
- Generate department reports
- Search and filter employees
- Verify data consistency

### 7. **Data Validation & Error Handling**
- Duplicate email validation
- Non-existent employee handling
- Invalid input handling
- Status code verification

### 8. **Complete Employee Lifecycle**
- Onboarding (Create)
- Verification (Read)
- Year 1 Promotion (Update)
- Year 2 Raise (Update)
- Career progression tracking
- Termination (Delete)

## 🐳 Running E2E Tests with Docker

### **Option 1: Automated Script (Linux/Mac)**

```bash
chmod +x run-e2e-tests.sh
./run-e2e-tests.sh
```

### **Option 2: Automated Script (Windows)**

```batch
run-e2e-tests.bat
```

### **Option 3: Manual Steps**

1. **Start Docker Compose:**
```bash
docker-compose up -d
```

2. **Wait for app to start:**
```bash
sleep 30
```

3. **Run E2E tests:**
```bash
mvn test -Dtest=EmployeeE2ETest
```

4. **Stop containers:**
```bash
docker-compose down
```

## 🏃 Running E2E Tests without Docker

```bash
# Start the application
mvn spring-boot:run

# In another terminal, run E2E tests
mvn test -Dtest=EmployeeE2ETest
```

## ✅ What Gets Tested

Each scenario verifies:
- ✅ HTTP status codes (201, 200, 204, 404, 400)
- ✅ Request/response payload validation
- ✅ Database persistence
- ✅ Data filtering and searching
- ✅ Update operations
- ✅ Delete operations
- ✅ Error handling

## 📊 Test Results

After running tests, you'll see:
```
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## 🔍 View Test Output

### In IntelliJ:
1. Open **Run → Run...**
2. Select **EmployeeE2ETest**
3. View detailed test results in the console

### From Terminal:
```bash
mvn test -Dtest=EmployeeE2ETest -X
```

## 📝 Sample Test Output

```
Running com.employee.EmployeeE2ETest
E2E: New company onboarding - Create employees for multiple departments ... OK
E2E: Employee promotion scenario - Update department and salary ... OK
E2E: Employee transfer - Create, search, update department ... OK
E2E: Department team management - Create team, add members, retrieve by department ... OK
E2E: Employee termination workflow - Create, update, verify, delete ... OK
E2E: Bulk operations - Create multiple employees and generate reports ... OK
E2E: Data validation and error handling scenarios ... OK
E2E: Complete employee lifecycle - Create, Read, Update, Delete with verification ... OK

Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
```

## 🚨 Troubleshooting

### Docker Port Already in Use
```bash
docker-compose down
docker system prune
docker-compose up -d
```

### Application Timeout
```bash
# Increase wait time in run-e2e-tests.sh from 30 to 60
sleep 60
```

### Tests Fail with Connection Error
```bash
# Check if Docker container is running
docker-compose ps

# View logs
docker-compose logs app
```

### Maven Build Fails
```bash
mvn clean install
mvn test -Dtest=EmployeeE2ETest
```

## 🎯 Integration with CI/CD

### GitHub Actions Example

```yaml
name: E2E Tests with Docker

on: [push, pull_request]

jobs:
  e2e-tests:
    runs-on: ubuntu-latest
    services:
      docker:
        image: docker:latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-java@v2
        with:
          java-version: '17'
      - name: Run E2E Tests
        run: ./run-e2e-tests.sh
```

## 📈 Performance Metrics

Typical E2E test execution times:
- Docker startup: ~10-15 seconds
- Tests execution: ~15-20 seconds
- Cleanup: ~5 seconds
- **Total: ~30-40 seconds**

## ✨ Best Practices

1. **Always run in isolated environment** (Docker)
2. **Clean up after tests** (docker-compose down)
3. **Use meaningful test names**
4. **Verify at each step**
5. **Test error scenarios**
6. **Check HTTP status codes**

## 📚 Related Documentation

- [Unit Tests](../service/EmployeeServiceTest.java)
- [Integration Tests](../EmployeeIntegrationTest.java)
- [Docker Setup](../docker-compose.yml)
- [API Endpoints](../README.md)
