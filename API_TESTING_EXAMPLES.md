# 🧪 API Testing Examples

## Using Swagger UI (Easiest Method)

### Step 1: Start the Application
```bash
.\mvnw.cmd spring-boot:run
```

### Step 2: Open Swagger UI
Navigate to: **http://localhost:8080/swagger-ui.html**

### Step 3: Test Each Endpoint

#### 1️⃣ Register a New User
1. Expand **POST /api/users/register**
2. Click **Try it out**
3. Paste this JSON:
```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123",
  "fullName": "John Doe",
  "phoneNumber": "+1234567890"
}
```
4. Click **Execute**
5. ✅ You should see **201 Created**

#### 2️⃣ Get All Users
1. Expand **GET /api/users**
2. Click **Try it out**
3. Click **Execute**
4. ✅ You should see the user you just created

#### 3️⃣ Get User by ID
1. Expand **GET /api/users/{id}**
2. Click **Try it out**
3. Enter **1** in the id field
4. Click **Execute**
5. ✅ You should see the user details

#### 4️⃣ Get User by Username
1. Expand **GET /api/users/username/{username}**
2. Click **Try it out**
3. Enter **johndoe** in the username field
4. Click **Execute**
5. ✅ You should see the user details

#### 5️⃣ Delete User
1. Expand **DELETE /api/users/{id}**
2. Click **Try it out**
3. Enter **1** in the id field
4. Click **Execute**
5. ✅ You should see **204 No Content**

---

## Using PowerShell

### 1. Register a User
```powershell
$headers = @{
    "Content-Type" = "application/json"
}

$body = @{
    username = "trader1"
    email = "trader1@example.com"
    password = "secure123"
    fullName = "Trader One"
    phoneNumber = "+1234567890"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/users/register" -Method POST -Headers $headers -Body $body
```

**Expected Output:**
```
id          : 1
username    : trader1
email       : trader1@example.com
fullName    : Trader One
phoneNumber : +1234567890
active      : True
createdAt   : 2026-02-12T15:30:00
updatedAt   : 2026-02-12T15:30:00
```

### 2. Get All Users
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/users" -Method GET
```

### 3. Get User by ID
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/users/1" -Method GET
```

### 4. Get User by Username
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/users/username/trader1" -Method GET
```

### 5. Delete User
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/users/1" -Method DELETE
```

---

## Using cURL (Git Bash or WSL)

### 1. Register a User
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "trader1",
    "email": "trader1@example.com",
    "password": "secure123",
    "fullName": "Trader One",
    "phoneNumber": "+1234567890"
  }'
