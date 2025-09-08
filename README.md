# ServeNow

> Local Service Marketplace Platform - Connecting customers with service providers

## Overview
RESTful API backend for a service marketplace platform similar to UrbanClap/TaskRabbit, built with Spring Boot.

## Quick Start
```bash
# Clone and run
mvn spring-boot:run
```

Then open Swagger UI: `http://localhost:8080/swagger-ui.html`

Authorize with JWT:
- Login via `POST /auth/login`
- Copy `accessToken`
- In Swagger, click Authorize and paste only the token (no `Bearer `)

## Access Points
- **API Base URL:** `http://localhost:8080`
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **API Docs:** `http://localhost:8080/api-docs`

Admin credentials for testing:
- Email: `admin@servenow.com`
- Password: `admin123`

## Core Features
- JWT Authentication & Role-based Authorization
- Service Provider & Customer Management
- Booking System with Real-time Status Tracking
- Review & Rating System
- Geolocation-based Service Discovery

## Tech Stack
Spring Boot 3.5.5 • MySQL 8.0 • JWT • Spring Security • Maven • Java 24

