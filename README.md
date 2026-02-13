# Trading Platform - User Management API

## Overview
This is a Spring Boot application for managing users in a trading platform with REST APIs and Swagger documentation.

## Prerequisites
- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

## Project Structure
```
src/main/java/com/example/tradingbackend/
├── config/
│   └── SwaggerConfig.java          # Swagger/OpenAPI configuration
├── controller/
│   └── UserController.java         # REST API endpoints for user management
├── dto/
│   ├── UserRegistrationRequest.java # Request DTO for user registration
│   └── UserResponse.java           # Response DTO for user data
├── exception/
│   ├── DuplicateResourceException.java
│   ├── ResourceNotFoundException.java
│   └── GlobalExceptionHandler.java # Global exception handling
├── model/
│   └── User.java                   # User entity
├── repository/
│   └── UserRepository.java         # JPA repository for User
└── service/
    └── UserService.java            # Business logic for user operations
```

## Database Setup

### 1. Install MySQL
Make sure MySQL is installed and running on your system.

### 2. Create Database (Optional)
The application will automatically create the database if it doesn't exist. If you want to create it manually:

```sql
CREATE DATABASE trading_db;
```

### 3. Configure Database Connection
Update the following properties in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/trading_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root  # Change this to your MySQL password
```

## Running the Application

### 1. Build the Project
```bash
mvnw clean install
```

### 2. Run the Application
```bash
mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

## API Documentation

### Swagger UI
Once the application is running, access the Swagger UI at:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

## Available Endpoints

### User Management APIs

#### 1. Register a New User
- **Endpoint**: `POST /api/users/register`
- **Request Body**:
```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123",
  "fullName": "John Doe",
  "phoneNumber": "+1234567890"
}
```
- **Response**: `201 Created`
```json
{
  "id": 1,
  "username": "johndoe",
  "email": "john@example.com",
  "fullName": "John Doe",
  "phoneNumber": "+1234567890",
  "active": true,
  "createdAt": "2026-02-12T10:30:00",
  "updatedAt": "2026-02-12T10:30:00"
}
```

#### 2. Get All Users
- **Endpoint**: `GET /api/users`
- **Response**: `200 OK`
```json
[
  {
    "id": 1,
    "username": "johndoe",
    "email": "john@example.com",
    "fullName": "John Doe",
    "phoneNumber": "+1234567890",
    "active": true,
    "createdAt": "2026-02-12T10:30:00",
    "updatedAt": "2026-02-12T10:30:00"
  }
]
```

#### 3. Get User by ID
- **Endpoint**: `GET /api/users/{id}`
- **Response**: `200 OK`

#### 4. Get User by Username
- **Endpoint**: `GET /api/users/username/{username}`
- **Response**: `200 OK`

#### 5. Delete User
- **Endpoint**: `DELETE /api/users/{id}`
- **Response**: `204 No Content`

## Testing with cURL

### Register a User
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "password123",
    "fullName": "John Doe",
    "phoneNumber": "+1234567890"
  }'
```

### Get All Users
```bash
curl http://localhost:8080/api/users
```

### Get User by ID
```bash
curl http://localhost:8080/api/users/1
```

### Delete User
```bash
curl -X DELETE http://localhost:8080/api/users/1
```

## Error Handling

The API returns appropriate HTTP status codes:
- `200 OK` - Successful GET request
- `201 Created` - Successful POST request
- `204 No Content` - Successful DELETE request
- `400 Bad Request` - Validation errors
- `404 Not Found` - Resource not found
- `409 Conflict` - Duplicate username or email
- `500 Internal Server Error` - Server error

## Validation Rules

- **Username**: Required, 3-50 characters, must be unique
- **Email**: Required, valid email format, max 100 characters, must be unique
- **Password**: Required, minimum 6 characters
- **Full Name**: Optional, max 100 characters
- **Phone Number**: Optional, max 20 characters

## Features

✅ User Registration with validation  
✅ Get all users  
✅ Get user by ID  
✅ Get user by username  
✅ Delete user  
✅ MySQL database integration  
✅ Swagger/OpenAPI documentation  
✅ Global exception handling  
✅ Request/Response DTOs  
✅ Automatic timestamps (createdAt, updatedAt)  

## Technologies Used

- **Spring Boot 4.0.2**
- **Spring Data JPA**
- **MySQL 8.0+**
- **Lombok**
- **SpringDoc OpenAPI (Swagger)**
- **Bean Validation**

## Security Note

⚠️ **Important**: The current implementation stores passwords in plain text. In a production environment, you should:
1. Hash passwords using BCrypt or similar
2. Implement authentication (JWT, OAuth2, etc.)
3. Add authorization and role-based access control
4. Use HTTPS for all communications

## Next Steps

Consider adding:
- Password encryption
- JWT authentication
- User update endpoint
- User profile management
- Role-based access control
- Email verification
- Password reset functionality

## License

This project is for educational/demonstration purposes.
