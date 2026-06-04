# Employee Management API - Spring Boot + Docker

A complete Spring Boot REST API for managing employee information with full Docker support.

## 🚀 Features

- ✅ Create, Read, Update, Delete employees (CRUD operations)
- ✅ Search employees by name, email, or department
- ✅ RESTful API endpoints with proper HTTP status codes
- ✅ H2 in-memory database for development
- ✅ Spring Data JPA for database operations
- ✅ CORS enabled for frontend integration
- ✅ Docker & Docker Compose ready
- ✅ Multi-stage Docker build for optimized images
- ✅ Health checks configured

## 📋 Prerequisites

- **Option 1 (Docker):** Docker (v20.10+) and Docker Compose (v1.29+)
- **Option 2 (Local):** Java 17+ and Maven 3.6+

## 🐳 Quick Start with Docker

### 1. Clone the repository
```bash
git clone https://github.com/ihabsag/employee-management-api.git
cd employee-management-api
```

### 2. Run with Docker Compose
```bash
docker-compose up -d
```

### 3. Verify services are running
```bash
docker-compose ps
```

### 4. View logs
```bash
docker-compose logs -f app
```

### 5. Stop services
```bash
docker-compose down
```

## 💻 Local Development (Without Docker)

### 1. Clone the repository
```bash
git clone https://github.com/ihabsag/employee-management-api.git
cd employee-management-api
```

### 2. Build the project
```bash
mvn clean install
```

### 3. Run the application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080` with H2 in-memory database.

## 📡 API Endpoints

### Base URL
```
http://localhost:8080/api/employees
```

### 1. Get All Employees
```http
GET /api/employees
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "department": "IT",
    "salary": 75000.0,
    "phoneNumber": "123-456-7890",
    "jobTitle": "Senior Developer"
  }
]
```

### 2. Get Employee by ID
```http
GET /api/employees/{id}
```

**Example:**
```
GET /api/employees/1
```

### 3. Get Employees by Department
```http
GET /api/employees/department/{department}
```

**Example:**
```
GET /api/employees/department/IT
```

### 4. Search Employees by Name
```http
GET /api/employees/search?name={name}
```

**Example:**
```
GET /api/employees/search?name=John
```

### 5. Create New Employee
```http
POST /api/employees
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "Jane Smith",
  "email": "jane@example.com",
  "department": "HR",
  "salary": 65000.0,
  "phoneNumber": "098-765-4321",
  "jobTitle": "HR Manager"
}
```

**Response (201 Created):**
```json
{
  "id": 2,
  "name": "Jane Smith",
  "email": "jane@example.com",
  "department": "HR",
  "salary": 65000.0,
  "phoneNumber": "098-765-4321",
  "jobTitle": "HR Manager"
}
```

### 6. Update Employee
```http
PUT /api/employees/{id}
Content-Type: application/json
```

**Example:**
```
PUT /api/employees/1
```

**Request Body (partial update):**
```json
{
  "name": "John Doe Updated",
  "salary": 80000.0
}
```

### 7. Delete Employee
```http
DELETE /api/employees/{id}
```

**Example:**
```
DELETE /api/employees/1
```

**Response (204 No Content)**

## 🗄️ Database

The application uses **H2 in-memory database** for development.

### H2 Console
- **URL:** `http://localhost:8080/h2-console`
- **JDBC URL:** `jdbc:h2:mem:testdb`
- **Username:** `sa`
- **Password:** (leave blank)

## 📁 Project Structure

```
employee-management-api/
├── src/main/java/com/employee/
│   ├── EmployeeManagementApplication.java    # Main Spring Boot class
│   ├── controller/
│   │   └── EmployeeController.java           # REST endpoints
│   ├── model/
│   │   └── Employee.java                     # Entity class
│   ├── repository/
│   │   └── EmployeeRepository.java           # Data access layer
│   └── service/
│       └── EmployeeService.java              # Business logic layer
├── src/main/resources/
│   └── application.properties
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── .gitignore
└── README.md
```

## 🛠️ Technologies Used

- **Spring Boot 3.1.5**
- **Spring Data JPA**
- **H2 Database**
- **Docker & Docker Compose**
- **Maven**
- **Lombok**
- **Jakarta Persistence API**

## 🧪 Testing with cURL

```bash
# Get all employees
curl http://localhost:8080/api/employees

# Create a new employee
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "department": "IT",
    "salary": 75000,
    "phoneNumber": "123-456-7890",
    "jobTitle": "Senior Developer"
  }'

# Get employee by ID
curl http://localhost:8080/api/employees/1

# Search employees by name
curl "http://localhost:8080/api/employees/search?name=John"

# Get employees by department
curl http://localhost:8080/api/employees/department/IT

# Update employee
curl -X PUT http://localhost:8080/api/employees/1 \
  -H "Content-Type: application/json" \
  -d '{"salary": 80000}'

# Delete employee
curl -X DELETE http://localhost:8080/api/employees/1
```

## 🐳 Docker Commands

### Build Docker Image
```bash
docker build -t employee-api:latest .
```

### Run Docker Container
```bash
docker run -d -p 8080:8080 --name employee-api employee-api:latest
```

### View Docker Logs
```bash
docker logs -f employee-api
```

### Stop and Remove Container
```bash
docker stop employee-api
docker rm employee-api
```

## 🚨 Troubleshooting

### Port already in use
```bash
# Find process on port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>
```

### Docker Compose not starting
```bash
# Clean and rebuild
docker-compose down
docker-compose up --build
```

### Maven build fails
```bash
# Clean Maven cache
mvn clean

# Rebuild
mvn install
```

## 📚 API Documentation

### Response Status Codes
- `200 OK` - Successful GET or PUT request
- `201 Created` - Successful POST request
- `204 No Content` - Successful DELETE request
- `400 Bad Request` - Invalid input
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

## 🔮 Future Enhancements

- [ ] Add authentication and authorization (JWT)
- [ ] Implement pagination and sorting
- [ ] Add input validation constraints
- [ ] Comprehensive exception handling
- [ ] Unit and integration tests
- [ ] Swagger/OpenAPI documentation
- [ ] MySQL/PostgreSQL support
- [ ] Logging with ELK stack
- [ ] API rate limiting
- [ ] Kubernetes deployment

## 📄 License

MIT License

## 👨‍💻 Author

Created by ihabsag

---

**Happy Coding! 🎉**