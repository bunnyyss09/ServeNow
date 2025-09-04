# ServeNow API Documentation

## Overview
ServeNow is a Local Service Finder platform that connects customers with service providers. Similar to UrbanClap/TaskRabbit.

## Base URL
```
http://localhost:8080
```

## Authentication
Most endpoints require JWT token authentication. Include the token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## API Endpoints

### 🏠 Home
- `GET /` - API health check and information

### 🔐 Authentication
- `POST /auth/register` - Register new user (customer/provider)
- `POST /auth/login` - Login user
- `GET /auth/profile` - Get current user profile
- `PUT /auth/profile` - Update user profile

### 🏷️ Categories
- `GET /categories` - Get all categories
- `GET /categories/top-level` - Get top-level categories
- `GET /categories/{categoryId}/subcategories` - Get subcategories
- `GET /categories/{categoryId}` - Get category by ID
- `GET /categories/slug/{slug}` - Get category by slug

### 🛠️ Services
- `GET /services` - Get all services (paginated)
- `GET /services/category/{categoryId}` - Get services by category
- `GET /services/search?q={query}` - Search services
- `GET /services/featured` - Get featured services
- `GET /services/{serviceId}` - Get service by ID
- `GET /services/slug/{slug}` - Get service by slug
- `GET /services/provider/{providerId}` - Get services by provider
- `POST /services` - Create service (Provider only)
- `PUT /services/{serviceId}` - Update service (Provider only)
- `DELETE /services/{serviceId}` - Delete service (Provider only)

### 📅 Bookings
- `POST /bookings` - Create booking (Customer only)
- `GET /bookings/customer` - Get customer bookings
- `GET /bookings/provider` - Get provider bookings
- `GET /bookings/{bookingId}` - Get booking details
- `PUT /bookings/{bookingId}/accept` - Accept booking (Provider only)
- `PUT /bookings/{bookingId}/reject` - Reject booking (Provider only)
- `PUT /bookings/{bookingId}/complete` - Complete booking (Provider only)
- `PUT /bookings/{bookingId}/cancel` - Cancel booking (Customer only)

### ⭐ Reviews
- `POST /reviews` - Create review (Customer only)
- `GET /reviews/service/{serviceId}` - Get service reviews
- `GET /reviews/provider/{providerId}` - Get provider reviews
- `GET /reviews/customer` - Get customer reviews
- `GET /reviews/{reviewId}` - Get review details

### 🔍 Search
- `GET /search` - Enhanced search with filters
- `GET /search/featured` - Get featured services
- `GET /search/popular` - Get popular services

## Data Models

### User Registration
```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "phone": "+91-9876543210",
  "userType": "CUSTOMER",
  "address": "123 Main St, City",
  "latitude": 12.9716,
  "longitude": 77.5946
}
```

### Service Creation
```json
{
  "title": "Home Cleaning Service",
  "description": "Professional home cleaning",
  "categoryId": 1,
  "basePrice": 500.00,
  "priceUnit": "PER_HOUR",
  "estimatedDurationMinutes": 120,
  "serviceArea": "Bangalore",
  "isAvailable": true
}
```

### Booking Creation
```json
{
  "serviceId": 1,
  "scheduledAt": "2024-01-15T10:00:00",
  "notes": "Please bring cleaning supplies",
  "serviceAddress": "123 Home St, City"
}
```

### Review Creation
```json
{
  "bookingId": 1,
  "rating": 5,
  "comment": "Excellent service!"
}
```

## Status Codes
- `200` - Success
- `201` - Created
- `400` - Bad Request
- `401` - Unauthorized
- `403` - Forbidden
- `404` - Not Found
- `500` - Internal Server Error

## Currency
All prices are in Indian Rupees (₹).

## Swagger Documentation
Visit `/swagger-ui.html` for interactive API documentation.