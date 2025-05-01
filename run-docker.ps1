# Load environment variables from .env (must be in the same directory, see env.example)
$envFile = ".env"

if (Test-Path $envFile) {
    Get-Content $envFile | ForEach-Object {
        if ($_ -match "^\s*#") { return }  # Skip comments
        if ($_ -match "^\s*$") { return }  # Skip empty lines
        $name, $value = $_ -split '=', 2
        $env:$name = $value
    }
} else {
    Write-Host "Environment file 'env' not found. Exiting..." -ForegroundColor Red
    exit 1
}

# Move to the web module folder
Set-Location -Path "prompt-manager-web"

# Run Spring Boot with 'docker' profile
mvn "-Dspring-boot.run.profiles=docker" spring-boot:run
