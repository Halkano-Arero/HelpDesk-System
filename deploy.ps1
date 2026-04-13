$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$tomcatRoot = 'C:\Program Files\apache-tomcat-9.0.98'
$contextName = 'helpdesk'
$warPath = Join-Path $projectRoot "target\$contextName.war"
$deployWar = Join-Path $tomcatRoot "webapps\$contextName.war"
$explodedApp = Join-Path $tomcatRoot "webapps\$contextName"
$loginUrl = "http://localhost:8080/$contextName/login.jsp"

if (-not (Test-Path $tomcatRoot)) {
    throw "Tomcat was not found at $tomcatRoot. Update `$tomcatRoot in deploy.ps1 to match your installation."
}

Push-Location $projectRoot
try {
    Write-Host 'Building the WAR...'
    mvn -o clean package -DskipTests

    Write-Host 'Stopping Tomcat...'
    & (Join-Path $tomcatRoot 'bin\shutdown.bat') | Out-Null
    Start-Sleep -Seconds 5

    if (Test-Path $explodedApp) {
        Remove-Item $explodedApp -Recurse -Force
    }

    if (Test-Path $deployWar) {
        Remove-Item $deployWar -Force
    }

    Copy-Item $warPath $deployWar -Force

    Write-Host 'Starting Tomcat...'
    & (Join-Path $tomcatRoot 'bin\startup.bat') | Out-Null

    Start-Sleep -Seconds 10
    Start-Process $loginUrl
    Write-Host "Deployed successfully. Opened $loginUrl"
}
finally {
    Pop-Location
}
