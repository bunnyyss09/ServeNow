# 🚀 ServeNow API Testing Guide

## Quick Start - Test the Platform in 5 Minutes

### Step 1: Verify API is Running
```bash
# Test basic health check
curl http://localhost:8080/health
```
**Expected**: Welcome message with API information

### Step 2: Login as Admin (Pre-configured User)
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@servenow.com",
    "password": "admin123"
  }'
```
**Expected**: JWT tokens in response
**Copy the `accessToken` for next steps**

### Step 3: Test Protected Endpoint
```bash
curl -X GET "http://localhost:8080/users/profile" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE"
```
**Replace `YOUR_ACCESS_TOKEN_HERE` with the token from Step 2**

### Step 4: Register a New Customer
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@test.com",
    "password": "TestPass123!",
    "confirmPassword": "TestPass123!",
    "userType": "CUSTOMER",
    "phoneNumber": "+919876543210",
    "city": "Mumbai",
    "country": "India"
  }'
```

### Step 5: Browse Categories
```bash
curl http://localhost:8080/categories
```

## 🎯 Core Testing Workflows

### 1. User Management Flow
1. **Register** → `/auth/register`
2. **Login** → `/auth/login` 
3. **View Profile** → `/users/profile`
4. **Update Profile** → `/users/profile` (PUT)

### 2. Service Provider Flow
1. Register as PROVIDER (set `"userType": "PROVIDER"`)
2. Login → copy `accessToken`
3. Authorize in Swagger (paste token without `Bearer `)
4. Create Service → `/services` (POST)
5. View My Services → `/services/provider/{providerId}`
6. Update Service → `/services/{serviceId}` (PUT)

### 3. Customer Booking Flow  
1. Browse Categories → `/categories`
2. Search Services → `/search?q=cleaning`
3. Create Booking → `/bookings` (POST)
4. View My Bookings → `/bookings/customer`

### 4. Review System Flow
1. Complete a booking
2. Write Review → `/reviews` (POST)
3. View Service Reviews → `/reviews/service/{serviceId}`

## 🛠️ Swagger UI Access
For interactive testing: **http://localhost:8080/swagger-ui.html**

Authorization steps:
- Click "Authorize" and paste the raw JWT access token (no `Bearer ` prefix)
- Ensure the request shows header `Authorization: Bearer <token>`

## 📋 Pre-configured Test Data
- **Admin User**: admin@servenow.com / admin123
- **Categories**: Home Services, Health & Wellness, Education, Technology
- **Currency**: All prices in Indian Rupees (₹)

Sample provider payload (registration):
```json
{
  "firstName": "Alex",
  "lastName": "Provider",
  "email": "provider125@example.com",
  "password": "StrongPass!234",
  "confirmPassword": "StrongPass!234",
  "phoneNumber": "+15550003",
  "userType": "PROVIDER",
  "city": "NYC",
  "country": "USA"
}
```

Sample create service payload (POST /services):
```json
{
  "title": "House Cleaning",
  "description": "Thorough home cleaning service",
  "categoryId": 1,
  "basePrice": 120.0,
  "priceUnit": "per service",
  "estimatedDurationMinutes": 180,
  "isAvailable": true
}
```

## 🔧 Common Issues & Solutions

### Port Already in Use
```bash
# Kill existing process
taskkill /F /PID [PROCESS_ID]
# Or change port in application.yaml
```

### MySQL Connection Issues
1. Ensure MySQL is running on port 3306
2. Verify credentials in `application.yaml`
3. Database `ServeNow` will be created automatically

### JWT Token Expired
- Access tokens expire in 24 hours
- Use refresh token or login again

## 🎯 Success Criteria Checklist
- [ ] Health check returns 200 OK
- [ ] Admin login successful with JWT tokens
- [ ] Profile access with token works
- [ ] New user registration works
- [ ] Categories API returns data
- [ ] Services can be created by providers
- [ ] Bookings can be created by customers
- [ ] Reviews can be submitted

---
**Need Help?** Check the Swagger UI for detailed API documentation and try the interactive endpoints.