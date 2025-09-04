$body = @'
{
  "firstName": "John",
  "lastName": "Doe", 
  "email": "john@test.com",
  "password": "TestPass123!",
  "confirmPassword": "TestPass123!",
  "userType": "CUSTOMER",
  "phoneNumber": "+91-1234567890",
  "city": "Mumbai",
  "country": "India"
}
'@

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method POST -Body $body -ContentType "application/json"
    Write-Host "SUCCESS: User registered successfully!" -ForegroundColor Green
    Write-Host "User: $($response.data.userResponse.fullName)" -ForegroundColor Cyan
} catch {
    Write-Host "REGISTRATION FAILED" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}