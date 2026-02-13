# 🚀 Quick Start Guide

## Prerequisites Check
- [ ] Java 17+ installed
- [ ] MySQL 8.0+ installed and running
- [ ] Maven installed (or use included mvnw)

## Step 1: Configure Database

1. Start MySQL server
2. Open `src/main/resources/application.properties`
3. Update credentials if needed:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```

## Step 2: Build the Project

```bash
.\mvnw.cmd clean install
```

## Step 3: Run the Application

```bash
.\mvnw.cmd spring-boot:run
```

You should see:
```
Started TradingBackendApplication in X.XXX seconds
```

## Step 4: Access Swagger UI

Open your browser and go to:
```
http://localhost:8080/swagger-ui.html
```

## Step 5: Test the APIs

### Using Swagger UI (Recommended for beginners)

1. Click on **User Management** section
2. Click on **POST /api/users/register**
3. Click **Try it out**
4. Enter this sample data:
   ```json
   {
     "username": "testuser",
     "email": "test@example.com",
     "password": "password123",
     "fullName": "Test User",
     "phoneNumber": "+1234567890"
   }
   ```
5. Click **Execute**
6. Check the response - you should see 201 Created

### Using PowerShell/cURL

**Register a user:**
```powershell
Invoke-RestMethod -Uri http://localhost:8080/api/users/register -Method POST -ContentType "application/json" -Body '{
  "username": "trader1",
  "email": "trader1@example.com",
  "password": "secure123",
  "fullName": "Trader One",
  "phoneNumber": "+1234567890"
}'
```

**Get all users:**
```powershell
Invoke-RestMethod -Uri http://localhost:8080/api/users -Method GET
```

**Get user by ID:**
```powershell
Invoke-RestMethod -Uri http://localhost:8080/api/users/1 -Method GET
```

## Step 6: Verify Database

1. Connect to MySQL:
   ```bash
   mysql -u root -p
   ```

2. Check the database:
   ```sql
   USE trading_db;
   SHOW TABLES;
   SELECT * FROM users;
   ```

## Troubleshooting

### Port 8080 already in use
Change the port in `application.properties`:
```properties
server.port=8081
```

### Cannot connect to database
- Check if MySQL is running
- Verify credentials
- Check if port 3306 is accessible

### Build fails
```bash
.\mvnw.cmd clean install -U
```

## What's Working?

✅ User Registration with validation  
✅ Get all users  
✅ Get user by ID  
✅ Get user by username  
✅ Delete user  
✅ Swagger documentation  
✅ MySQL integration  
✅ Exception handling  

## Next Steps

- Add more entities (Portfolio, Transaction, Stock)
- Implement authentication (JWT)
- Add password encryption
- Create trading endpoints
- Add user update functionality

## Need Help?

Check the README.md for detailed documentation!
