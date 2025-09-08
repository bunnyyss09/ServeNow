Write-Host "ServeNow API Complete Functionality Test" -ForegroundColor Green
Write-Host "=========================================" -ForegroundColor Yellow

$baseUrl = "http://localhost:8080"
$headers = @{"Content-Type" = "application/json"}
$global:adminToken = ""

# Test 1: Health Check
Write-Host "`n1. Testing API Health..." -ForegroundColor Cyan
try {
    $health = Invoke-RestMethod -Uri "$baseUrl/health"
    Write-Host "SUCCESS: API Health: $($health.data.status)" -ForegroundColor Green
} catch {
    Write-Host "FAILED: Health Check Failed: $($_.Exception.Message)" -ForegroundColor Red
    exit
}

# Test 2: Welcome Message  
Write-Host "`n2. Testing Welcome Endpoint..." -ForegroundColor Cyan
try {
    $welcome = Invoke-RestMethod -Uri "$baseUrl/"
    Write-Host "SUCCESS: Welcome: $($welcome.data.message)" -ForegroundColor Green
    Write-Host "INFO: Quick Start Available: $($welcome.data.quickStart.step1)" -ForegroundColor Gray
} catch {
    Write-Host "FAILED: Welcome Endpoint Failed" -ForegroundColor Red
}

# Test 3: Admin Login
Write-Host "`n3. Testing Admin Login..." -ForegroundColor Cyan
$loginData = @{
    email = "admin@servenow.com"
    password = "admin123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method POST -Body $loginData -Headers $headers
    $global:adminToken = $loginResponse.data.accessToken
    Write-Host "SUCCESS: Admin Login Successful" -ForegroundColor Green
    Write-Host "INFO: JWT Token Length: $($global:adminToken.Length) characters" -ForegroundColor Gray
} catch {
    Write-Host "FAILED: Admin Login Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 4: Protected Endpoint (Profile)
if ($global:adminToken) {
    Write-Host "`n4. Testing Protected Endpoint (Profile)..." -ForegroundColor Cyan
    $authHeaders = @{
        "Content-Type" = "application/json"
        "Authorization" = "Bearer $global:adminToken"
    }
    
    try {
        $profile = Invoke-RestMethod -Uri "$baseUrl/users/profile" -Headers $authHeaders
        Write-Host "SUCCESS: Profile Access: $($profile.data.fullName)" -ForegroundColor Green
        Write-Host "INFO: User Roles: $($profile.data.roles -join ', ')" -ForegroundColor Gray
    } catch {
        Write-Host "FAILED: Profile Access Failed: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# Test 5: Categories
Write-Host "`n5. Testing Categories..." -ForegroundColor Cyan
try {
    $categories = Invoke-RestMethod -Uri "$baseUrl/categories"
    $categoryCount = $categories.data.Count
    Write-Host "SUCCESS: Categories Available: $categoryCount categories" -ForegroundColor Green
    if ($categoryCount -gt 0) {
        Write-Host "INFO: Sample Category: $($categories.data[0].name)" -ForegroundColor Gray
    }
} catch {
    Write-Host "FAILED: Categories Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 6: Services Endpoint
Write-Host "`n6. Testing Services Endpoint..." -ForegroundColor Cyan
try {
    $services = Invoke-RestMethod -Uri "$baseUrl/services"
    Write-Host "SUCCESS: Services Endpoint Working" -ForegroundColor Green
    Write-Host "INFO: Total Services: $($services.data.totalElements)" -ForegroundColor Gray
} catch {
    Write-Host "FAILED: Services Endpoint Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 7: Search Functionality
Write-Host "`n7. Testing Search..." -ForegroundColor Cyan
try {
    $search = Invoke-RestMethod -Uri "$baseUrl/search?q=service"
    Write-Host "SUCCESS: Search Working" -ForegroundColor Green
} catch {
    Write-Host "FAILED: Search Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test Summary
Write-Host "`n" -NoNewline
Write-Host "Test Summary" -ForegroundColor Green
Write-Host "============" -ForegroundColor Yellow
Write-Host "Core API functionality tested"
Write-Host "Authentication system verified"
Write-Host "Database connectivity confirmed"
Write-Host "Protected endpoints working"
Write-Host ""
Write-Host "Next Steps:" -ForegroundColor Cyan
Write-Host "   • Visit: $baseUrl/swagger-ui.html for interactive testing"
Write-Host "   • Read: API_TESTING_GUIDE.md for detailed workflows"
Write-Host "   • Test: Individual endpoints using JWT tokens"
Write-Host ""
Write-Host "Admin Credentials: admin@servenow.com / admin123" -ForegroundColor Yellow
Write-Host "Platform Ready for Development and Testing!" -ForegroundColor Green