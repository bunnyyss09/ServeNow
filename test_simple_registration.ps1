Write-Host "Testing User Registration..." -ForegroundColor Green

$regData = @{
    firstName = "John"
    lastName = "Doe"
    email = "john.doe@test.com"
    password = "TestPassword123!"
    confirmPassword = "TestPassword123!"
    userType = "CUSTOMER"
    phoneNumber = "+91-9876543210"
    city = "Mumbai"
    country = "India"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method POST -Body $regData -ContentType "application/json"
    Write-Host "✓ Registration successful!" -ForegroundColor Green
    Write-Host "User: $($response.data.userResponse.fullName)" -ForegroundColor Cyan
    Write-Host "Email: $($response.data.userResponse.email)" -ForegroundColor Cyan
    Write-Host "Roles: $($response.data.userResponse.roles)" -ForegroundColor Cyan
} catch {
    Write-Host "✗ Registration failed" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $stream = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($stream)
        $errorBody = $reader.ReadToEnd()
        Write-Host "Response Body: `$errorBody" -ForegroundColor Yellow
    }
}