```

### 2. Get All Users
```bash
curl -X GET http://localhost:8080/api/users
```

### 3. Get User by ID
```bash
curl -X GET http://localhost:8080/api/users/1
```

### 4. Get User by Username
```bash
curl -X GET http://localhost:8080/api/users/username/trader1
```

### 5. Delete User
```bash
curl -X DELETE http://localhost:8080/api/users/1
```

---

## Testing Error Scenarios

### Test 1: Duplicate Username
```powershell
# Register first user
$body1 = @{
    username = "testuser"
    email = "test1@example.com"
    password = "password123"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/users/register" -Method POST -ContentType "application/json" -Body $body1

# Try to register with same username (should fail)
$body2 = @{
    username = "testuser"
    email = "test2@example.com"
    password = "password123"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/users/register" -Method POST -ContentType "application/json" -Body $body2
```

**Expected:** ❌ **409 Conflict** - "Username already exists: testuser"

### Test 2: Invalid Email
```powershell
$body = @{
    username = "testuser"
    email = "invalid-email"
    password = "password123"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/users/register" -Method POST -ContentType "application/json" -Body $body
```

**Expected:** ❌ **400 Bad Request** - "Email should be valid"

### Test 3: Short Password
```powershell
$body = @{
    username = "testuser"
    email = "test@example.com"
    password = "123"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/users/register" -Method POST -ContentType "application/json" -Body $body
```

**Expected:** ❌ **400 Bad Request** - "Password must be at least 6 characters"

### Test 4: User Not Found
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/users/999" -Method GET
```

**Expected:** ❌ **404 Not Found** - "User not found with id: 999"

---

## Testing Multiple Users

### Create Multiple Users Script
```powershell
# Create array of users
$users = @(
    @{ username = "alice"; email = "alice@example.com"; password = "password1"; fullName = "Alice Smith" },
    @{ username = "bob"; email = "bob@example.com"; password = "password2"; fullName = "Bob Johnson" },
    @{ username = "charlie"; email = "charlie@example.com"; password = "password3"; fullName = "Charlie Brown" }
)

# Register each user
foreach ($user in $users) {
    $body = $user | ConvertTo-Json
    Write-Host "Registering $($user.username)..."
    Invoke-RestMethod -Uri "http://localhost:8080/api/users/register" -Method POST -ContentType "application/json" -Body $body
}

# Get all users
Write-Host "`nAll registered users:"
Invoke-RestMethod -Uri "http://localhost:8080/api/users" -Method GET
```

---

## Verify in Database

### Connect to MySQL
```bash
mysql -u root -p
```

### Check the Data
```sql
-- Switch to database
USE trading_db;

-- View all tables
SHOW TABLES;

-- View all users
SELECT * FROM users;

-- Count users
SELECT COUNT(*) as total_users FROM users;

-- View specific user
SELECT * FROM users WHERE username = 'trader1';

-- View users by email domain
SELECT * FROM users WHERE email LIKE '%@example.com';

-- Check active users
SELECT username, email, active FROM users WHERE active = TRUE;
```

---

## Performance Testing

### Test 1: Register 100 Users
```powershell
for ($i=1; $i -le 100; $i++) {
    $body = @{
        username = "user$i"
        email = "user$i@example.com"
        password = "password$i"
        fullName = "User $i"
    } | ConvertTo-Json
    
    Invoke-RestMethod -Uri "http://localhost:8080/api/users/register" -Method POST -ContentType "application/json" -Body $body
    Write-Host "Created user $i"
}
```

### Test 2: Retrieve All Users
```powershell
Measure-Command {
    Invoke-RestMethod -Uri "http://localhost:8080/api/users" -Method GET
}
```

---

## Common Issues & Solutions

### Issue 1: Connection Refused
**Error:** `Connection refused`
**Solution:** Make sure the application is running
```bash
.\mvnw.cmd spring-boot:run
```

### Issue 2: Database Connection Error
**Error:** `Access denied for user 'root'@'localhost'`
**Solution:** Update credentials in `application.properties`

### Issue 3: Port Already in Use
**Error:** `Port 8080 is already in use`
**Solution:** Change port in `application.properties`:
```properties
server.port=8081
```

### Issue 4: Table Not Found
**Error:** `Table 'trading_db.users' doesn't exist`
**Solution:** 
1. Make sure MySQL is running
2. Check `spring.jpa.hibernate.ddl-auto=update` in application.properties
3. Restart the application

---

## Expected Response Formats

### Success Response (201 Created)
```json
{
  "id": 1,
  "username": "trader1",
  "email": "trader1@example.com",
  "fullName": "Trader One",
  "phoneNumber": "+1234567890",
  "active": true,
  "createdAt": "2026-02-12T15:30:00",
  "updatedAt": "2026-02-12T15:30:00"
}
```

### Validation Error (400 Bad Request)
```json
{
  "status": 400,
  "errors": {
    "username": "Username must be between 3 and 50 characters",
    "email": "Email should be valid"
  },
  "timestamp": "2026-02-12T15:30:00"
}
```

### Not Found Error (404)
```json
{
  "status": 404,
  "message": "User not found with id: 123",
  "timestamp": "2026-02-12T15:30:00"
}
```

### Conflict Error (409)
```json
{
  "status": 409,
  "message": "Username already exists: trader1",
  "timestamp": "2026-02-12T15:30:00"
}
```

---

## Quick Test Checklist

- [ ] Application starts without errors
- [ ] Swagger UI is accessible
- [ ] Can register a new user
- [ ] Can get all users
- [ ] Can get user by ID
- [ ] Can get user by username
- [ ] Can delete a user
- [ ] Duplicate username is rejected
- [ ] Invalid email is rejected
- [ ] Short password is rejected
- [ ] Database contains the data

---

**Happy Testing! 🎉**
