Write-Host "Verifying Database Setup..." -ForegroundColor Green

# First, let's check if the server is responding
Write-Host "`nChecking server status..." -ForegroundColor Cyan
try {
    $health = Invoke-RestMethod -Uri "http://localhost:8080/health"
    Write-Host "SUCCESS: Server is running" -ForegroundColor Green
} catch {
    Write-Host "FAILED: Server is not responding" -ForegroundColor Red
    exit
}

# Test registration endpoint to see if database is working
Write-Host "`nTesting database via registration (to see errors)..." -ForegroundColor Cyan
$testUser = @{
    firstName = "Test"
    lastName = "User"
    email = "test$(Get-Date -Format 'yyyyMMddHHmmss')@test.com"
    password = "TestPassword123!"
    confirmPassword = "TestPassword123!"
    userType = "CUSTOMER"
    phoneNumber = "+1-555-9999"
    city = "Test City"
    country = "Test Country"
} | ConvertTo-Json

try {
    $regResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method POST -Body $testUser -ContentType "application/json"
    Write-Host "SUCCESS: Registration works - Database is accessible" -ForegroundColor Green
    Write-Host "Created user: $($regResponse.data.userResponse.fullName)" -ForegroundColor Gray
} catch {
    Write-Host "FAILED: Registration failed" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Yellow
    
    if ($_.Exception.Response) {
        try {
            $stream = $_.Exception.Response.GetResponseStream()
            $reader = New-Object System.IO.StreamReader($stream)
            $errorBody = $reader.ReadToEnd()
            Write-Host "Response: $errorBody" -ForegroundColor Yellow
        } catch {
            Write-Host "Could not read error details" -ForegroundColor Yellow
        }
    }
}

# Now test admin login specifically
Write-Host "`nTesting admin login specifically..." -ForegroundColor Cyan
$adminLogin = @{
    email = "admin@servenow.com"
    password = "admin123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -Body $adminLogin -ContentType "application/json"
    Write-Host "SUCCESS: Admin login works!" -ForegroundColor Green
    Write-Host "Admin user exists and password is correct" -ForegroundColor Gray
} catch {
    Write-Host "FAILED: Admin login failed" -ForegroundColor Red
    Write-Host "This suggests either:" -ForegroundColor Yellow
    Write-Host "  1. Admin user doesn't exist in database" -ForegroundColor Yellow
    Write-Host "  2. Password doesn't match" -ForegroundColor Yellow
    Write-Host "  3. Database initialization script didn't run" -ForegroundColor Yellow
}

Write-Host "`nDatabase verification complete." -ForegroundColor Green